from .models import Item, Order
from django.shortcuts import get_object_or_404
import json
import uuid
from django.http import JsonResponse
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.decorators import api_view
from .models import Order, Item
from .serializers import OrderSerializer


class OrderCreateView(APIView):
    def post(self, request, *args, **kwargs):
        item_uuids = request.data.get('item_uuids', [])

        if not item_uuids or not isinstance(item_uuids, list):
            return Response(
                {"error": "Invalid data. 'item_uuids' must be a list of UUIDs."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Fetch Items matching the provided UUIDs
        items = Item.objects.filter(uuid__in=item_uuids)

        if not items.exists():
            return Response(
                {"error": "No valid items found for the provided UUIDs."},
                status=status.HTTP_404_NOT_FOUND,
            )

        # Check if any of the items already belong to an order
        if items.filter(order__isnull=False).exists():
            return Response(
                {"error": "One or more items already belong to an order."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Create the Order
        order = Order.objects.create(type="forward")

        # Update the order field of each item to the newly created order
        items.update(order=order)

        # Serialize the order response
        serializer = OrderSerializer(order)
        return Response(serializer.data, status=status.HTTP_201_CREATED)


class UpdateOrderTypeToReturnView(APIView):
    def post(self, request, *args, **kwargs):
        """
        Expects a JSON payload like:
        {
            "item_uuids": ["uuid1", "uuid2", "uuid3"]
        }
        """
        # Validate input
        uuids = request.data.get("item_uuids", [])
        if not uuids or not isinstance(uuids, list):
            return Response(
                {"error": "Invalid data. 'item_uuids' must be a list of UUIDs."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Fetch items matching the provided UUIDs
        items = list(Item.objects.filter(
            uuid__in=uuids).select_related("order"))

        if not items:
            return Response(
                {"error": "No valid items found for the provided UUIDs."},
                status=status.HTTP_404_NOT_FOUND,
            )

        # Extract the order from the items
        orders = {item.order for item in items if item.order}

        if len(orders) != 1:
            return Response(
                {"error": "Items do not belong to the same order."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Get the single order
        order = orders.pop()

        # Check if all items for the order are included in the request
        all_order_item_uuids = set(
            Item.objects.filter(order=order).values_list("uuid", flat=True)
        )
        provided_item_uuids = {item.uuid for item in items}

        if provided_item_uuids != all_order_item_uuids:
            return Response(
                {"error": "Not all items for this order are provided."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Check if the order type is 'forward'
        if order.type != "forward":
            return Response(
                {"error": "Order type is not 'forward'. No update performed."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Update the order type to 'return'
        order.type = "return"
        order.save()

        return Response(
            {"message": f"Order {order.id} updated to 'return'."},
            status=status.HTTP_200_OK,
        )


class UpdateOrderTypeToCompletedView(APIView):
    def post(self, request, *args, **kwargs):
        """
        Expects a JSON payload like:
        {
            "item_uuids": ["uuid1", "uuid2", "uuid3"]
        }
        """
        # Validate input
        uuids = request.data.get("item_uuids", [])
        if not uuids or not isinstance(uuids, list):
            return Response(
                {"error": "Invalid data. 'item_uuids' must be a list of UUIDs."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Fetch items matching the provided UUIDs
        items = list(Item.objects.filter(
            uuid__in=uuids).select_related("order"))

        if not items:
            return Response(
                {"error": "No valid items found for the provided UUIDs."},
                status=status.HTTP_404_NOT_FOUND,
            )

        # Extract the order from the items
        orders = {item.order for item in items if item.order}

        if len(orders) != 1:
            return Response(
                {"error": "Items do not belong to the same order."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Get the single order
        order = orders.pop()

        # Check if all items for the order are included in the request
        all_order_item_uuids = set(
            Item.objects.filter(order=order).values_list("uuid", flat=True)
        )
        provided_item_uuids = {item.uuid for item in items}

        if provided_item_uuids != all_order_item_uuids:
            return Response(
                {"error": "Not all items for this order are provided."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Check if the order type is 'return'
        if order.type != "return":
            return Response(
                {"error": "Order type is not 'return'. No update performed."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        # Set the order type to 'completed' and update the items
        order.type = "completed"
        order.save()

        # Set all items in the order to have their `order` field as None
        Item.objects.filter(order=order).update(order=None)

        return Response(
            {"message": f"Order {order.id} updated to 'completed' and items' order set to None."},
            status=status.HTTP_200_OK,
        )


@api_view(['POST'])
def create_item(request):
    try:
        # Parse the JSON data from the request body
        data = json.loads(request.body)

        # Validate and prepare items
        items_to_create = []
        for entry in data:
            try:
                item_uuid = uuid.UUID(entry['uuid'])
                six_digit_code = int(entry['six_digit_code'])

                # Validate six_digit_code is within range
                if 100000 <= six_digit_code <= 999999:
                    items_to_create.append(
                        Item(uuid=item_uuid, six_digit_code=six_digit_code))
                else:
                    return JsonResponse({"error": f"Invalid six_digit_code: {six_digit_code}"}, status=400)
            except (ValueError, KeyError, TypeError):
                return JsonResponse({"error": f"Invalid data format for entry: {entry}"}, status=400)

        # Bulk create items
        Item.objects.bulk_create(items_to_create)

        return JsonResponse({"message": "Items created successfully.", "created_count": len(items_to_create)}, status=201)

    except json.JSONDecodeError:
        return JsonResponse({"error": "Invalid JSON data."}, status=400)


@api_view(['GET'])
def get_live_return_orders(request):
    # Filter for orders of type "return"
    return_orders = Order.objects.filter(type="return")

    # Serialize the list of return orders
    serializer = OrderSerializer(return_orders, many=True)

    # Return the serialized data (list of orders or empty list if none)
    return Response(serializer.data, status=status.HTTP_200_OK)


@api_view(['POST'])
def clear_all_orders(request):
    Order.objects.all().delete()
    return Response({"message": "All orders cleared."}, status=status.HTTP_200_OK)
