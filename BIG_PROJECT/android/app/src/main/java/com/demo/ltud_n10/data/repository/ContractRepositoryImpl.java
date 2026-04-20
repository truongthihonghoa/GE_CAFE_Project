package com.demo.ltud_n10.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.data.remote.ApiService;
import com.demo.ltud_n10.data.remote.dto.ContractDto;
import com.demo.ltud_n10.domain.model.Contract;
import com.demo.ltud_n10.domain.repository.ContractRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ContractRepositoryImpl implements ContractRepository {

    private final ApiService apiService;

    @Inject
    public ContractRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Contract>>> getContracts() {
        MutableLiveData<Resource<List<Contract>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getContracts().enqueue(new Callback<List<ContractDto>>() {
            @Override
            public void onResponse(Call<List<ContractDto>> call, Response<List<ContractDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contract> list = new ArrayList<>();
                    for (ContractDto dto : response.body()) {
                        list.add(mapToDomain(dto));
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Lỗi lấy danh sách hợp đồng", null));
                }
            }

            @Override
            public void onFailure(Call<List<ContractDto>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Contract>> addContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addContract(mapToDto(contract)).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(Call<ContractDto> call, Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi thêm hợp đồng";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ContractDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Contract>> updateContract(Contract contract) {
        MutableLiveData<Resource<Contract>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.updateContract(contract.getId(), mapToDto(contract)).enqueue(new Callback<ContractDto>() {
            @Override
            public void onResponse(Call<ContractDto> call, Response<ContractDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(mapToDomain(response.body())));
                } else {
                    String errorMsg = "Lỗi cập nhật hợp đồng";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ContractDto> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });
        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> deleteContract(String contractId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteContract(contractId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Lỗi xóa hợp đồng", false));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), false));
            }
        });
        return result;
    }

    private Contract mapToDomain(ContractDto dto) {
        double salary = 0;
        double hourly = 0;
        double hours = 0;
        String terms = "";
        String resp = "";
        String notes = "";

        if (dto.getChiTiet() != null) {
            salary = dto.getChiTiet().getLuongCoBan();
            hourly = dto.getChiTiet().getLuongTheoGio();
            hours = dto.getChiTiet().getSoGioLam();
            terms = dto.getChiTiet().getDieuKhoan();
            resp = dto.getChiTiet().getTrachNhiem();
            notes = dto.getChiTiet().getGhiChu();
        }
        
        Contract contract = new Contract(
                dto.getMaHd(),
                dto.getMaNv(),
                dto.getHoTenNv() != null ? dto.getHoTenNv() : dto.getMaNv(),
                dto.getLoaiHd(),
                dto.getNgayBatDau(),
                dto.getNgayKetThuc(),
                salary,
                dto.getChucVu(),
                dto.getTrangThai()
        );
        contract.setHourlySalary(hourly);
        contract.setWorkHours(hours);
        contract.setTerms(terms);
        contract.setResponsibilities(resp);
        contract.setNotes(notes);
        return contract;
    }

    private ContractDto mapToDto(Contract domain) {
        ContractDto dto = new ContractDto();
        dto.setMaHd(domain.getId());
        dto.setMaNv(domain.getEmployeeId());
        dto.setLoaiHd(domain.getType());
        dto.setChucVu(domain.getPosition());
        dto.setNgayBatDau(domain.getStartDate());
        dto.setNgayKetThuc(domain.getEndDate());
        dto.setTrangThai(domain.getStatus());
        dto.setMaChiNhanh("CN01");

        ContractDto.DetailDto detail = new ContractDto.DetailDto();
        detail.setLuongCoBan(domain.getSalary());
        detail.setLuongTheoGio(domain.getHourlySalary());
        detail.setSoGioLam(domain.getWorkHours());
        detail.setDieuKhoan(domain.getTerms());
        detail.setTrachNhiem(domain.getResponsibilities());
        detail.setGhiChu(domain.getNotes());
        dto.setChiTiet(detail);

        return dto;
    }
}
