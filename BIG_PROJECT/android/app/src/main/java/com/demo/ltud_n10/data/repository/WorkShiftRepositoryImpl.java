package com.demo.ltud_n10.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.RequestDto;
import com.demo.ltud_n10.data.remote.dto.ScheduleDto;
import com.demo.ltud_n10.data.remote.model.EmployeeDto;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkShiftRepositoryImpl implements WorkShiftRepository {

    private final ApiService apiService;
    private final Map<String, String> employeeNames = new HashMap<>();

    @Inject
    public WorkShiftRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getEmployees().enqueue(new Callback<List<EmployeeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<EmployeeDto>> call, @NonNull Response<List<EmployeeDto>> responseEmp) {
                if (responseEmp.isSuccessful() && responseEmp.body() != null) {
                    for (EmployeeDto emp : responseEmp.body()) {
                        employeeNames.put(emp.getMaNv(), emp.getHoTen());
                    }
                }
                fetchSchedulesAndRequests(result);
            }

            @Override
            public void onFailure(@NonNull Call<List<EmployeeDto>> call, @NonNull Throwable t) {
                fetchSchedulesAndRequests(result);
            }
        });

        return result;
    }

    private void fetchSchedulesAndRequests(MutableLiveData<Resource<List<WorkShift>>> result) {
        apiService.getSchedules().enqueue(new Callback<List<ScheduleDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ScheduleDto>> call, @NonNull Response<List<ScheduleDto>> response) {
                List<WorkShift> combinedList = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (ScheduleDto dto : response.body()) {
                        combinedList.add(mapScheduleDtoToDomain(dto));
                    }
                }
                
                apiService.getRequests().enqueue(new Callback<List<RequestDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<RequestDto>> call, @NonNull Response<List<RequestDto>> responseRequests) {
                        if (responseRequests.isSuccessful() && responseRequests.body() != null) {
                            for (RequestDto dto : responseRequests.body()) {
                                combinedList.add(mapRequestDtoToDomain(dto, "Đăng ký ca"));
                            }
                        }
                        
                        apiService.getLeaveRequests().enqueue(new Callback<List<RequestDto>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<RequestDto>> call, @NonNull Response<List<RequestDto>> responseLeave) {
                                if (responseLeave.isSuccessful() && responseLeave.body() != null) {
                                    for (RequestDto dto : responseLeave.body()) {
                                        combinedList.add(mapRequestDtoToDomain(dto, "Nghỉ phép"));
                                    }
                                }
                                result.setValue(Resource.success(combinedList));
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<RequestDto>> call, @NonNull Throwable t) {
                                result.setValue(Resource.success(combinedList));
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<RequestDto>> call, @NonNull Throwable t) {
                        result.setValue(Resource.success(combinedList));
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<ScheduleDto>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        RequestDto dto = new RequestDto();
        dto.setEmployeeId(shift.getEmployeeId());
        dto.setReason(shift.getPosition());
        dto.setStatus("Chờ duyệt");

        String dateStr = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        
        if (shift.getType().equals("Nghỉ phép")) {
            dto.setType("Nghỉ phép");
            dto.setId("NP_" + shift.getEmployeeId() + "_" + dateStr);
            
            String[] dates = (shift.getDate() != null) ? shift.getDate().split(" - ") : new String[0];
            if (dates.length == 2) {
                dto.setStartDate(dates[0]);
                dto.setEndDate(dates[1]);
            } else {
                dto.setStartDate(shift.getDate());
                dto.setEndDate(shift.getDate());
            }
            
            apiService.addLeaveRequest(dto).enqueue(new Callback<RequestDto>() {
                @Override
                public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                    if (response.isSuccessful()) {
                        result.setValue(Resource.success(mapRequestDtoToDomain(response.body(), "Nghỉ phép")));
                    } else {
                        result.setValue(Resource.error("Lỗi Server: Không thể gửi đơn nghỉ phép", null));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                    result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                }
            });
        } else {
            dto.setType("Đăng ký ca làm");
            dto.setId("DK_" + shift.getEmployeeId() + "_" + dateStr);

            dto.setStartDate(shift.getDate());
            dto.setEndDate(shift.getDate());
            
            apiService.addRequest(dto).enqueue(new Callback<RequestDto>() {
                @Override
                public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                    if (response.isSuccessful()) {
                        result.setValue(Resource.success(mapRequestDtoToDomain(response.body(), "Đăng ký ca")));
                    } else {
                        result.setValue(Resource.error("Lỗi Server: Không thể gửi đăng ký ca", null));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                    result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                }
            });
        }

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        RequestDto dto = new RequestDto();
        dto.setId(shift.getId());
        dto.setEmployeeId(shift.getEmployeeId());
        dto.setType(shift.getType().equals("Nghỉ phép") ? "Nghỉ phép" : "Đăng ký ca làm");
        dto.setReason(shift.getPosition());
        dto.setStatus("Chờ duyệt");
        
        if (shift.getType().equals("Nghỉ phép")) {
            apiService.updateLeaveRequest(shift.getId(), dto).enqueue(new Callback<RequestDto>() {
                @Override
                public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                    if (response.isSuccessful()) {
                        result.setValue(Resource.success(mapRequestDtoToDomain(response.body(), "Nghỉ phép")));
                    } else {
                        result.setValue(Resource.error("Lỗi khi cập nhật nghỉ phép", null));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                    result.setValue(Resource.error(t.getMessage(), null));
                }
            });
        } else {
            apiService.updateRequest(shift.getId(), dto).enqueue(new Callback<RequestDto>() {
                @Override
                public void onResponse(@NonNull Call<RequestDto> call, @NonNull Response<RequestDto> response) {
                    if (response.isSuccessful()) {
                        result.setValue(Resource.success(mapRequestDtoToDomain(response.body(), "Đăng ký ca")));
                    } else {
                        result.setValue(Resource.error("Lỗi khi cập nhật đăng ký", null));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RequestDto> call, @NonNull Throwable t) {
                    result.setValue(Resource.error(t.getMessage(), null));
                }
            });
        }

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteRequest(shiftId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    apiService.deleteLeaveRequest(shiftId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> callLeave, @NonNull Response<Void> responseLeave) {
                            if (responseLeave.isSuccessful()) result.setValue(Resource.success(true));
                            else result.setValue(Resource.error("Lỗi khi xóa", false));
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> callLeave, @NonNull Throwable t) {
                            result.setValue(Resource.error(t.getMessage(), false));
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        return new MutableLiveData<>(Resource.success(true));
    }

    private WorkShift mapScheduleDtoToDomain(ScheduleDto dto) {
        WorkShift shift = new WorkShift();
        shift.setId(dto.getId());
        shift.setEmployeeId(dto.getEmployeeId());
        String name = employeeNames.get(dto.getEmployeeId());
        shift.setEmployeeName(name != null ? name : dto.getEmployeeId()); 
        shift.setDate(dto.getWorkDate());
        
        String caLam = dto.getShift();
        if (caLam != null && caLam.contains("-")) {
            String[] parts = caLam.split("-");
            shift.setStartTime(parts[0].trim());
            shift.setEndTime(parts[1].trim());
        } else {
            String[] times = parseShiftTime(caLam);
            shift.setStartTime(times[0]);
            shift.setEndTime(times[1]);
        }
        
        shift.setPosition(dto.getShift());
        shift.setStatus(dto.getStatus());
        shift.setType("Lịch làm việc");
        shift.setSent(true);
        return shift;
    }

    private WorkShift mapRequestDtoToDomain(RequestDto dto, String defaultType) {
        WorkShift shift = new WorkShift();
        if (dto == null) return shift;
        
        shift.setId(dto.getId());
        shift.setEmployeeId(dto.getEmployeeId());
        String name = employeeNames.get(dto.getEmployeeId());
        shift.setEmployeeName(name != null ? name : dto.getEmployeeId());
        
        if ("Nghỉ phép".equals(dto.getType()) || "Nghỉ phép".equals(defaultType)) {
            shift.setType("Nghỉ phép");
            shift.setDate(dto.getStartDate() + " - " + dto.getEndDate());
        } else {
            shift.setType("Đăng ký ca");
            shift.setDate(dto.getStartDate());
        }
        
        shift.setPosition(dto.getReason());
        shift.setStatus(dto.getStatus());
        shift.setSent(true);
        return shift;
    }

    private String[] parseShiftTime(String shiftType) {
        if (shiftType == null) return new String[]{"00:00", "00:00"};
        switch (shiftType) {
            case "Ca Sáng": return new String[]{"06:00", "12:00"};
            case "Ca Chiều": return new String[]{"12:00", "18:00"};
            case "Ca Tối": return new String[]{"18:00", "22:00"};
            default: return new String[]{"08:00", "16:00"};
        }
    }
}
