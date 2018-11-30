package cn.rongcloud.rtc.example.channelsync.domain;

import com.google.gson.Gson;

/**
 * Created by kang on 2018/4/23.
 */
public class ResponseEntity {

	private static final Gson gson = new Gson();

	public static final int CODE_OK = 200;
	
	private int code;
	private String msg;
	private Object data;
	
	public ResponseEntity(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}

	public ResponseEntity setCode(int code) {
		this.code = code;
		return this;
	}

	public ResponseEntity setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public ResponseEntity setData(Object data) {
		this.data = data;
		return this;
	}

	@Override
	public String toString() {
		return gson.toJson(this);
	}
}
