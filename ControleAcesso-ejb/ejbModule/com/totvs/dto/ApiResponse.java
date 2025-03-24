package com.totvs.dto;

import java.util.List;

public class ApiResponse {
    private boolean hasNext;
    private int total;
    private List<FuncionarioTotvsDto> items;

    // Getters and Setters

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FuncionarioTotvsDto> getItems() {
        return items;
    }

    public void setItems(List<FuncionarioTotvsDto> items) {
        this.items = items;
    }
}
