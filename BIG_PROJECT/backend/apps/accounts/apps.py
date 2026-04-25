from django.apps import AppConfig

class AccountsConfig(AppConfig):
    name = 'apps.accounts'

    def ready(self):
        # Tự động nạp dữ liệu khi khởi động nếu DB trống
        import os
        from django.db.models.signals import post_migrate

        def load_initial_data(sender, **kwargs):
            from django.contrib.auth.models import User
            from django.core.management import call_command
            
            # Chỉ nạp dữ liệu nếu chưa có User nào
            try:
                if not User.objects.exists():
                    print(">>> Detecting empty database on Cloud. Starting auto-load of data.json...")
                    call_command('loaddata', 'data.json')
                    print(">>> Data loaded successfully!")
            except Exception as e:
                print(f">>> Error loading data: {e}")

        post_migrate.connect(load_initial_data, sender=self)
