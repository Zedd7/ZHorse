package com.github.zedd7.zhorse.utils;

public class CallbackResponse<T> {

	private T result;

	public CallbackResponse() {}

	public CallbackResponse(T result) {
		this.result = result;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

}
