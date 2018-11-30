package cn.rongcloud.rtc.example.recorder.customrecord;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import cn.rongcloud.rtc.example.channelsync.domain.Channel;
import cn.rongcloud.rtc.example.channelsync.domain.RecordType;
import cn.rongcloud.rtc.example.channelsync.domain.ResponseEntity;
import cn.rongcloud.rtc.example.config.Config;
import cn.rongcloud.rtc.example.recorder.RecordManager;

@RestController
public class CustomRecordController {

	private static Gson gson = new Gson();

	@RequestMapping(value = "/customrecord/start", method = RequestMethod.POST)
	public void startCustomRecord(String uid, String filename, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (Config.instance().getRecordType() != RecordType.CUSTOMASYNC.getValue()) {
			response.sendError(403, "server does not support this record type!");
			return;
		}
		if (uid == null || uid.length() == 0) {
			response.sendError(405, "uid can not be empty!");
			return;
		}
		Channel channel = RecordManager.instance().getChannelByUid(uid);
		if (channel == null) {
			response.sendError(404, "the user is not joining a call!");
			return;
		}

		boolean bIsSuc = RecordManager.instance().startRecord(uid, channel, filename);
		if (bIsSuc) {
			sendResponse(200, "OK", response);
		} else {
			response.sendError(500, "start record failed!");
		}
	}

	@RequestMapping(value = "/customrecord/stop", method = RequestMethod.POST)
	public void stopCustomRecord(String uid, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (Config.instance().getRecordType() != RecordType.CUSTOMASYNC.getValue()) {
			response.sendError(403, "server does not support this record type!");
			return;
		}
		if (uid == null || uid.length() == 0) {
			response.sendError(405, "uid can not be empty!");
			return;
		}

		boolean bIsSuc = RecordManager.instance().stopRecord(uid);
		if (bIsSuc) {
			sendResponse(200, "OK", response);
		} else {
			response.sendError(500, "start record failed!");
		}
	}

	private void sendResponse(int code, String msg, HttpServletResponse response) throws IOException  {
		response.setContentType("application/json");
		ResponseEntity entity = new ResponseEntity(code, msg);
		response.getWriter().write(gson.toJson(entity));
		response.getWriter().flush();
	}
}
