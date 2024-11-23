package com.study.mms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.util.ImageUploader;

@Controller
public class CkeditorApiController {

	// ck 에디터 temp 파일에 이미지 임시 저장
	@ResponseBody
	@PostMapping("/user/uploadImage")
	public void uploadImage(@RequestParam("boardImg") MultipartFile multi, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		JSONObject outData = new JSONObject();
		outData.put("uploaded", true);
		outData.put("url", ImageUploader.uploadImage(multi, req, "/upload/temp/")); // 임시폴더로 경로지정
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().print(outData.toString()); // json("uploaded"=true, "url"=업로드된 이미지 경로) 반환
	}

}
