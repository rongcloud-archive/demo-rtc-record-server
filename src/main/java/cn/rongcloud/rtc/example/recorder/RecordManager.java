package cn.rongcloud.rtc.example.recorder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.rongcloud.rtc.example.channelsync.ChannelEventListener;
import cn.rongcloud.rtc.example.channelsync.ChannelManager;
import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.Event;
import cn.rongcloud.rtc.example.channelsync.domain.Member;
import cn.rongcloud.rtc.example.channelsync.domain.RecordType;
import cn.rongcloud.rtc.example.config.Config;

public class RecordManager implements ChannelEventListener {

	private static Logger logger = LoggerFactory.getLogger(RecordManager.class);

	private static RecordManager instance = new RecordManager();

	private Map<String, Recorder> recorderMap = new HashMap<>();

	private Map<String, String> memberChannelMap = new HashMap<>();
	
	private Map<String, Recorder> memberRecorderMap = new HashMap<>();

	private RecordManager() {
	}

	public static RecordManager instance() {
		return instance;
	}

	@Override
	public void onChannelSync(Event event, Channel channel) {
	}

	@Override
	public void onMemberJoined(Event event, Channel channel) {
		if (Config.instance().getRecordType() == RecordType.CUSTOMASYNC.getValue()) {
			memberChannelMap.put(event.getUid(), channel.getCid());
		}
	}

	@Override
	public void onMemberLeft(Event event, Channel channel) {
		if (Config.instance().getRecordType() == RecordType.CUSTOMASYNC.getValue()) {
			memberChannelMap.remove(event.getUid());
			if (memberRecorderMap.containsKey(event.getUid())) {
				memberRecorderMap.remove(event.getUid());
			}
		} else {
			String cid = channel.getCid();
			
			//当前channel中只有一个人时，用户不会上传媒体流，停止录像
			synchronized (this) {
				if (channel.getMembers().size() < 2 && recorderMap.get(cid) != null) {
					logger.info("stop recorder,cid={}", cid);
					recorderMap.remove(cid);
				}
			}
		}
	}

	@Override
	public void onMemberKicked(Event event, Channel channel) {
		if (Config.instance().getRecordType() == RecordType.CUSTOMASYNC.getValue()) {
			memberChannelMap.remove(event.getUid());
			if (memberRecorderMap.containsKey(event.getUid())) {
				memberRecorderMap.remove(event.getUid());
			}
		} else {
			String cid = channel.getCid();
			
			//当前channel中只有一个人时，用户不会上传媒体流，停止录像
			synchronized (this) {
				if (channel.getMembers().size() < 2 && recorderMap.get(cid) != null) {
					logger.info("stop recorder,cid={}", cid);
					recorderMap.remove(cid);
				}
			}
		}
	}

	@Override
	public void onMemberTypeUpdated(Event event, Channel channel) {
	}

	@Override
	public void onMediaServerSelected(Event event, Channel channel) {
		if (recorderMap.containsKey(channel.getCid())
				|| Config.instance().getRecordType() != RecordType.AUTOSYNC.getValue()) {
			return;
		}

		String[] mediaServerAddr = String.valueOf(event.getData()).split(":");
		String host = mediaServerAddr[0];
		String port = mediaServerAddr[1];

		// 连接到指定的mediaserver，进行录像
		synchronized (this) {
			try {
				Recorder recorder = new Recorder(Config.instance().getAppKey(), channel.getCid(), host, port,
						channel.getUniqueKey(), null, null);
				recorder.start();
				recorderMap.put(channel.getCid(), recorder);
			} catch (Exception e) {
				logger.error("start recorder error", e);
			}
		}
	}

	@Override
	public void onChannelCreated(Event event, Channel channel) {
	}

	@Override
	public void onChannelDestroyed(Event event, Channel channel) {
		String cid = channel.getCid();
		// 当前channel被移除时，停止录像
		synchronized (this) {
			if (recorderMap.containsKey(cid)) {
				logger.info("stop recorder,cid={}", cid);
				recorderMap.remove(cid);
			}
		}
	}

	@Override
	public void onWhiteBoardCreated(Event event, Channel channel) {
	}

	public Channel getChannelByUid(String uid) {
		if (memberChannelMap.containsKey(uid)) {
			return ChannelManager.instance().getChannel(memberChannelMap.get(uid));
		}
		return null;
	}

	public boolean startRecord(String uid, Channel channel, String fileName) {
		try {
			if (checkRecorderIsExsit(uid, channel)) {
				return true;
			}
			String mediaServerAddr = getMediaServerAddrByChannel(channel);
			if (mediaServerAddr == null) {
				return false;
			}
			String[] arrayAddr = mediaServerAddr.split(":");
			String host = arrayAddr[0];
			String port = arrayAddr[1];
			Recorder recorder = new Recorder(Config.instance().getAppKey(), channel.getCid(), host, port,
					channel.getUniqueKey(), uid, fileName);
			recorder.start();
			memberRecorderMap.put(uid, recorder);
			return true;
		} catch (Exception e) {
			logger.error("server start record failed!");
			return false;
		}
	}

	public boolean stopRecord(String uid) {
		try {
			Recorder recorder = memberRecorderMap.get(uid);
			if (recorder != null) {
				recorder.stop();
			}
			memberRecorderMap.remove(uid);
			return true;
		} catch (Exception e) {
			logger.error("server stop record failed!", e);
			return false;
		}
	}

	private String getMediaServerAddrByChannel(Channel channel) {
		for (Member member : channel.getMembers()) {
			if (member != null && member.getMediaServer() != null) {
				return member.getMediaServer();
			}
		}
		return null;
	}
	
	private boolean checkRecorderIsExsit(String uid, Channel channel) throws IOException {
		Recorder recorder = memberRecorderMap.get(uid);
		if (recorder == null) {
			return false;
		}
		
		if (!recorder.checkIsSameRecord(channel.getCid(), channel.getUniqueKey())) {
			memberRecorderMap.remove(uid);
			recorder.stop();
			return false;
		}
		
		return true;
	}
}
