package cn.rongcloud.rtc.example.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
	private static String startFormatCmd = "./Recorder -h %s -p %s -a %s -c %s -o %s -k %s ";

	private String appid;
	private String cid;
	private String addr;
	private String uniqueKey;
	private String port;
	private String fileName;
	private String uid;

	public Recorder(String appid, String cid, String addr, String port, String uniqueKey, String uid, String fileName) {
		this.addr = addr;
		this.appid = appid;
		this.uniqueKey = uniqueKey;
		this.cid = cid;
		this.port = port;
		this.uid = uid;
		this.fileName = fileName;
	}

	public void start() throws IOException {
		// cid中如果含有特殊字符 | 会使得录像错误，特修正
		String dir = Config.instance().getRecordSaveDir() + cid.replaceAll("\\|", "").split("@")[0] + "_"
				+ dateFormat.format(new Date()) + "/";

		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}

		String localIp = Config.instance().getExternalToLocalIpMap().get(addr);
		if (!StringUtils.isEmpty(localIp)) {
			addr = localIp;
		}
		
		List<String> cmds = getStartRecordProcessCmd(dir);
		ProcessBuilder pb = new ProcessBuilder(cmds);
		pb.start();
		
	}

	public void stop() throws IOException {
		String pid = getProcessPid();
		if (pid == null) {
			return;
		}
		String command = "kill -15 " + pid;
		Runtime.getRuntime().exec(command);
	}

	private List<String> getStartRecordProcessCmd(String dir) throws IOException {
		List<String> cmds = new ArrayList<>();
		String cmd = String.format(startFormatCmd, addr, port, appid, cid.replaceAll("\\|", "\\\\|"), dir, uniqueKey);
		String logFile = dir + "result.log";

		StringBuilder builder = new StringBuilder(cmd);
		if (this.uid != null && this.uid.length() > 0) {
			builder.append(" -u ").append(this.uid);
		}
		if (this.fileName != null && this.fileName.length() > 0) {
			builder.append(" --file ").append(fileName);
			logFile = dir + fileName + ".log";
		}
		if (Config.instance().isMixMode()) {
			builder.append(" -m");
		}

		File logs = new File(logFile);
		if (!logs.createNewFile()) {
			logger.warn("create log file error,path={}", logFile);
		}
		builder.append(" > ").append(logFile).append(" 2>&1 &");

		cmds.add("sh");
		cmds.add("-c");
		cmds.add(builder.toString());

		return cmds;
	}
	
	private String getProcessPid() throws IOException {
		String pid = null;
		Process pro = Runtime.getRuntime().exec("ps -ef");
		BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (checkIsRecordProcess(line)) {
				logger.info("line:{}", line);
				String[] strs = line.split("\\s+");
				if (strs.length > 2) {
					pid = strs[1];
					break;
				}
			}

		}
		reader.close();
		pro.destroy();
		return pid;

	}

	private boolean checkIsRecordProcess(String line) {
		if (!line.contains(appid) || !line.contains(cid.replaceAll("\\|", "\\\\|"))
				|| (uid != null && !line.contains(uid))) {
			return false;
		}
		
		return true;
	}
	
	public boolean checkIsSameRecord(String cid, String uniqueKey) {
		return cid.equals(this.cid) && uniqueKey.equals(this.uniqueKey);
	}
}
