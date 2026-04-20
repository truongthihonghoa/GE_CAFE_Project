from rest_framework import serializers
from .models import ChamCong

class ChamCongSerializer(serializers.ModelSerializer):
    class Meta:
        model = ChamCong
        fields = '__all__'