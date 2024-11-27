package com.study.mms.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.InquiryDTO;
import com.study.mms.dto.TodoDTO;
import com.study.mms.dto.UsersJoinDTO;
import com.study.mms.dto.joinRequestDTO;
import com.study.mms.dto.updatePasswordDTO;
import com.study.mms.model.Attendance;
import com.study.mms.model.Inquiry;
import com.study.mms.model.StudyGroupJoinRequest;
import com.study.mms.model.StudyGroupMember;
import com.study.mms.model.Todo;
import com.study.mms.model.User;
import com.study.mms.repository.AttendanceRepository;
import com.study.mms.repository.InquiryRepository;
import com.study.mms.repository.StudyGroupJoinRequestRepository;
import com.study.mms.repository.StudyGroupMemberRepository;
import com.study.mms.repository.TodoRepository;
import com.study.mms.repository.UserRepository;
import com.study.mms.util.ImageUploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository usersRepository;
	private final TodoRepository todoRepository;
	private final AttendanceRepository attendanceRepository;
	private final StudyGroupJoinRequestRepository studyGroupJoinRequestRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;
	private final InquiryRepository inquiryRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	// ================= < 회원가입 > =================

	// 이메일 인증 시 사용
	public Map<String, Object> verifyAuthCode(String key, HttpServletRequest req) {
		Map<String, Object> responseMap = new HashMap<>();
		HttpSession session = req.getSession();
		String originalCode = (String) session.getAttribute("authCode");
		Long generatedTime = (Long) session.getAttribute("authCodeGeneratedTime"); // 인증 코드 생성 시간 가져오기
		String authEmail = (String) session.getAttribute("authEmail");

		int returnCode = -1;

		// 현재 시간과 생성 시간을 비교하여 3분 이내인지 확인
		if (generatedTime != null) {
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - generatedTime;

			if (elapsedTime > 180000) {
				// 3분(180,000ms)이 지난 경우 인증 코드 만료
				returnCode = -4; // 인증 코드 만료 코드
				responseMap.put("status", false);
				responseMap.put("code", returnCode);
				responseMap.put("message", "인증 코드가 만료되었습니다. 다시 시도해 주세요.");
				return responseMap;
			}
		}

		if (originalCode != null && originalCode.equals(key)) {
			// 인증 성공 시 세션에 인증 완료 플래그 설정
			session.setAttribute("isAuthenticated", true); // 인증 성공 여부 저장
			returnCode = 1; // 인증 성공 코드
		} else {
			returnCode = -3; // 인증 코드 불일치 코드
		}

		responseMap.put("status", returnCode == 1);
		responseMap.put("code", returnCode);
		responseMap.put("message", returnCode == 1 ? "인증이 완료되었습니다." : "인증 코드가 일치하지 않습니다.");

		return responseMap;
	}

	// 회원가입
	@Transactional
	public Map<String, Object> joinUsers(UsersJoinDTO joinDTO, HttpServletRequest req) {

		Map<String, Object> returnMap = new HashMap<String, Object>();

		HttpSession session = req.getSession();

		// 이메일 인증 여부 확인
		Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
		String authEmail = (String) session.getAttribute("authEmail");

		// 인증되지 않았거나, 인증된 이메일이 회원가입 이메일과 다를 경우 에러 반환
		if (isAuthenticated == null || !isAuthenticated || !authEmail.equals(joinDTO.getEmail())) {
			returnMap.put("status", "fail");
			returnMap.put("message", "이메일 인증이 필요합니다.");
			return returnMap;
		}

		// 1. 사용자 이름 중복 확인 및 길이 확인
		if (joinDTO.getUsername() == null || joinDTO.getUsername().trim().isEmpty()) {
			returnMap.put("status", "fail");
			returnMap.put("message", "아이디를 입력해 주세요.");
			return returnMap;
		}

		if (joinDTO.getUsername().trim().length() > 10) {
			returnMap.put("status", "fail");
			returnMap.put("message", "아이디는 최대 10자 이하로 입력해 주세요.");
			return returnMap;
		}

		if (usersRepository.existsByUsername(joinDTO.getUsername())) {
			returnMap.put("status", "fail");
			returnMap.put("message", "이미 사용중인 아이디 입니다.");
			return returnMap;
		}
		// 닉네임 중복 확인
		if (joinDTO.getNickname() == null || joinDTO.getNickname().trim().isEmpty()) {
			returnMap.put("status", "fail");
			returnMap.put("message", "닉네임을 입력해 주세요.");
			return returnMap;
		}
		if (joinDTO.getNickname().trim().length() > 10) {
			returnMap.put("status", "fail");
			returnMap.put("message", "닉네임은 최대 10자 이하로 입력해 주세요.");
			return returnMap;
		}

		if (usersRepository.existsByNickname(joinDTO.getNickname())) {
			returnMap.put("status", "fail");
			returnMap.put("message", "이미 사용중인 닉네임 입니다.");
			return returnMap;
		}

		// 2. 이메일 중복 확인
		if (usersRepository.existsByEmail(joinDTO.getEmail())) {
			returnMap.put("status", "fail");
			returnMap.put("message", "이미 사용중인 이메일 입니다.");
			return returnMap;
		}

		// 2-1. 이메일 형식 확인
		String email = joinDTO.getEmail();
		if (!validateEmailFormat(email)) {
			returnMap.put("status", "fail");
			returnMap.put("message", "유효하지 않은 이메일 형식입니다.");
			return returnMap;
		}

		// 3. 비밀번호 일치 확인
		if (!joinDTO.getPassword1().equals(joinDTO.getPassword2())) {
			returnMap.put("status", "fail");
			returnMap.put("message", "비밀번호가 틀렸습니다.");
			return returnMap;
		}

		// 4. 비밀번호 유효성 검사
		String password = joinDTO.getPassword1();
		String passwordValidationMessage = validatePassword(password);
		if (passwordValidationMessage != null) {
			returnMap.put("status", "fail");
			returnMap.put("message", passwordValidationMessage);
			return returnMap;
		}

		// 5. 비밀번호 암호화 및 사용자 정보 저장
		String salt = BCrypt.gensalt();
		String encPassword = BCrypt.hashpw(password, salt);

		User user = new User();
		user.setUsername(joinDTO.getUsername());
		user.setEmail(joinDTO.getEmail());
		user.setNickname(joinDTO.getNickname());
		user.setSalt(salt);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		user.setImg_name("default_img"); // 기본 이미지 저장
		user.setImg_path("/img/defaultImg.png");
		usersRepository.save(user);

		returnMap.put("status", "success");
		returnMap.put("message", "회원가입이 완료되었습니다.");
		return returnMap;
	}

	// 비밀번호 유효성 검사 함수
	private String validatePassword(String password) {
		// 최소 8자, 하나 이상의 대문자, 소문자, 숫자, 특수 문자 필요
		if (password.length() < 8) {
			return "비밀번호는 최소 8자 이상이어야 합니다.";
		}

		if (!Pattern.compile("[A-Z]").matcher(password).find()) {
			return "비밀번호에는 최소 하나의 대문자가 포함되어야 합니다.";
		}

		if (!Pattern.compile("[a-z]").matcher(password).find()) {
			return "비밀번호에는 최소 하나의 소문자가 포함되어야 합니다.";
		}

		if (!Pattern.compile("[0-9]").matcher(password).find()) {
			return "비밀번호에는 최소 하나의 숫자가 포함되어야 합니다.";
		}

		if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
			return "비밀번호에는 최소 하나의 특수 문자가 포함되어야 합니다.";
		}
		return null; // 유효성 검사 통과
	}

	// 이메일 형식 유효성 검사 함수
	private boolean validateEmailFormat(String email) {
		// 이메일 정규 표현식
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		return Pattern.compile(emailRegex).matcher(email).matches();
	}

	// ================= < 마이페이지 Todo List > =================

	// 오늘의 할일 저장
	@Transactional
	public Map<String, Object> createUserTodoList(Authentication authentication, String description) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// PrincipalDetail에서 Users 객체를 가져옴
			User user = principalDetail.getUser();

			// Todo 객체를 생성 및 저장
			Todo todo = Todo.builder().description(description).user(user).build();
			todoRepository.save(todo);

			// Todo 객체를 DTO로 변환 (직렬화 문제 방지)
			TodoDTO todoDTO = new TodoDTO(todo.getId(), todo.getDescription(), todo.isCompleted());

			// 성공 시 반환할 값 설정
			returnMap.put("status", "success");
			returnMap.put("message", "성공적으로 생성되었습니다.");
			returnMap.put("data", todoDTO);
		} catch (Exception e) {
			// 예외 발생 시 처리
			returnMap.put("status", "error");
			returnMap.put("message", "생성 중 오류가 발생했습니다.");
			returnMap.put("error", e.getMessage()); // 예외 메시지를 반환할 수도 있음
		}

		// 반환
		return returnMap;
	}

	// 해당하는 유저의 오늘의 할일 데이터 반환
	@Transactional
	public Map<String, Object> getUserTodoList(Authentication authentication) {
		Map<String, Object> returnMap = new HashMap<>();

		try {

			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// PrincipalDetail에서 User 객체를 가져옴
			User user = principalDetail.getUser();

			// 해당 유저의 할일 목록 조회
			List<Todo> todos = todoRepository.findByUser(user);

			// Todo 객체를 DTO로 변환
			List<TodoDTO> todoDTOList = todos.stream()
					.map(todo -> new TodoDTO(todo.getId(), todo.getDescription(), todo.isCompleted()))
					.collect(Collectors.toList());

			// 성공적인 응답 데이터 설정
			returnMap.put("status", "success");
			returnMap.put("message", "조회 성공");
			returnMap.put("data", todoDTOList);

		} catch (Exception e) {
			// 예외 발생 시 처리
			returnMap.put("status", "error");
			returnMap.put("message", "조회 중 오류 발생");
			returnMap.put("error", e.getMessage()); // 예외 메시지를 반환할 수 있음
		}

		// 응답 반환
		return returnMap;
	}

	@Transactional
	public Map<String, Object> completeUserTodoList(Authentication authentication, Integer todoId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			// Authentication 객체에서 PrincipalDetail 객체를 가져옴
			PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

			// 인증된 사용자 정보 가져오기
			User user = principalDetail.getUser();

			// todoId로 Todo 항목 조회
			Todo todo = todoRepository.findById(todoId)
					.orElseThrow(() -> new IllegalArgumentException("해당 ID의 할일을 찾을 수 없습니다."));

			// 인증된 사용자가 이 할일의 소유자인지 확인
			if (!todo.getUser().getId().equals(user.getId())) {
				returnMap.put("status", "error");
				returnMap.put("message", "해당 할일을 완료할 권한이 없습니다.");
				return returnMap;
			}

			// 완료 상태로 변경
			todo.setCompleted(true);
			todoRepository.save(todo); // 변경 사항 저장

			// Todo 객체를 DTO로 변환 (직렬화 문제 방지)
			TodoDTO todoDTO = new TodoDTO(todo.getId(), todo.getDescription(), todo.isCompleted());

			// 성공적인 응답 데이터 설정
			returnMap.put("status", "success");
			returnMap.put("message", "할일이 완료되었습니다.");
			returnMap.put("data", todoDTO);

		} catch (IllegalArgumentException e) {
			// IllegalArgumentException 발생 시 처리
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage()); // 예외 메시지 설정
		} catch (Exception e) {
			// 기타 예외 처리
			returnMap.put("status", "error");
			returnMap.put("message", "완료 처리 중 오류가 발생했습니다.");
			returnMap.put("error", e.getMessage());
		}

		// 응답 반환
		return returnMap;
	}

	// ================= < 마이페이지 출석체크 > =================
	// 출석체크
	@Transactional
	public Map<String, Object> addUserAttendance(Authentication authentication) {
		Map<String, Object> returnMap = new HashMap<>();
		// Authentication 객체에서 PrincipalDetail 객체를 가져옴
		PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

		// 인증된 사용자 정보 가져오기
		User user = principalDetail.getUser();

		// 오늘 날짜로 이미 출석체크가 되어있는지 확인
		Optional<Attendance> existingAttendance = attendanceRepository.findByUserAndAttendance(user, LocalDate.now());
		if (existingAttendance.isPresent()) {
			// 이미 출석체크가 된 경우
			returnMap.put("status", "failure");
			returnMap.put("message", "이미 오늘 출석체크가 완료되었습니다.");
		} else {

			// 새로운 출석체크 기록 생성
			Attendance attendance = Attendance.builder().user(user).build();
			// 출석체크 저장
			attendanceRepository.save(attendance);

			returnMap.put("status", "success");
			returnMap.put("message", "출석체크가 완료되었습니다.");
		}

		return returnMap;
	}

	// 출석체크 리스트 반환
	@Transactional
	public Map<String, Object> getUserAttendance(Authentication authentication) {
		Map<String, Object> returnMap = new HashMap<>();
		// Authentication 객체에서 PrincipalDetail 객체를 가져옴
		PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();

		// 인증된 사용자 정보 가져오기
		User user = principalDetail.getUser();

		// 사용자의 모든 출석 기록을 조회
		List<Attendance> attendanceList = attendanceRepository.findByUser(user);

		List<LocalDate> attendanceDateList = attendanceList.stream().map(Attendance::getAttendance)
				.collect(Collectors.toList());
		returnMap.put("status", "success");
		returnMap.put("data", attendanceDateList);
		return returnMap;
	}

	// 닉네임 유효성 검사
	private Map<String, Object> validateNickname(String nickname, User user) {
		Map<String, Object> response = new HashMap<>();

		// 닉네임이 null이거나 빈 문자열인 경우 예외 처리
		if (nickname == null || nickname.trim().isEmpty()) {
			response.put("status", "error");
			response.put("message", "닉네임을 입력해주세요.");
			return response;
		}

		// 닉네임 중복 검사 (현재 닉네임과 다른 경우에만 중복 검사 수행)
		if (!nickname.equals(user.getNickname())) {
			boolean isNicknameDuplicate = usersRepository.existsByNickname(nickname);
			if (isNicknameDuplicate) {
				response.put("status", "error");
				response.put("message", "이미 존재하는 닉네임입니다. 다른 닉네임을 입력해주세요.");
				return response;
			}
		}

		response.put("status", "success");
		return response;
	}

	// 사용자 정보 유효성 검사
	private Map<String, Object> validateUser(PrincipalDetail principalDetail) {
		Map<String, Object> response = new HashMap<>();
		User user = principalDetail.getUser();
		if (user == null) {
			response.put("status", "error");
			response.put("message", "이용자 정보를 찾을 수 없습니다.");
		} else {
			response.put("status", "success");
			response.put("user", user);
		}
		return response;
	}

	// 마이페이지 회원 정보 저장(기본 이미지로 변경한 경우)
	@Transactional
	public Map<String, Object> saveUserDefaultImgAndDatas(String imagePath, String nickname,
			PrincipalDetail principalDetai) {
		Map<String, Object> returnMap = new HashMap<>();

		// 사용자 유효성 검사
		Map<String, Object> userValidationResult = validateUser(principalDetai);
		if ("error".equals(userValidationResult.get("status"))) {
			return userValidationResult;
		}
		User user = (User) userValidationResult.get("user");

		// 닉네임 유효성 검사
		Map<String, Object> nicknameValidationResult = validateNickname(nickname, user);
		if ("error".equals(nicknameValidationResult.get("status"))) {
			return nicknameValidationResult;
		}

		try {
			// 이미지 경로에서 파일 이름만 추출
			String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);

			// 사용자 정보 업데이트
			user.setImg_name(imageName);
			user.setImg_path(imagePath);

			// 닉네임이 변경된 경우에만 업데이트
			if (!nickname.equals(user.getNickname())) {
				user.setNickname(nickname);
			}

			user.setImg_type(false);

			// 데이터베이스에 저장
			usersRepository.save(user);

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "변경되었습니다.");
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "변경 중 오류가 발생했습니다.");
		}

		return returnMap;
	}

	// 마이페이지 회원 정보 저장(자신의 이미지로 변경하는 경우)
	@Transactional
	public Map<String, Object> saveUserDatas(MultipartFile imagePath, String nickname, PrincipalDetail principalDetai,
			HttpServletRequest req) {
		Map<String, Object> returnMap = new HashMap<>();

		// 사용자 유효성 검사
		Map<String, Object> userValidationResult = validateUser(principalDetai);
		if ("error".equals(userValidationResult.get("status"))) {
			return userValidationResult;
		}
		User user = (User) userValidationResult.get("user");

		// 닉네임 유효성 검사
		Map<String, Object> nicknameValidationResult = validateNickname(nickname, user);
		if ("error".equals(nicknameValidationResult.get("status"))) {
			return nicknameValidationResult;
		}

		try {
			// 기존 이미지가 사용자 지정 이미지인 경우 삭제
			if (user.getImg_type() != false) {
				ImageUploader.deleteImage(req, user.getImg_path());
			}

			// 새로운 이미지 업로드 및 정보 설정
			String imageUrl = ImageUploader.uploadImage(imagePath, req, "/upload/profile/");
			user.setImg_name(imagePath.getOriginalFilename());
			user.setImg_type(true);
			user.setImg_path(imageUrl);

			// 닉네임이 변경된 경우에만 업데이트
			if (!nickname.equals(user.getNickname())) {
				user.setNickname(nickname);
			}

			// 데이터베이스에 저장
			usersRepository.save(user);

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "변경되었습니다.");
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "변경 중 오류가 발생했습니다.");
		}

		return returnMap;
	}

	@Transactional
	public Map<String, Object> saveUserNickNameDatas(String nickname, PrincipalDetail principalDetai) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			// 사용자 유효성 검사
			Map<String, Object> userValidationResult = validateUser(principalDetai);
			if ("error".equals(userValidationResult.get("status"))) {
				return userValidationResult;
			}

			User user = (User) userValidationResult.get("user");
			// 닉네임 유효성 검사
			Map<String, Object> nicknameValidationResult = validateNickname(nickname, user);
			if ("error".equals(nicknameValidationResult.get("status"))) {
				return nicknameValidationResult;
			}

			// 닉네임이 변경된 경우에만 업데이트
			if (!nickname.equals(user.getNickname())) {
				user.setNickname(nickname);
			}

			// 데이터베이스에 저장
			usersRepository.save(user);

			// 성공 응답 설정
			returnMap.put("status", "success");
			returnMap.put("message", "변경되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "변경 중 오류가 발생했습니다.");
		}

		return returnMap;
	}

	// 비밀번호 변경
	@Transactional
	public Map<String, Object> changeUserPassword(updatePasswordDTO passwordDTO, PrincipalDetail principalDetai) {

		Map<String, Object> returnMap = new HashMap<>();
		try {
			// 1. 입력값 검증
			if (passwordDTO.getPassword() == null || passwordDTO.getPassword().isEmpty()
					|| passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().isEmpty()
					|| passwordDTO.getPasswordCheck() == null || passwordDTO.getPasswordCheck().isEmpty()) {

				returnMap.put("status", "fail");
				returnMap.put("message", "모든 비밀번호 필드를 입력해야 합니다.");
				return returnMap;
			}

			// 사용자 유효성 검사
			Map<String, Object> userValidationResult = validateUser(principalDetai);
			if ("error".equals(userValidationResult.get("status"))) {
				return userValidationResult;
			}

			User user = (User) userValidationResult.get("user");

			// 기존 비밀번호 검증
			if (!passwordEncoder.matches(passwordDTO.getPassword(), user.getPassword())) {
				returnMap.put("status", "fail");
				returnMap.put("message", "기존 비밀번호가 일치하지 않습니다.");
				return returnMap;
			}

			// 비밀번호 일치 확인
			if (!passwordDTO.getNewPassword().equals(passwordDTO.getPasswordCheck())) {
				returnMap.put("status", "fail");
				returnMap.put("message", "비밀번호가 틀렸습니다.");
				return returnMap;
			}

			// 기존 비밀번호와 변경하는 비밀번호가 일치하는지 확인
			if (passwordEncoder.matches(passwordDTO.getNewPassword(), user.getPassword())) {
				returnMap.put("status", "fail");
				returnMap.put("message", "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
				return returnMap;
			}

			// 비밀번호 유효성 검사
			String password = passwordDTO.getNewPassword();
			String passwordValidationMessage = validatePassword(password);
			if (passwordValidationMessage != null) {
				returnMap.put("status", "fail");
				returnMap.put("message", passwordValidationMessage);
				return returnMap;
			}

			// password = "1234";

			// 비밀번호 암호화 및 사용자 정보 저장
			String salt = BCrypt.gensalt();
			String encPassword = BCrypt.hashpw(password, salt);

			user.setSalt(salt);
			user.setPassword(encPassword);
			usersRepository.save(user);

			returnMap.put("status", "success");
			returnMap.put("message", "비밀번호가 변경 되었습니다.");

		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "변경 중 오류가 발생했습니다.");
		}
		return returnMap;
	}

	// 마이페이지 내가 신청한 스터디 그룹의 리스트 반환
	public Map<String, Object> getSignUpStudyLists(PrincipalDetail principalDetai) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		// 사용자 유효성 검사
		Map<String, Object> userValidationResult = validateUser(principalDetai);
		if ("error".equals(userValidationResult.get("status"))) {
			return userValidationResult;
		}

		User user = (User) userValidationResult.get("user");

		List<StudyGroupJoinRequest> studyGroupJoinRequests = studyGroupJoinRequestRepository.findByUser(user);

		// StudyGroupJoinRequest 엔티티를 joinRequestDTO로 빌더를 통해 매핑
		List<joinRequestDTO> dtoList = studyGroupJoinRequests.stream().map(joinRequestDTO::fromEntity)
				.collect(Collectors.toList());

		returnMap.put("status", "success");
		returnMap.put("data", dtoList);
		returnMap.put("message", "리스트가 조회되었습니다. ");

		return returnMap;
	}

	// 마이페이지 내가 가입한 스터디 그룹 리스트 반환
	public Map<String, Object> getJoinStudyLists(PrincipalDetail principalDetai) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<>();

		// 사용자 유효성 검사
		Map<String, Object> userValidationResult = validateUser(principalDetai);
		if ("error".equals(userValidationResult.get("status"))) {
			return userValidationResult;
		}

		User user = (User) userValidationResult.get("user");

		List<StudyGroupMember> studyGroup = studyGroupMemberRepository.findByUser(user);
		returnMap.put("status", "success");
		returnMap.put("data", studyGroup);
		returnMap.put("message", "리스트가 조회되었습니다. ");
		return returnMap;
	}

	// 1:1 문의 리스트 반환
	@Transactional
	public Map<String, Object> getUserInquiries(PrincipalDetail principalDetail, int page) {

		Map<String, Object> returnMap = new HashMap<>();
		try {

			// 유저의 정보를 가져와서 유저의 1:1문의 데이터를 확인
			User user = principalDetail.getUser();

			Pageable pageable = PageRequest.of(page - 1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

			// 페이징된 문의 리스트 가져오기
			Page<Inquiry> inquiries = inquiryRepository.findByUser(user, pageable);

			// Inquiry 엔티티를 InquiryDTO로 변환
			Page<InquiryDTO> inquiryDTOs = inquiries.map(inquiry -> InquiryDTO.builder().id(inquiry.getId())
					.createdAt(inquiry.getCreatedAt()).category(inquiry.getCategory()).title(inquiry.getTitle())
					.content(inquiry.getContent()).user(inquiry.getUser()) // user 필드 설정 (username, nickname, userId)
					.inquiryStatus(inquiry.getStatus()).answer(inquiry.getAnswer()).build());

			returnMap.put("status", "success");
			returnMap.put("message", "조회되었습니다.");
			returnMap.put("data", inquiryDTOs.getContent());
			returnMap.put("totalPages", inquiries.getTotalPages()); // 총 페이지 수
			returnMap.put("totalElements", inquiries.getTotalElements()); // 총 요소 수
			returnMap.put("currentPage", inquiries.getNumber() + 1); // 현재 페이지 번호 (0부터 시작하므로 +1)
			returnMap.put("pageSize", inquiries.getSize()); // 페이지당 요소 수

		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
			returnMap.put("error", e.getMessage());
		}

		return returnMap;
	}

	// 내가 작성한 1:1 문의 삭제하기
	@Transactional
	public Map<String, Object> deleteUserInquiry(PrincipalDetail principalDetail, Integer inquiryId) {
		Map<String, Object> returnMap = new HashMap<>();
		try {
			// 유저의 정보를 가져와서 유저의 1:1 문의 데이터를 확인
			User user = principalDetail.getUser();

			// 유저와 관련된 Inquiry 가져오기, 없으면 예외 발생
			Inquiry inquiry = inquiryRepository.findById(inquiryId)
					.orElseThrow(() -> new IllegalArgumentException("1:1 문의가 존재하지 않습니다."));

			// 문의 작성자와 현재 로그인된 유저가 일치하지 않을 때 예외 던지기
			if (!user.getId().equals(inquiry.getUser().getId())) {
				throw new IllegalAccessException("권한이 없습니다.");
			}

			// 문의 삭제
			inquiryRepository.delete(inquiry);

			returnMap.put("status", "success");
			returnMap.put("message", "1:1 문의가 삭제되었습니다.");
		} catch (IllegalArgumentException e) {
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (IllegalAccessException e) {
			returnMap.put("status", "error");
			returnMap.put("message", e.getMessage());
		} catch (Exception e) {
			returnMap.put("status", "error");
			returnMap.put("message", "오류가 발생하였습니다.");
			returnMap.put("error", e.getMessage());
		}

		return returnMap;
	}

}
