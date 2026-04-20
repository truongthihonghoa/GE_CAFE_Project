from rest_framework import serializers
from .models import LichLamViec

class ScheduleSerializer(serializers.ModelSerializer):
    class Meta:
        model = LichLamViec
        fields = '__all__'