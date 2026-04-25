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

from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication
from .serializers import ChiNhanhSerializer
from rest_framework.authentication import SessionAuthentication

class ChiNhanhViewSet(viewsets.ModelViewSet):
    queryset = ChiNhanh.objects.all()
    serializer_class = ChiNhanhSerializer
    authentication_classes = [JWTAuthentication, SessionAuthentication]
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        user = self.request.user

        # 1. Nếu là Chủ (Admin): Hiển thị tất cả chi nhánh
        if user.is_staff or user.is_superuser:
            return ChiNhanh.objects.all()

        # 2. Nếu là Nhân viên:
        # Tìm chi nhánh mà nhân viên (có tài khoản là user hiện tại) đang thuộc về
        # Dựa trên trường 'ma_chi_nhanh' trong bảng NhanVien
        return ChiNhanh.objects.filter(
            nhan_viens__taikhoan__user=user
        ).distinct()

    def is_admin(self, request):
        """Hàm kiểm tra nhanh xem có phải là Chủ (Admin) không"""
        return request.user.is_superuser or request.user.is_staff

    def create(self, request, *args, **kwargs):
        """Chặn nhân viên thêm chi nhánh"""
        if not self.is_admin(request):
            return Response({"detail": "Chỉ Chủ cửa hàng mới có quyền thêm chi nhánh mới."},
                            status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

    def update(self, request, *args, **kwargs):
        """Chặn nhân viên sửa chi nhánh"""
        if not self.is_admin(request):
            return Response({"detail": "Bạn không có quyền chỉnh sửa thông tin chi nhánh."},
                            status=status.HTTP_403_FORBIDDEN)
        return super().update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        """Chặn nhân viên xóa chi nhánh"""
        if not self.is_admin(request):
            return Response({"detail": "Hành động bị từ chối. Chỉ Chủ mới có thể xóa chi nhánh."},
                            status=status.HTTP_403_FORBIDDEN)
        return super().destroy(request, *args, **kwargs)
