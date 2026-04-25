package com.demo.ltud_n10.domain.repository;

import androidx.lifecycle.LiveData;
import com.demo.ltud_n10.core.Resource;
import com.demo.ltud_n10.domain.model.Request;
import java.util.List;

public interface RequestRepository {
    LiveData<Resource<List<Request>>> getRequests();
    LiveData<Resource<Request>> addRequest(Request request);
    LiveData<Resource<Request>> updateRequest(Request request);
    LiveData<Resource<Boolean>> deleteRequest(String requestId);
    LiveData<Resource<Request>> updateRequestStatus(String requestId, String status);
}
