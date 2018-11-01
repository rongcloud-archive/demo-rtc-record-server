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
	private static String startFormatCmd = "./Recorder -h %s -p %s -a %s -c %s -o %s -k %s";

	private String appid;
	private String cid;
	private String addr;
	private String uniqueKey;
	private String port;
	private String fileName;

	public Recorder(String appid, String cid, String addr, String port, String uniqueKey, String fileName) {
		this.addr = addr;
		this.appid = appid;
		this.uniqueKey = uniqueKey;
		this.cid = cid;
		this.port = port;
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

//		List<String> cmds = getStartProcessCmd(dir);
//		ProcessBuilder pb = new ProcessBuilder(cmds);
//		pb.start();
		
		String command = getStartProcessCmdTest(dir);
		Runtime.getRuntime().exec(command);
	}

	public void stop() throws Exception {
		String pid = getProcessPid();
		logger.info("stop process pid:{}", pid);
		if (pid == null) {
			return;
		}
		String command = "kill -3 " + pid;
		Process killPro = Runtime.getRuntime().exec(command);
		killPro.destroy();
//		List<String> cmds = new ArrayList<>();
//		cmds.add("kill");
//		cmds.add("-3");
//		cmds.add(pid);
//		ProcessBuilder pb = new ProcessBuilder(cmds);
//		pb.start();
	}

	private List<String> getStartProcessCmd(String dir) throws IOException {
		List<String> cmds = new ArrayList<>();
		String cmd = String.format(startFormatCmd, addr, port, appid, cid.replaceAll("\\|", "\\\\|"), dir, uniqueKey);
		String logFile = dir + "result.log";

		StringBuilder builder = new StringBuilder(cmd);
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
		
		logger.info("start process cmd: {}", builder.toString());

		cmds.add("sh");
		cmds.add("-c");
		cmds.add(builder.toString());

		return cmds;
	}
	
	private String getStartProcessCmdTest(String dir) throws IOException {
		String cmd = String.format(startFormatCmd, addr, port, appid, cid.replaceAll("\\|", "\\\\|"), dir, uniqueKey);
		String logFile = dir + "result.log";

		StringBuilder builder = new StringBuilder(cmd);
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
		builder.append(" > ").append(logFile).append(" &");
		
		logger.info("start process cmd: {}", builder.toString());

		return builder.toString();
	}


	private String getProcessPid() throws IOException {
		String pid = null;
		Process pro = Runtime.getRuntime().exec("ps -ef");
		BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (checkIsProcess(line)) {
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

	private boolean checkIsProcess(String line) {
		if (!line.contains(appid) || !line.contains(cid.replaceAll("\\|", "\\\\|"))
				|| (fileName != null && !line.contains(fileName))) {
			return false;
		}
		return true;
	}
}
