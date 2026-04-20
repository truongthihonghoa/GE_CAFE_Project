package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.PayrollDto;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PayrollRepositoryImpl implements PayrollRepository {

    private final ApiService apiService;

    @Inject
    public PayrollRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<PayrollPeriod>>> getPayrollPeriods(String month, String year) {
        MutableLiveData<Resource<List<PayrollPeriod>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getPayrolls(month, year).enqueue(new Callback<List<PayrollDto>>() {
            @Override
            public void onResponse(Call<List<PayrollDto>> call, Response<List<PayrollDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PayrollDto> dtos = response.body();
                    List<PayrollPeriod> periods = new ArrayList<>();
                    
                    if (!dtos.isEmpty()) {
                        double total = 0;
                        for (PayrollDto dto : dtos) total += dto.getTongLuong();
                        
                        PayrollPeriod period = new PayrollPeriod(
                            year + month, 
                            month, 
                            year, 
                            dtos.size(), 
                            "", 
                            total, 
                            "Đã tính"
                        );
                        periods.add(period);
                    }
                    result.setValue(Resource.success(periods));
                } else {
                    result.setValue(Resource.error("Lỗi lấy kỳ lương", null));
                }
            }

            @Override
            public void onFailure(Call<List<PayrollDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<List<PayrollDetail>>> getPayrollDetails(String periodId) {
        // periodId is actually YYYYMM
        String year = periodId.substring(0, 4);
        String month = periodId.substring(4);
        
        MutableLiveData<Resource<List<PayrollDetail>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getPayrolls(month, year).enqueue(new Callback<List<PayrollDto>>() {
            @Override
            public void onResponse(Call<List<PayrollDto>> call, Response<List<PayrollDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PayrollDetail> list = new ArrayList<>();
                    for (PayrollDto dto : response.body()) {
                        list.add(mapToDetailDomain(dto));
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Lỗi lấy chi tiết lương", null));
                }
            }

            @Override
            public void onFailure(Call<List<PayrollDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<PayrollPeriod>> calculatePayroll(String month, String year, List<String> employeeIds) {
        MutableLiveData<Resource<PayrollPeriod>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        final int total = employeeIds.size();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        if (total == 0) {
            result.setValue(Resource.error("Không có nhân viên nào được chọn", null));
            return result;
        }

        for (String empId : employeeIds) {
            PayrollDto dto = new PayrollDto();
            dto.setMaNv(empId);
            dto.setThang(Integer.parseInt(month));
            dto.setNam(Integer.parseInt(year));
            dto.setTrangThai("cho_duyet");
            dto.setMaChiNhanh("CN01"); // Mặc định chi nhánh 1
            
            // Các giá trị mặc định, Backend sẽ tính toán dựa trên hợp đồng nếu cần
            dto.setLuongCoBan(0);
            dto.setLuongTheoGio(0);
            dto.setSoGioLam(0);
            
            apiService.addPayroll(dto).enqueue(new Callback<PayrollDto>() {
                @Override
                public void onResponse(Call<PayrollDto> call, Response<PayrollDto> response) {
                    if (response.isSuccessful()) {
                        successCount[0]++;
                    } else {
                        failureCount[0]++;
                    }
                    checkCompletion(successCount[0], failureCount[0], total, result, month, year);
                }

                @Override
                public void onFailure(Call<PayrollDto> call, Throwable t) {
                    failureCount[0]++;
                    checkCompletion(successCount[0], failureCount[0], total, result, month, year);
                }
            });
        }
        return result;
    }

    private void checkCompletion(int success, int failure, int total, MutableLiveData<Resource<PayrollPeriod>> result, String month, String year) {
        if (success + failure == total) {
            if (success > 0) {
                result.setValue(Resource.success(new PayrollPeriod("", month, year, success, "", 0, "Đã tính")));
            } else {
                result.setValue(Resource.error("Không thể tính lương cho nhân viên nào", null));
            }
        }
    }

    @Override
    public LiveData<Resource<PayrollDetail>> updatePayrollDetail(PayrollDetail detail) {
        MutableLiveData<Resource<PayrollDetail>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        apiService.updatePayroll(detail.getId(), mapToDto(detail)).enqueue(new Callback<PayrollDto>() {
            @Override
            public void onResponse(Call<PayrollDto> call, Response<PayrollDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDetailDomain(response.body())));
                } else {
                    result.setValue(Resource.error("Lỗi cập nhật lương", null));
                }
            }

            @Override
            public void onFailure(Call<PayrollDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> approvePayrollDetail(String detailId) {
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<Boolean>> rejectPayrollDetail(String detailId) {
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<Boolean>> deletePayrollPeriod(String periodId) {
        return new MutableLiveData<>();
    }

    @Override
    public LiveData<Resource<Boolean>> deletePayrollDetail(String detailId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        apiService.deletePayroll(detailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa bản ghi lương", null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> approvePayrollPeriod(String periodId) {
        return new MutableLiveData<>();
    }

    private PayrollDetail mapToDetailDomain(PayrollDto dto) {
        PayrollDetail detail = new PayrollDetail();
        detail.setId(dto.getMaLuong());
        detail.setEmployeeId(dto.getMaNv());
        detail.setEmployeeName(dto.getTenNv());
        detail.setMonth(String.valueOf(dto.getThang()));
        detail.setYear(String.valueOf(dto.getNam()));
        detail.setBaseSalary(dto.getLuongCoBan());
        detail.setHourlyRate(dto.getLuongTheoGio());
        detail.setHoursWorked(dto.getSoGioLam());
        detail.setBonus(dto.getThuong());
        detail.setPenalty(dto.getPhat());
        detail.setTotalSalary(dto.getTongLuong());
        detail.setStatus(dto.getTrangThai());
        return detail;
    }

    private PayrollDto mapToDto(PayrollDetail domain) {
        PayrollDto dto = new PayrollDto();
        dto.setMaLuong(domain.getId());
        dto.setMaNv(domain.getEmployeeId());
        dto.setThang(Integer.parseInt(domain.getMonth()));
        dto.setNam(Integer.parseInt(domain.getYear()));
        dto.setLuongCoBan(domain.getBaseSalary());
        dto.setLuongTheoGio(domain.getHourlyRate());
        dto.setSoGioLam(domain.getHoursWorked());
        dto.setThuong(domain.getBonus());
        dto.setPhat(domain.getPenalty());
        dto.setTrangThai(domain.getStatus());
        dto.setMaChiNhanh("CN01");
        return dto;
    }
}
