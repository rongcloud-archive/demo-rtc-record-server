package cn.rongcloud.rtc.example.recorder;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.rongcloud.rtc.example.channelsync.ChannelEventListener;
import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.Event;
import cn.rongcloud.rtc.example.config.Config;

public class RecordManager implements ChannelEventListener {

	private static Logger logger = LoggerFactory.getLogger(RecordManager.class);

	private static RecordManager instance = new RecordManager();
	
	private Map<String, Recorder> recorderMap = new HashMap<>();
	
	private RecordManager() {
	}
	
	public static RecordManager instance(){
		return instance;
	}

	@Override
	public void onChannelSync(Event event, Channel channel) {
	}

	@Override
	public void onMemberJoined(Event event, Channel channel) {
	}

	@Override
	public void onMemberLeft(Event event, Channel channel) {
		String cid = channel.getCid();
		
		//当前channel中只有一个人时，用户不会上传媒体流，停止录像
		synchronized (this) {
			if (channel.getMembers().size() < 2 && recorderMap.get(cid) != null) {
				logger.info("stop recorder,cid={}", cid);
				recorderMap.remove(cid);
			}
		}
	}

	@Override
	public void onMemberKicked(Event event, Channel channel) {
	}

	@Override
	public void onMemberTypeUpdated(Event event, Channel channel) {
	}

	@Override
	public void onMediaServerSelected(Event event, Channel channel) {
		
		String[] mediaServerAddr = String.valueOf(event.getData()).split(":");	
		String host = mediaServerAddr[0];
		String port = mediaServerAddr[1];

		//连接到指定的mediaserver，进行录像
		synchronized (this) {
			if (recorderMap.get(channel.getCid()) == null) {
				try {
					Recorder recorder = new Recorder(Config.instance().getAppKey(), channel.getCid(), host, port,
							channel.getUniqueKey());
					recorder.start();
					recorderMap.put(channel.getCid(), recorder);
				} catch (Exception e) {
					logger.error("start recorder error", e);
				}
			}
		}
	}

	@Override
	public void onChannelCreated(Event event, Channel channel) {
	}

	@Override
	public void onChannelDestroyed(Event event, Channel channel) {
	}

	@Override
	public void onWhiteBoardCreated(Event event, Channel channel) {
	}

}
