package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.PayrollDetail;
import com.demo.ltud_n10.domain.model.PayrollPeriod;
import java.util.List;

public interface PayrollRepository {
    LiveData<Resource<List<PayrollPeriod>>> getPayrollPeriods(String month, String year);
    LiveData<Resource<List<PayrollDetail>>> getPayrollDetails(String periodId);
    LiveData<Resource<PayrollPeriod>> calculatePayroll(String month, String year, List<String> employeeIds);
    LiveData<Resource<PayrollDetail>> updatePayrollDetail(PayrollDetail detail);
    LiveData<Resource<Boolean>> approvePayrollDetail(String detailId);
    LiveData<Resource<Boolean>> rejectPayrollDetail(String detailId);
    LiveData<Resource<Boolean>> deletePayrollPeriod(String periodId);
    LiveData<Resource<Boolean>> deletePayrollDetail(String detailId);
    LiveData<Resource<Boolean>> approvePayrollPeriod(String periodId);
}
