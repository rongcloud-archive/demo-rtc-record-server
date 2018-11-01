package cn.rongcloud.rtc.example.channelsync;

import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.Event;
import cn.rongcloud.rtc.example.channelsync.domain.Notify;
import cn.rongcloud.rtc.example.channelsync.domain.ResponseEntity;
import cn.rongcloud.rtc.example.config.Config;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

	private static Logger logger = LoggerFactory.getLogger(ChannelManager.class);

	private static ChannelManager instance = new ChannelManager();

	private static Gson gson = new Gson();

	private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

	private List<ChannelEventListener> listeners = new ArrayList<>();

	private CloseableHttpClient httpclient = HttpClients.createDefault();

	private ChannelManager() {
	}

	public static ChannelManager instance() {
		return instance;
	}

	public void onChannelNotify(Notify notify) throws IOException {
		String cid = notify.getCid();
		Channel channelInfo = notify.getChannelInfo();
		channelInfo.setCid(cid);

		if (notify.getEvent().getEventType() == EventType.CHANNEL_DESTROYED) {
			channelMap.remove(cid);
		} else {
			channelMap.put(cid, channelInfo);
		}

		dispatch(notify.getEvent(), channelInfo);
	}

	private void dispatch(Event event, Channel channel) {

		for (ChannelEventListener listener : listeners) {
			switch (event.getEventType()) {
			case EventType.CHANNEL_SYNC:
				listener.onChannelSync(event, channel);
				break;
			case EventType.WHITEBOARD_CREATED:
				listener.onWhiteBoardCreated(event, channel);
				break;
			case EventType.CHANNEL_CREATED:
				listener.onChannelCreated(event, channel);
				break;
			case EventType.CHANNEL_DESTROYED:
				listener.onChannelDestroyed(event, channel);
				break;
			case EventType.MEDIA_SERVER_SELECTED:
				listener.onMediaServerSelected(event, channel);
				break;
			case EventType.MEMBER_JOINED:
				listener.onMemberJoined(event, channel);
				break;
			case EventType.MEMBER_KICKED:
				listener.onMemberKicked(event, channel);
				break;
			case EventType.MEMBER_LEFT:
				listener.onMemberLeft(event, channel);
				break;
			case EventType.MEMBER_TYPE_UPDATED:
				listener.onMemberTypeUpdated(event, channel);
				break;
			default:
				logger.error("unknow event type {}", event.getEventType());
				break;
			}
		}
	}

	public void addChannelEventListener(ChannelEventListener listener) {
		listeners.add(listener);
	}

	public void removeChannelEventListener(ChannelEventListener listener) {
		listeners.remove(listener);
	}

	public void startChannelSync() {
		// 会场同步订阅状态的有效期为5分钟
		// 为了防止过期，每两分钟订阅一次
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						doSubscribe();
						Thread.sleep(Config.REGIST_INTERVAL);
					} catch (Exception e) {
						logger.error("do regist error", e);
					}
				}
			}
		}).start();
	}

	private boolean doSubscribe() throws Exception {

		HttpPost request = subscribeRequest();

		try (CloseableHttpResponse response = httpclient.execute(request)) {

			String result = getResult(response);
			if(response.getStatusLine().getStatusCode() != ResponseEntity.CODE_OK){
			    logger.error("do regist fail , {}",result);
			    return false;
            }
            ResponseEntity responseEntity = gson.fromJson(result, ResponseEntity.class);

            if (responseEntity.getCode() == ResponseEntity.CODE_OK) {
				return true;
			} else {
				logger.error(" do regist fail , {}", responseEntity.getMsg());
				return false;
			}
		}
	}

	private static HttpPost subscribeRequest() throws UnsupportedEncodingException {
		String gatewayAddr = Config.instance().getGatewayAddr();
		String registAddr = gatewayAddr + (gatewayAddr.endsWith("/") ? "channel/subscribe" : "/channel/subscribe");
		String recvAddr = Config.instance().getRecvAddr();
		logger.info("registAddr={},recvAddr={}", registAddr, recvAddr);

		HttpPost request = new HttpPost(registAddr);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
		request.setConfig(requestConfig);
		SignUtil.addSign(request);
        List <NameValuePair> nvps = new ArrayList <>();
        nvps.add(new BasicNameValuePair("addr", recvAddr));
        request.setEntity(new UrlEncodedFormEntity(nvps));
		return request;
	}

	private static String getResult(CloseableHttpResponse response) throws Exception {
		String line = null;
		StringBuilder sb = new StringBuilder();
		HttpEntity entity = response.getEntity();
		InputStream content = entity.getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content, "utf-8"));
		while ((line = bufferedReader.readLine()) != null)
			sb.append(line);

		return sb.toString();
	}

	public Collection<Channel> getChannelList() {
		Collection<Channel> values = channelMap.values();
		return values;
	}

	public String getChannelInfo() {
		Collection<Channel> channelList = getChannelList();
		String s = gson.toJson(channelList);
		return s;
	}
	
	public Channel getChannel(String cid) {
		return channelMap.get(cid);
	}
}
