package cn.rongcloud.rtc.example.channelsync.domain;

public enum RecordType {
	AUTOSYNC(1), CUSTOMASYNC(2);
	private int value;

	RecordType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
