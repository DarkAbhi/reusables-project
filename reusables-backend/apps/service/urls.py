from django.urls import path
from .views import OrderCreateView, create_item, get_live_return_orders, clear_all_orders, UpdateOrderTypeToReturnView, UpdateOrderTypeToCompletedView

urlpatterns = [
    path('orders/create/', OrderCreateView.as_view(), name='order-create'),
    path('orders/return/', UpdateOrderTypeToReturnView.as_view(), name='order-return'),
    path('orders/completed/', UpdateOrderTypeToCompletedView.as_view(), name='order-completed'),
    path('items/create/', create_item, name='create-item'),
    path('orders/live-return-orders/', get_live_return_orders,
         name='live-return-orders'),
    path('orders/clear-all/', clear_all_orders,
         name='clear-all-orders'),
]
