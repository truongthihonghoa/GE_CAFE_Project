import os
import django
import json

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from django.core import serializers
from django.apps import apps

all_objects = []

# Lấy tất cả object từ từng model
for model in apps.get_models():
    all_objects.extend(model.objects.all())

# Serialize trực tiếp và ghi ra file UTF-8
with open('data_fixed.json', 'w', encoding='utf-8') as f:
    serializers_data = serializers.serialize("json", all_objects)
    # json.loads để format đẹp, ensure_ascii=False giữ tiếng Việt
    json.dump(
        json.loads(serializers_data),
        f,
        ensure_ascii=False,
        indent=2
    )

print("Dump thành công! File UTF-8 chuẩn, tiếng Việt OK ✅")