package cn.rongcloud.rtc.example.channelsync.domain;

/**
 * Created by kang on 2018/7/5.
 */
public class Member {

	private String uid;
    private String mediaServer;
    private int memberType;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMediaServer() {
        return mediaServer;
    }

    public void setMediaServer(String mediaServer) {
        this.mediaServer = mediaServer;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }
}
