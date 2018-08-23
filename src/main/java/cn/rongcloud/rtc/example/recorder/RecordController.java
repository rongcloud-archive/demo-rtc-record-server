package cn.rongcloud.rtc.example.recorder;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.rongcloud.rtc.example.config.Config;

@Controller
public class RecordController {
	
	private String baseDir = Config.instance().getRecordSaveDir();
	
	@RequestMapping("/record/**")
	public Object recordFileIndex(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String uri = req.getRequestURI();
		String fileName = uri.substring(7);
		
		File file = new java.io.File(baseDir + fileName);
		if (!file.exists()) {
			resp.sendError(404,Config.instance().getRecordSaveDir() + fileName +"error!");
			return null;
		}

		if (file.isDirectory()) {
			ModelAndView mav = new ModelAndView("recordList");
			mav.addObject("dir", "/record" + fileName);
			mav.addObject("fileList", file.list());
			return mav;
		} else {
			return new ModelAndView("forward:/recordDownload/" + fileName);
		}
	}		
}
