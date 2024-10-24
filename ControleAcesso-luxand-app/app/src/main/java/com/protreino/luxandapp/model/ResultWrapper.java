package com.protreino.luxandapp.model;

import com.protreino.services.enumeration.TaskType;

/**
 * A generic class that holds a result
 */
public class ResultWrapper<T> {

    private T result;
    private boolean error;
    private String errorMessage;
    private TaskType taskType;

    public ResultWrapper(T result, TaskType taskType) {
        this.error = false;
        this.result = result;
        this.taskType = taskType;
    }

    public ResultWrapper(String errorMessage, TaskType taskType) {
        this.error = true;
        this.errorMessage = errorMessage;
        this.taskType = taskType;
    }

    public T getResult() {
        return result;
    }

    public boolean hasError() {
        return error;
    }

    public String getErrorMessage() { return errorMessage; }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskType getTaskType() {
        return this.taskType;
    }
}