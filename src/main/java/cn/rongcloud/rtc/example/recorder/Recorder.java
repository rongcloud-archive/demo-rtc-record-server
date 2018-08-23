package cn.rongcloud.rtc.example.recorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cn.rongcloud.rtc.example.config.Config;

public class Recorder {

	private static Logger logger = LoggerFactory.getLogger(Recorder.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("YY-MM-dd_HH-mm-ss");
	
	private static String cmdFormat = Config.instance().isMixMode()
			? "./Recorder -h %s -p %s -a %s -c %s -o %s -k %s -m > %s 2>&1 &"
			: "./Recorder -h %s -p %s -a %s -c %s -o %s -k %s > %s 2>&1 &";

	private String appid;
	private String cid;
	private String addr;
	private String uniqueKey;
	private String port;

	private Process p;
	
	public Recorder(String appid, String cid, String addr, String port, String uniqueKey) {
		this.addr = addr;
		this.appid = appid;
		this.uniqueKey = uniqueKey;
		this.cid = cid;
		this.port = port;
	}

	public void start() throws Exception {

		String dir = Config.instance().getRecordSaveDir() + cid.split("@")[0] + "_" + dateFormat.format(new Date())
				+ "/";

		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}

		String logFile = dir + "result.log";
		File logs = new File(logFile);
		logs.createNewFile();

		String localIp = Config.instance().getExternalToLocalIpMap().get(addr);
		if (!StringUtils.isEmpty(localIp)) {
			addr = localIp;
		}
		String cmd = String.format(cmdFormat, addr, port, appid, cid, dir, uniqueKey, logFile);
		logger.info(cmd);

		List<String> cmds = new ArrayList<String>();
		// https://www.cnblogs.com/lisperl/archive/2012/06/28/2568494.html
		cmds.add("sh");
		cmds.add("-c");
		cmds.add(cmd);
		ProcessBuilder pb = new ProcessBuilder(cmds);
		this.p = pb.start();
	}

	/**
	 * 不建议调用
	 * 在录像机性能不够  网络差时，会导致录像不完整
	 * 录像程序自己会在完成录像后结束进程
	 */
	public void stop(){
		p.destroy();
	}
}
