package cn.rongcloud.rtc.example.channelsync;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import cn.rongcloud.rtc.example.channelsync.domain.Notify;
import cn.rongcloud.rtc.example.channelsync.domain.ResponseEntity;
import cn.rongcloud.rtc.example.config.Config;

/**
 * Created by kang on 2018/6/22.
 */
@CrossOrigin
@Controller
public class ChannelSyncController {

	private static Logger logger = LoggerFactory.getLogger(ChannelSyncController.class);

	private static Gson gson = new Gson();

	@RequestMapping("/recv")
	@ResponseBody
	public ResponseEntity recvChannelNotify(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		logger.info(body);

		Notify notify = gson.fromJson(body, Notify.class);

		String appid = notify.getAppid();
		if (!Config.instance().getAppKey().equals(appid)) {
			logger.warn("appid error {}", appid);
			response.setStatus(403);
			return new ResponseEntity(403, "appid error");
		}

		if (!SignUtil.checkSign(request)) {
			logger.warn("sign error");
			response.setStatus(403);
			return new ResponseEntity(403, "sign error");
		}

		ChannelManager.instance().onChannelNotify(notify);

		return new ResponseEntity(200, "ok");
	}

	@RequestMapping("/channelInfo")
	public Object channelInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ModelAndView mav = new ModelAndView("channelinfo");
		mav.addObject("channelinfo", ChannelManager.instance().getChannelInfo());
		return mav;
	}
}
