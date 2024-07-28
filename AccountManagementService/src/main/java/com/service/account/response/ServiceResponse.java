package com.service.account.response;

import java.util.List;

public class ServiceResponse<T> {

	private List<T> dataList;
	private T data;
	private String errorCode;
	private String message;
	
	public ServiceResponse() {
		super();
	}

	public ServiceResponse(T data, String message, String errorCode) {
		super();
//		this.dataList = dataList;
		this.data = data;
		this.message = message;
		this.errorCode = errorCode;
	}
	
	public ServiceResponse(List<T> list, T data, String message, String errorCode) {
		super();
		this.dataList = list;
		this.data = data;
		this.message = message;
		this.errorCode = errorCode;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
