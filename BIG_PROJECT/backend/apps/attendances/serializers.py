from rest_framework import serializers
from .models import ChamCong

class ChamCongSerializer(serializers.ModelSerializer):
    ho_ten = serializers.SerializerMethodField()

    class Meta:
        model = ChamCong
        fields = '__all__'

    def get_ho_ten(self, obj):
        try:
            return obj.ma_nv.ho_ten
        except:
            return "Chưa xác định"