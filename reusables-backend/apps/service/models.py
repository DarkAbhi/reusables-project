from django.db import models
import uuid, random
from django.core.validators import MinValueValidator, MaxValueValidator
from django.db.models import UniqueConstraint


# Create your models here.


class Order(models.Model):
    ORDER_TYPE_CHOICES = [
        ('forward', 'Forward'),
        ('return', 'Return'),
        ('completed', 'Completed'),
    ]

    type = models.CharField(
        max_length=9, choices=ORDER_TYPE_CHOICES, default='forward')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Order {self.id} - {self.type}"


class Item(models.Model):
    order = models.ForeignKey(
        'Order', null=True, blank=True, on_delete=models.SET_NULL)
    uuid = models.UUIDField(default=uuid.uuid4, editable=True)
    six_digit_code = models.PositiveIntegerField(
        validators=[
            MinValueValidator(100000),
            MaxValueValidator(999999)
        ]
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        constraints = [
            UniqueConstraint(
                fields=['six_digit_code', 'uuid'], name='unique_six_digit_code_uuid')
        ]

    def save(self, *args, **kwargs):
        if not self.six_digit_code:
            # Loop until a unique six-digit code is found
            while True:
                code = random.randint(100000, 999999)
                if not Item.objects.filter(six_digit_code=code, uuid=self.uuid).exists():
                    self.six_digit_code = code
                    break
        super().save(*args, **kwargs)

    def __str__(self):
        return f"{self.id}. Item {self.uuid} - Code {self.six_digit_code}"
