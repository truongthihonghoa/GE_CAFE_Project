from django.shortcuts import render


def report_list_view(request):
    """
    View hiển thị danh sách báo cáo (MOCK DATA - UI only)
    """
    return render(request, 'reports/report_list.html')
