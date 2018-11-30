package cn.rongcloud.rtc.example.channelsync;

import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.Event;

public interface ChannelEventListener {

	/**
	 * 同步房间信息事件，每分钟同步一次。
	 * @param event
	 * @param channel
	 */
	void onChannelSync(Event event, Channel channel);

	/**
	 * 成员加入房间事件，当有成员加入房间时会触发此事件
	 * @param event
	 * @param channel
	 */
	void onMemberJoined(Event event, Channel channel);

	/**
	 * 成员离开房间事件，当有成员离开房间时会触发此事件。
	 * @param event
	 * @param channel
	 */
	void onMemberLeft(Event event, Channel channel);

	/**
	 * 成员被移除房间事件，当客户端心跳超时（比如直接杀掉客户端进程），服务器将会此成员移除房间。
	 * @param event
	 * @param channel
	 */
	void onMemberKicked(Event event, Channel channel);

	/**
	 * 成员的角色类型发生了变更，比如由观察者变为与会者。
	 * @param event
	 * @param channel
	 */
	void onMemberTypeUpdated(Event event, Channel channel);

	/**
	 * 成员选定mediaserver
	 * @param event
	 * @param channel
	 */
	void onMediaServerSelected(Event event, Channel channel);

	/**
	 * 房间创建事件，当有成员加入房间，如果当前此房间不存在则自动创建。此时会触发此事件。
	 * @param event
	 * @param channel
	 */
	void onChannelCreated(Event event, Channel channel);

	/**
	 * 当最后一个人离开房间，此房间从server的内存中清除。此时会触发此事件。
	 * @param event
	 * @param channel
	 */
	void onChannelDestroyed(Event event, Channel channel);

	/**
	 * 房间中某成员创建白板会触发此事件。
	 * @param event
	 * @param channel
	 */
	void onWhiteBoardCreated(Event event, Channel channel);
}
