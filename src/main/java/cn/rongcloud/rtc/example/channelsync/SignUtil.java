package cn.rongcloud.rtc.example.channelsync;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpRequest;

import cn.rongcloud.rtc.example.config.Config;

public class SignUtil {

	public static void addSign(HttpRequest request){
		
		String nonce = String.valueOf((int) (Math.random() * 10000000));
		String timestamp = String.valueOf(System.currentTimeMillis());
		request.addHeader("App-Key", Config.instance().getAppKey());
		request.addHeader("Nonce", nonce);
		request.addHeader("Timestamp", timestamp);
		request.addHeader("Signature", SignUtil.hexSHA1(Config.instance().getSecret(), nonce, timestamp));
	}
	
	
	public static boolean checkSign(HttpServletRequest request){
		
		String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String signature = request.getHeader("signature");
        String sign = SignUtil.hexSHA1(Config.instance().getSecret(), nonce, timestamp);

        if(sign.equalsIgnoreCase(signature)){
        	return true;
        }
        
        return false;
	}
	
	public static String hexSHA1(String secret, String nonce, String timestamp) {
		StringBuilder sb = new StringBuilder();
		sb.append(secret).append(nonce).append(timestamp);
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(sb.toString().getBytes("utf-8"));
			byte[] digest = md.digest();
			return byteToHexString(digest);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String byteToHexString(byte[] bytes) {
		return String.valueOf(Hex.encodeHex(bytes));
	}

    /*public static void main(String[] args) {
	    System.out.print(SignUtil.hexSHA1("OYjzdrxMmUOmq", "666666", "666666"));

    }*/
}
