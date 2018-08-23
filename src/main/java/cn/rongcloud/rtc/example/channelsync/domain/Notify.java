package cn.rongcloud.rtc.example.channelsync.domain;

/**
 {
	"appid": "1234567890 abcdefg ",
	"cid": "xxxx",
	"event": {
		 "eventType": 1,
		 "uid": "xxxxx",
		 "data": "xxxxx",
		 "timestamp": 111111111
	},
	"channelInfo": {
		 "members": 
		 [
			 {
			 	"uid": "cccc",
			 	"mediaServer": "",
			 	"memberType": 1,
			 	"talkType ": 1
			 },
			 {
			 	"uid": "cccc",
			 	"mediaServer": "",
			 	"memberType": 1,
			 	"talkType ": 1
			 }
		 ]
	 }
 }

 */
public class Notify {

    public String appid;
    public String cid;
    public Event event;
    public Channel channelInfo;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Channel getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(Channel channelInfo) {
        this.channelInfo = channelInfo;
    }
}
