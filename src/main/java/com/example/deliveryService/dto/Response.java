package com.example.deliveryService.dto;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class Response<T> {

    private int responseCode = 500;
    private Map<String, T> data = new HashMap<>();

    private String errorMessage;
    private String successMessage;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(HttpStatus responseCode) {
        this.responseCode = responseCode.value();
    }

    public Map<String, T> getData() {
        return data;
    }

    public void setData(T data) {
        this.data.put("data", data);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

	public void setMessage(String successMessage) {
		this.successMessage = successMessage;
		
	}
}