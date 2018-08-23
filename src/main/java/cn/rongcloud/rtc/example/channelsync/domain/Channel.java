package cn.rongcloud.rtc.example.channelsync.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 2018/7/5.
 */
public class Channel {
	
	private String cid;
	private String uniqueKey;
    private List<Member> members = new ArrayList<>();

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
