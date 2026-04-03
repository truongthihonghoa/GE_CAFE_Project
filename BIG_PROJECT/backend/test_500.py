import os
import sys

# Ensure backend directory is in the python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
import django
django.setup()

from django.test import Client
import traceback

client = Client()
try:
    # Use API url correctly
    response = client.get('/api/employees/nhanvien/')
    if response.status_code >= 400:
        print(f"Status Code: {response.status_code}")
        print(response.content.decode('utf-8')[:2000])
    else:
        print(f"Success: {response.status_code}")
except Exception as e:
    print(traceback.format_exc())
