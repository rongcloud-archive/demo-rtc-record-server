package cn.rongcloud.rtc.example.recorder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.rongcloud.rtc.example.config.Config;

@Component
@WebFilter(urlPatterns = "/recordDownload/**",filterName = "recordDownloadFilter")
@Configuration
public class RecordDownloadFilter  extends WebMvcConfigurerAdapter implements Filter{

	Logger logger = LoggerFactory.getLogger(RecordDownloadFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String uri = ((HttpServletRequest) request).getRequestURI();
		
		if (uri.startsWith("/recordDownload")) {
			logger.info("record file:" + uri);
			HttpServletResponse res = (HttpServletResponse) response;
			String[] strs = uri.split("/");
			res.setHeader("content-type", "application/octet-stream");
			res.setContentType("application/octet-stream");
			res.setHeader("Content-Disposition", "attachment;filename=" + strs[strs.length - 1]);
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// addResourceHandler是指你想在url请求的路径
		// addResourceLocations是图片存放的真实路径
		registry.addResourceHandler("/recordDownload/**")
				.addResourceLocations("file:" + Config.instance().getRecordSaveDir());
		super.addResourceHandlers(registry);
	}

	@Override
	public void destroy() {
	}

}
