package com.study.mms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.study.mms.model.User;
import com.study.mms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SendEmail {
	// JavaMailSender 의존성 주입
	private final JavaMailSender mailSender;
	private final UserRepository userRepository;

	// 이메일 인증 보내는 이메일
	private static final String FROM_ADDRESS = "dbfl00027@gmail.com"; // application.yml에서도 똑같이 설정 필수.
	// 인증키 사이즈 (영어[소문자, 대문자], 숫자) 랜덤
	private static int authSize = 8;

	// 이메일 인증 보내는 이메일
	// 이메일 인증 보내는 이메일
	public Map<String, Object> sendEmailWithAuthCode(String email, HttpServletRequest req) {

		Map<String, Object> responseMap = new HashMap<>();
		
		// 이메일 유효성 검사 (빈 값 또는 형식 확인)
		if (email == null || email.trim().isEmpty()) {

			responseMap.put("status", "fail");
			responseMap.put("message", "이메일 주소가 입력되지 않았습니다.");
			return responseMap; // 이메일이 비어 있으면 실패 응답 반환
		}

		// 동일한 이메일이 존재하는지 확인 후 메일 발송
		List<User> users = userRepository.findAllByEmail(email);
		if (!users.isEmpty()) {
			responseMap.put("status", "fail");
			responseMap.put("message", "동일한 이메일이 존재합니다.");
		} else {
			String authKey = getAuthCode();
			int validTimeInSeconds = 180; // Code valid for 180 seconds (3 minutes)

			// 이메일 전송
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setFrom(FROM_ADDRESS);
			message.setSubject("[MMS-모두모여스터디] 인증번호");
			message.setText("인증번호: [" + authKey + "]");
			mailSender.send(message); // 메세지 전달

			// 세션에 인증코드와 관련 정보 저장
			HttpSession session = req.getSession();
			session.setAttribute("authCode", authKey);
			session.setAttribute("authEmail", email); // 이메일 저장하여 인증 확인 시 사용
			session.setAttribute("authCodeGeneratedTime", System.currentTimeMillis()); // 인증 코드 생성 시간 저장
			session.setAttribute("authCodeValidTime", validTimeInSeconds); // 인증 코드 유효 시간 저장

			responseMap.put("status", "success");
			responseMap.put("message", "인증코드가 발송되었습니다.");
			responseMap.put("validTime", validTimeInSeconds); // 남은 유효 시간 반환
		}

		return responseMap;
	}

	// 이메일 인증키 랜덤생성 (6자리 숫자)
	public String getAuthCode() {
		Random rnd = new Random();
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < 6; i++) {
			temp.append(rnd.nextInt(10)); // 0-9 사이의 숫자를 추가
		}
		return temp.toString();
	}

	// 인증키 확인 메소드
	public Map<String, Object> verifyAuthCode(String key, String email, HttpServletRequest req) {
		Map<String, Object> responseMap = new HashMap<>();

		HttpSession session = req.getSession();
		String originalCode = (String) session.getAttribute("authCode");
		int returnCode = -1;
		String accountResult = null;

		// 세션에 저장된 코드와 입력된 코드 비교
		if (originalCode != null && originalCode.equals(key)) {
			List<User> users = userRepository.findAllByEmail(email);
			if (!users.isEmpty()) {
				// 첫 번째 사용자 가져오기
				User user = users.get(0);
				accountResult = user.getNickname(); // 해당 사용자의 닉네임 반환
				returnCode = 1; // 인증 성공 코드
			} else {
				returnCode = -2; // 해당 이메일로 등록된 사용자가 없는 경우
			}
		} else {
			returnCode = -3; // 인증 코드가 일치하지 않는 경우
		}

		// 결과 설정
		responseMap.put("status", returnCode == 1); // 인증 성공 여부 (true/false)
		responseMap.put("code", returnCode);
		if (returnCode == 1) {
			responseMap.put("message", "인증이 완료되었습니다.");
			responseMap.put("account", accountResult); // 인증 성공 시 account 정보 추가
		} else if (returnCode == -2) {
			responseMap.put("message", "해당 이메일로 등록된 사용자가 없습니다.");
		} else if (returnCode == -3) {
			responseMap.put("message", "인증 코드가 일치하지 않습니다.");
		} else {
			responseMap.put("message", "인증에 실패했습니다.");
		}

		return responseMap;
	}
}
