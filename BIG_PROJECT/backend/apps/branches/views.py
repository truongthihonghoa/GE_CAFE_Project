from .models import ChiNhanh  # import model ChiNhanh
from django.shortcuts import render, get_object_or_404, redirect
from .forms import ChiNhanhForm

def branch_list(request):
    """View for displaying branch list"""
    # Lấy tất cả chi nhánh từ database
    branches = ChiNhanh.objects.all().order_by('ma_chi_nhanh')  # hoặc order_by('ten_chi_nhanh')

    context = {
        'branches': branches
    }
    return render(request, "branches/branch_list.html", context)


# 🔍 LIST + SEARCH (giống JS search)
def branch_list(request):
    keyword = request.GET.get('q', '')

    branches = ChiNhanh.objects.select_related('ma_nv_ql')

    if keyword:
        branches = branches.filter(
            ten_chi_nhanh__icontains=keyword
        ) | branches.filter(
            dia_chi__icontains=keyword
        ) | branches.filter(
            ma_nv_ql__ten_nv__icontains=keyword
        )

    return render(request, 'branches/branch_list.html', {
        'branches': branches,
        'keyword': keyword
    })


# ➕ CREATE
def branch_create(request):
    if request.method == 'POST':
        form = ChiNhanhForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, "Đã thêm chi nhánh mới thành công")
            return redirect('branches:branch_list')
    else:
        form = ChiNhanhForm()

    return render(request, 'branches/branch_form.html', {
        'form': form,
        'title': 'Thêm chi nhánh'
    })


# ✏️ UPDATE
def branch_update(request, pk):
    branch = get_object_or_404(ChiNhanh, pk=pk)

    if request.method == 'POST':
        form = ChiNhanhForm(request.POST, instance=branch)
        if form.is_valid():
            form.save()
            messages.success(request, "Cập nhât chi nhánh mới thành công")
            return redirect('branches:branch_list')
    else:
        form = ChiNhanhForm(instance=branch)

    return render(request, 'branches/branch_form.html', {
        'form': form,
        'title': 'Sửa chi nhánh'
    })


# ❌ DELETE
def branch_delete(request, pk):
    branch = get_object_or_404(ChiNhanh, pk=pk)
    branch.trang_thai = 'inactive'
    branch.save()
    return redirect('branches:branch_list')

from django.shortcuts import render, redirect
from django.contrib import messages
from .models import ChiNhanh # Giả sử tên model của bạn


from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status, permissions
from .models import ChiNhanh
from .serializers import ChiNhanhSerializer

class ChiNhanhViewSet(ModelViewSet):
    queryset = ChiNhanh.objects.all()
    serializer_class = ChiNhanhSerializer
    permission_classes = [permissions.AllowAny]

    # 👇 Xem dữ liệu
    def get_queryset(self):
        # Cho phép tất cả người dùng đã đăng nhập xem toàn bộ chi nhánh
        if self.request.user.is_authenticated:
            return ChiNhanh.objects.all()
        return ChiNhanh.objects.none()

    # 👇 thêm
    def create(self, request, *args, **kwargs):
        if not request.user.is_staff:
            return Response({"error": "Không có quyền"}, status=403)
        return super().create(request, *args, **kwargs)

    # 👇 sửa
    def update(self, request, *args, **kwargs):
        if not request.user.is_staff:
            return Response({"error": "Không có quyền"}, status=403)
        return super().update(request, *args, **kwargs)

    # 👇 xoá
    def destroy(self, request, *args, **kwargs):
        if not request.user.is_staff:
            return Response({"error": "Không có quyền"}, status=403)
        return super().destroy(request, *args, **kwargs)