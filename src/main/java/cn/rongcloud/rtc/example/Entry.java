package cn.rongcloud.rtc.example;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import cn.rongcloud.rtc.example.channelsync.ChannelManager;
import cn.rongcloud.rtc.example.config.Config;
import cn.rongcloud.rtc.example.recorder.RecordManager;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class Entry {
	
	static Logger logger = LoggerFactory.getLogger(Entry.class);

	public static void main(String[] args) throws Exception {
		
		PropertyConfigurator.configure("log4j.properties");
		//初始配置
		Config.initialize();

		System.setProperty("server.port", Config.instance().getPort() + "");
		SpringApplication.run(Entry.class, args);

		//启动会场同步
		ChannelManager.instance().startChannelSync();

		//监听会场状态
		//并在合适的时候启动录像
		if (Config.instance().getRecordSaveDir() != null) {
			ChannelManager.instance().addChannelEventListener(RecordManager.instance());
		}
		
		logger.info("startup successful !");
	}
}
