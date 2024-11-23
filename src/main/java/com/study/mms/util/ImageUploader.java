package com.study.mms.util;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.config.WebMvcConfig;

public class ImageUploader {
	// 이미지 업로드
	public static String uploadImage(MultipartFile multipartFile, HttpServletRequest request, String path)
			throws Exception {
		String originFilename = multipartFile.getOriginalFilename(); // 업로드할 파일의 이름
		String extName = originFilename.substring(originFilename.lastIndexOf("."), originFilename.length()); // 업로드할 파일

		// 확장자
		String firstName = originFilename.substring(0, originFilename.lastIndexOf(".")); // 업로드할 파일의 이름(확장자 제외)
		String resourceSrc = WebMvcConfig.uploadImagesPath; // 현재 프로젝트의 경로

		String saveFileName = makeRandomString(10); // 랜덤 문자열 생성(10자리)
		saveFileName += firstName;
		saveFileName += extName; // 랜덤 문자열 뒤에 원래 파일 이름 추가.
									// 확장자 확인해서 이미지가 아닐 시의 예외상황 체크용으로 확장자를 따로 분리했음.

		
		byte[] data = multipartFile.getBytes();
		File file = new File(resourceSrc + path);
		if (!file.exists()) {
			file.mkdir();
		}
		FileOutputStream fos = new FileOutputStream(resourceSrc + path + saveFileName);
		fos.write(data);
		fos.close(); // 이미지 업로드 처리
		return path + saveFileName; // 업로드된 이미지 경로+이미지명 반환
	}

	// 파일이름 랜덤화용 함수
	public static String makeRandomString(int length) throws Exception {
		String text = "";
		String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		for (int i = 0; i < length; i++) {
			text += possible.charAt((int) Math.floor(Math.random() * possible.length()));
		}
		return text;
	}

	// 이미지 삭제
	public static void deleteImage(HttpServletRequest request, String image) throws Exception {
//		String resourceSrc = WebMvcConfig.isLocal ? request.getServletContext().getRealPath("")
//				: WebMvcConfig.uploadImagesPath;
		String resourceSrc = WebMvcConfig.isLocal ? WebMvcConfig.uploadImagesPath : WebMvcConfig.uploadImagesPath;

		File imgfile = new File(resourceSrc + image);

		// System.out.println(imgfile + "경로확인용");

		if (imgfile.exists()) {
			imgfile.delete();
		}
	}

	// 이미지 이동
	public static void moveImage(HttpServletRequest req, String fileName, String movePath) {
		String resourceSrc = WebMvcConfig.isLocal ? WebMvcConfig.uploadImagesPath : WebMvcConfig.uploadImagesPath;

		String oldFile = resourceSrc + "/upload/temp/" + fileName; // 임시폴더 경로
		String freshFile = resourceSrc + movePath + fileName; // 옴길 폴더의 경로

		File old = new File(oldFile);
		File fresh = new File(freshFile);
		try {
			FileUtils.moveFile(old, fresh); // 파일 이동 처리
		} catch (Exception e) {
		}
	}
}
