package com.study.mms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	public static String uploadImagesPath;
	public static boolean isLocal;

//	@Autowired
//	private UserInterceptor userInterceptor;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			isLocal = true;
			uploadImagesPath = "C:/Users/82103/Desktop/projects/mmsServer"; // 찾을 파일 경로

			// upload라는 요청이 들어오면 uploadImagesPath 이 경로 사용
			// upload/라는 명칭이 있으면 /upload/img 폴더에서 이미지를 찾아옴
			// 그래서 upload/upload/img 라고 사용 해야 함

			registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadImagesPath + "/")
					.setCachePeriod(2_592_000);
		} else {
			isLocal = false;
			uploadImagesPath = "/home/ubuntu/tomcat/webapps/"; // 라이브 경로
			registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadImagesPath + "/")
					.setCachePeriod(2_592_000);
		}
	}

//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(userInterceptor).addPathPatterns("/**");
//	}
//	

}
