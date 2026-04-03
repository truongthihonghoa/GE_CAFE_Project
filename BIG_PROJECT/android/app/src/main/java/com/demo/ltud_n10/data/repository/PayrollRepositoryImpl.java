package com.demo.ltud_n10.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import com.demo.ltud_n10.domain.repository.PayrollRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PayrollRepositoryImpl implements PayrollRepository {

    private final List<PayrollPeriod> periods = new ArrayList<>();
    private final List<PayrollDetail> details = new ArrayList<>();

    @Inject
    public PayrollRepositoryImpl() {
        // Mock data cho nhân viên Lê Văn C (NV001) qua các tháng
        
        // Tháng 02/2026
        PayrollDetail feb = new PayrollDetail("D001", "NV001", "Lê Văn C", 5000000.0, 500000.0, 100000.0, "Đã duyệt");
        feb.setMonth("02"); feb.setYear("2026");
        feb.setHoursWorked(160); feb.setFactor(1.0); feb.setHourlyRate(30000);
        details.add(feb);

        // Tháng 01/2026
        PayrollDetail jan = new PayrollDetail("D002", "NV001", "Lê Văn C", 2000000.0, 2000000.0, 100000.0, "Đã duyệt");
        jan.setMonth("01"); jan.setYear("2026");
        jan.setHoursWorked(100); jan.setFactor(2.0); jan.setHourlyRate(20000);
        details.add(jan);

        // Tháng 12/2025
        PayrollDetail dec = new PayrollDetail("D003", "NV001", "Lê Văn C", 4500000.0, 1000000.0, 50000.0, "Đã duyệt");
        dec.setMonth("12"); dec.setYear("2025");
        dec.setHoursWorked(150); dec.setFactor(1.2); dec.setHourlyRate(25000);
        details.add(dec);

        // Thêm một số nhân viên khác
        details.add(new PayrollDetail("D004", "NV002", "Phạm Thị D", 6000000.0, 0.0, 0.0, "Đang chờ duyệt"));
    }

    @Override
    public LiveData<Resource<List<PayrollPeriod>>> getPayrollPeriods(String month, String year) {
        MutableLiveData<Resource<List<PayrollPeriod>>> result = new MutableLiveData<>();
        result.setValue(Resource.success(new ArrayList<>(periods)));
        return result;
    }

    @Override
    public LiveData<Resource<List<PayrollDetail>>> getPayrollDetails(String periodId) {
        MutableLiveData<Resource<List<PayrollDetail>>> result = new MutableLiveData<>();
        result.setValue(Resource.success(new ArrayList<>(details)));
        return result;
    }

    @Override
    public LiveData<Resource<PayrollPeriod>> calculatePayroll(String month, String year, List<String> employeeIds) {
        MutableLiveData<Resource<PayrollPeriod>> result = new MutableLiveData<>();
        return result;
    }

    @Override
    public LiveData<Resource<PayrollDetail>> updatePayrollDetail(PayrollDetail detail) {
        MutableLiveData<Resource<PayrollDetail>> result = new MutableLiveData<>();
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> approvePayrollDetail(String detailId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> rejectPayrollDetail(String detailId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deletePayrollPeriod(String periodId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> approvePayrollPeriod(String periodId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        return result;
    }
}
