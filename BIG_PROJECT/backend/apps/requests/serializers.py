from rest_framework import serializers
from .models import YeuCau

class YeuCauSerializer(serializers.ModelSerializer):
    class Meta:
        model = YeuCau
        fields = '__all__'