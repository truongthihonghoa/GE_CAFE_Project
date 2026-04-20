from django import forms
from .models import ChiNhanh

class ChiNhanhForm(forms.ModelForm):
    STATUS_CHOICES = [
        ('Đang hoạt động', 'Đang hoạt động'),
        ('Ngưng hoạt động', 'Ngưng hoạt động'),
    ]

    trang_thai = forms.ChoiceField(
        choices=STATUS_CHOICES,
        widget=forms.Select(attrs={'class': 'form-control'}),
        label="Trạng thái"
    )
    class Meta:
        model = ChiNhanh
        fields = ['ten_chi_nhanh', 'dia_chi', 'sdt', 'ma_nv_ql','trang_thai']