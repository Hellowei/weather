package com.example.vincent.util;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
