package cn.rongcloud.rtc.example.channelsync;

import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.Event;

public interface ChannelEventListener {

	void onChannelSync(Event event, Channel channel);

	void onMemberJoined(Event event, Channel channel);

	void onMemberLeft(Event event, Channel channel);

	void onMemberKicked(Event event, Channel channel);

	void onMemberTypeUpdated(Event event, Channel channel);

	void onMediaServerSelected(Event event, Channel channel);

	void onChannelCreated(Event event, Channel channel);

	void onChannelDestroyed(Event event, Channel channel);

	void onWhiteBoardCreated(Event event, Channel channel);
}
