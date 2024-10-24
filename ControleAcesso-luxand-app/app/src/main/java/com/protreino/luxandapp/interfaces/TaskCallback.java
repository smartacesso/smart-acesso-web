package com.protreino.luxandapp.interfaces;

import com.protreino.luxandapp.model.ResultWrapper;

public interface TaskCallback {
    void onTaskCompleted(ResultWrapper<?> resultWrapper);
}
