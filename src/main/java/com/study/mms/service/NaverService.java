package com.study.mms.service;


import com.study.mms.auth.PrincipalDetail;
import com.study.mms.model.User;
import com.study.mms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NaverService {

    protected String CLIENT_ID = "ldbRIw3Ictv3KHKXHkPj"; // naver developer client_id
    protected String CLIENT_SECRET_ID = "Adw5wB1gPE"; // naver developer client_secret_id


    private final UserRepository usersRepository;


    private BCryptPasswordEncoder encoder;


    // 네이버 로그인 화면 호출
    public String naverLoginPage(HttpServletRequest request) {

        String client_id = CLIENT_ID;
        String redirect_uri = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) { // http, https 일때는 포트 번호가 사용되지 않음
            redirect_uri = redirect_uri + ":" + request.getServerPort();
        }
        redirect_uri = redirect_uri + "/auth/naver-login"; // 로그인 후 처리할 controller mapping

        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString(32);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("https://nid.naver.com/oauth2.0/authorize");
        stringBuffer.append("?client_id=" + client_id);
        stringBuffer.append("&response_type=code");
        stringBuffer.append("&redirect_uri=" + redirect_uri);
        stringBuffer.append("&state=" + state);
        return stringBuffer.toString();
    }


    // 네이버 소셜 로그인 db, security 저장
    public String naverOauth(String code, HttpServletRequest request) throws Exception {

        if (code == null) { // 취소버튼 눌렀을 때
           //  return "/auth/clientNaverLoginPage";

            System.out.println("데이터 정보가 없습니다.");

            return "/auth/login-return/naver";
            //return "/";
        }

        String accessToken = getNaverAccessToken(code);

        HashMap<String, Object> userInfo = getNaverUserInfo(accessToken);

        String snsId = (String) userInfo.get("id");

        // ============유저 로그인 샘플===============
        User searchUserinfo = usersRepository.findBySnsAndSnsId("NAVER", snsId).orElse(null);

        //     int step = Integer.parseInt(request.getSession().getAttribute("step").toString());

        HttpSession session = request.getSession();


        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String authEmail = (String) session.getAttribute("authEmail");
        String authNickname = (String) session.getAttribute("authNickname");

        System.out.println(searchUserinfo.getId());

        //세션에 저장된 유저의 데이터가 없으면 이동
        if (authEmail == null || authNickname == null || searchUserinfo == null) {
      //      return "/auth/login-return/naver";
        }

        if (searchUserinfo == null) {

            String lastName = usersRepository.getNaverCount().orElse(null);
            int countNumber = lastName != null ? Integer.parseInt(lastName.replace("naver_", "")) : 0;
            String count = String.valueOf(countNumber + 1);
            String id = count;
            if (count.length() < 4) {
                for (int i = 3; i > count.length(); i--) {
                    id = "0" + id;
                }
            }


            //세션에서 가져온 값 넣기
            String nickname = authNickname;
            String email = authEmail;

            //인증을 위한 세션 삭제하기
            session.removeAttribute("isAuthenticated");
            session.removeAttribute("authEmail");
            session.removeAttribute("authNickname");

            String username = "naver_" + id;
            searchUserinfo = new User();
            searchUserinfo.setSnsId(snsId);
            searchUserinfo.setSns("NAVER");
            searchUserinfo.setUsername(username);
            searchUserinfo.setNickname(nickname);
            searchUserinfo.setEmail(email);
            String salt = BCrypt.gensalt();
            String newPassword = BCrypt.hashpw("qefhtsdf234f!sdagsga", salt);
            searchUserinfo.setPassword(newPassword);
            String token = UUID.randomUUID().toString();
            searchUserinfo.setPassword(newPassword);
            searchUserinfo.setToken(token);
            searchUserinfo.setSalt(salt);
            searchUserinfo.setRole("ROLE_USER");
            searchUserinfo.setImg_name("default_img"); // 기본 이미지 저장
            searchUserinfo.setImg_path("/img/defaultImg.png");
            usersRepository.save(searchUserinfo);
        }

        // 시큐리티 수동 로그인
        PrincipalDetail principal = new PrincipalDetail(searchUserinfo);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        // =======================================

        return "/study";

    }

    // 네이버 로그인 access_token 리턴
    public String getNaverAccessToken(String code) throws Exception {

        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://nid.naver.com/oauth2.0/token";

        String client_id = CLIENT_ID;
        String client_secret_id = CLIENT_SECRET_ID;

        try {
            URL url = new URL(reqURL);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + client_id); // 본인이 발급받은 key
            sb.append("&client_secret=" + client_secret_id); // 본인이 발급받은 key
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
//			System.out.println("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
//			System.out.println("response body : " + result);

            // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JSONParser parser = new JSONParser();

            Object obj = parser.parse(result);

            JSONObject jsonObj = (JSONObject) obj;

            access_Token = (String) jsonObj.get("access_token");
            refresh_Token = (String) jsonObj.get("refresh_token");

//			System.out.println("access_token : " + access_Token);
//			System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }

    // 네이버 이메일 추출
    public HashMap<String, Object> getNaverUserInfo(String accessToken) throws Exception {
        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://openapi.naver.com/v1/nid/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
//			System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
//			System.out.println("response body : " + result);

            JSONParser parser = new JSONParser();

            Object obj = parser.parse(result);

            JSONObject jsonObj = (JSONObject) obj;

            JSONObject naver_account = (JSONObject) jsonObj.get("response");
            String id = String.valueOf(naver_account.get("id"));
            userInfo.put("id", id);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

}



