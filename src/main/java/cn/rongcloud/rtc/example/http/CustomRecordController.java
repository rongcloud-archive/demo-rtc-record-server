package cn.rongcloud.rtc.example.http;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.ResponseEntity;
import cn.rongcloud.rtc.example.recorder.RecordManager;

@RestController
public class CustomRecordController {

	private static Gson gson = new Gson();

	@RequestMapping(value = "/customrecord/start", method = RequestMethod.POST)
	public void startCustomRecord(String uid, String filename, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");
		if (uid == null || uid.length() == 0) {
			sendResponse(405, "uid can not be empty!", response);
			return;
		}
		Channel channel = RecordManager.instance().getChannelByUid(uid);
		if (channel == null) {
			sendResponse(404, "the user is not joining a call!", response);
			return;
		}

		boolean bIsSuc = RecordManager.instance().startRecord(channel, filename);
		if (bIsSuc) {
			sendResponse(200, "start record suc!", response);
		} else {
			sendResponse(500, "start record failed!", response);
		}
	}

	@RequestMapping(value = "/customrecord/stop", method = RequestMethod.POST)
	public void stopCustomRecord(String uid, String filename, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");
		if (uid == null || uid.length() == 0) {
			sendResponse(405, "uid can not be empty!", response);
			return;
		}
		Channel channel = RecordManager.instance().getChannelByUid(uid);
		if (channel == null) {
			//会场结束时，server会自动结束录像。
			sendResponse(200, "stop record suc!", response);
			return;
		}
		boolean bIsSuc = RecordManager.instance().stopRecord(channel);
		if (bIsSuc) {
			sendResponse(200, "stop record suc!", response);
		} else {
			sendResponse(500, "stop record failed!", response);
		}
	}

	private void sendResponse(int code, String msg, HttpServletResponse response) throws IOException  {
		ResponseEntity entity = new ResponseEntity(code, msg);
		response.getWriter().write(gson.toJson(entity));
		response.getWriter().flush();
	}
}
