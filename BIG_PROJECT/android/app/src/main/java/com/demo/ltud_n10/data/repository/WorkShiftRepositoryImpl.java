package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.ScheduleDto;
import com.demo.ltud_n10.domain.model.WorkShift;
import com.demo.ltud_n10.domain.repository.WorkShiftRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkShiftRepositoryImpl implements WorkShiftRepository {

    private final ApiService apiService;

    @Inject
    public WorkShiftRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<WorkShift>>> getWorkShifts() {
        MutableLiveData<Resource<List<WorkShift>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getSchedules().enqueue(new Callback<List<ScheduleDto>>() {
            @Override
            public void onResponse(Call<List<ScheduleDto>> call, Response<List<ScheduleDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkShift> shifts = new ArrayList<>();
                    for (ScheduleDto dto : response.body()) {
                        shifts.add(mapDtoToDomain(dto));
                    }
                    result.setValue(Resource.success(shifts));
                } else {
                    result.setValue(Resource.error("Lỗi lấy dữ liệu lịch làm việc", null));
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleDto>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> addWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.createSchedule(mapDomainToDto(shift)).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(Call<ScheduleDto> call, Response<ScheduleDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi thêm ca làm";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ScheduleDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<WorkShift>> updateWorkShift(WorkShift shift) {
        MutableLiveData<Resource<WorkShift>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.updateSchedule(shift.getId(), mapDomainToDto(shift)).enqueue(new Callback<ScheduleDto>() {
            @Override
            public void onResponse(Call<ScheduleDto> call, Response<ScheduleDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapDtoToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi cập nhật ca làm";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ScheduleDto> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteWorkShift(String shiftId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteSchedule(shiftId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    String errorMsg = "Lỗi xóa ca làm";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), false));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> sendNotifications(List<String> shiftIds) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.sendNotification().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi gửi thông báo", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), false));
            }
        });

        return result;
    }

    private WorkShift mapDtoToDomain(ScheduleDto dto) {
        WorkShift domain = new WorkShift();
        domain.setId(dto.getId());
        domain.setEmployeeId(dto.getEmployeeId());
        domain.setEmployeeName(dto.getEmployeeName() != null ? dto.getEmployeeName() : "NV " + dto.getEmployeeId());
        
        // Convert yyyy-MM-dd (API) to dd/MM/yyyy (Domain)
        String apiDate = dto.getWorkDate();
        if (apiDate != null && apiDate.contains("-")) {
            try {
                String[] parts = apiDate.split("-");
                if (parts.length == 3) {
                    domain.setDate(parts[2] + "/" + parts[1] + "/" + parts[0]);
                }
            } catch (Exception e) {
                domain.setDate(apiDate);
            }
        } else {
            domain.setDate(apiDate);
        }

        domain.setStatus(dto.getStatus());
        
        // Map shift string (e.g., "7:00 - 11:00") to startTime and endTime
        String shift = dto.getShift();
        if (shift != null && shift.contains(" - ")) {
            String[] parts = shift.split(" - ");
            if (parts.length == 2) {
                domain.setStartTime(parts[0].trim());
                domain.setEndTime(parts[1].trim());
            }
        } else {
            domain.setStartTime(shift);
        }
        
        return domain;
    }

    private ScheduleDto mapDomainToDto(WorkShift domain) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(domain.getId());
        dto.setEmployeeId(domain.getEmployeeId());
        
        // Convert dd/MM/yyyy (Domain) to yyyy-MM-dd (API)
        String domainDate = domain.getDate();
        if (domainDate != null && domainDate.contains("/")) {
            try {
                String[] parts = domainDate.split("/");
                if (parts.length == 3) {
                    dto.setWorkDate(parts[2] + "-" + parts[1] + "-" + parts[0]);
                }
            } catch (Exception e) {
                dto.setWorkDate(domainDate);
            }
        } else {
            dto.setWorkDate(domainDate);
        }

        dto.setStatus(domain.getStatus());
        dto.setShift(domain.getStartTime() + " - " + domain.getEndTime());
        
        // Add required fields for server
        dto.setBranchId("CN01"); // Default branch (CN01 exists in DB)
        
        // Set created date (ngay_tao) as today
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        dto.setCreatedDate(sdf.format(new java.util.Date()));
        
        return dto;
    }
}
