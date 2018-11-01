package cn.rongcloud.rtc.example.config;

import org.springframework.util.StringUtils;

import cn.rongcloud.rtc.example.channelsync.domain.RecordType;

import java.util.HashMap;
import java.util.Map;

public class Config {
	
	private static Config instance;
	
	public static final int REGIST_INTERVAL = 1000 * 60 * 2;
	
	private String ip = "0.0.0.0";
	private int port = 8801;

	@RequiredConfig
	private String appKey;
	@RequiredConfig
	private String secret;

	private String gatewayAddr = "https://rtc.ronghub.com";
    @RequiredConfig
	private String recvAddr;
	
	private String recordSaveDir;
	private int recordType = RecordType.AUTOSYNC.getValue();
	private Map<String,String> externalToLocalIpMap = new HashMap<>();
	private boolean mixMode = false;
	

	public static void initialize() throws Exception {
		instance = new Config();
		ConfigUitl.initLocalConfig(instance, "ServiceSettings.properties");
		ConfigUitl.checkRequiredConfig(instance);
	}

	public static Config instance() {
		return instance;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getRecvAddr() {
		return recvAddr;
	}
	
	public int getRecordType() {
		return recordType;
	}

	public String getGatewayAddr() {
		return gatewayAddr;
	}

	public String getRecordSaveDir() {
		return recordSaveDir;
	}

//	public boolean isMixMode() {
//		return mixMode;
//	}


	public Map<String, String> getExternalToLocalIpMap() {
		return externalToLocalIpMap;
	}

	public void setExternalToLocalIpMap(String externalToLocalIpMap) {
		String[] split = externalToLocalIpMap.split(";");
		for(int i=0;i<split.length;i++){
			String index = split[i];
			if(!StringUtils.isEmpty(index)){
				String[] ipmap = index.split("-");
				this.externalToLocalIpMap.put(ipmap[0],ipmap[1]);
			}
		}
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecret() {
		return secret;
	}

	public boolean isMixMode() {
		return mixMode;
	}
}
