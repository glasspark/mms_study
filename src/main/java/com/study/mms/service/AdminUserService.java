package com.study.mms.service;

import com.study.mms.constants.ErrorCode;
import com.study.mms.constants.SuccessCode;
import com.study.mms.dto.AdminUserInfoDTO;
import com.study.mms.exception.CustomException;
import com.study.mms.model.StudyBoard;
import com.study.mms.model.StudyGroup;
import com.study.mms.model.UploadedFile;
import com.study.mms.model.User;
import com.study.mms.repository.StudyBoardRepository;
import com.study.mms.repository.StudyGroupRepository;
import com.study.mms.repository.UploadedFileRepository;
import com.study.mms.repository.UserRepository;
import com.study.mms.util.ImageUploader;
import com.study.mms.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final StudyBoardRepository studyBoardRepository;
    private final StudyGroupRepository studyGroupRepository;

    public ResponseEntity<Map<String, Object>> getUsers(Integer page, String type, String content) {

        if (page == null) {
            page = 1;
        }

        if (type == null || type.isEmpty()) {
            type = "default";
        }

        if (!type.equals("nickname") && !type.equals("email") && !type.equals("default")) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users;

        if (type.equals("nickname")) {
            users = userRepository.findUsersByNickname(content, pageable);
        } else if (type.equals("email")) {
            users = userRepository.findUsersByNickname(content, pageable);
        } else {
            users = userRepository.findAllNonAdminUsers(pageable);
        }

        // DTO 클래스의 변환 메서드 사용
        List<AdminUserInfoDTO> userDTOs = users.getContent().stream()
                .map(AdminUserInfoDTO::fromEntity)
                .collect(Collectors.toList());

        // 페이징 정보와 함께 반환
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("pagination", Map.of(
                "currentPage", users.getNumber() + 1, //클라에게 1 부터 시작이 되므로
                "totalPages", users.getTotalPages(),
                "totalElements", users.getTotalElements(),
                "pageSize", users.getSize()
        ));
        return ResponseUtil.buildSuccessResponseWithDataAndPageData(HttpStatus.OK, SuccessCode.DATA_LOADED.getMessage(),
                userDTOs, pageData);
    }


    @Transactional
    public ResponseEntity<Map<String, Object>> deleteUser(Integer id, HttpServletRequest req) throws Exception {

        //이용자 데이터 확인
        if (id == null) {
            //파라이터 값 확인
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            // 사용자 삭제
            User user = optionalUser.get();

            //1. 프로필 이미지 삭제
            if (Boolean.TRUE.equals(user.getImg_type())) {
                // 프로필 이미지 삭제 로직
                ImageUploader.deleteImage(req, user.getImg_path());
            }
            //2. 등록한 스터디그룹 공유 파일 삭제
            List<UploadedFile> fileLists = uploadedFileRepository.findAllByUserId(user.getId());

            //공유 파일이 존재한다면 공유파일 삭제
            if (!fileLists.isEmpty()) {
                for (UploadedFile file : fileLists) {
                    ImageUploader.deleteImage(req, file.getFilePath());
                }
            }

            //3. 스터디 그룹 게시글 이미지 삭제

            // 1. User ID로 모든 게시글 조회
            List<StudyBoard> studyBoards = studyBoardRepository.findAllByUser_Id(user.getId());
            if (!studyBoards.isEmpty()) {
                // 2. 각 게시글의 이미지 삭제
                for (StudyBoard board : studyBoards) {
                    if (board.getImg() != null && !board.getImg().isEmpty()) {
                        String[] imgList = board.getImg().split(",");
                        for (String imageName : imgList) {
                            try {
                                String filePath = "/upload/studyBoard/" + imageName.trim();
                                ImageUploader.deleteImage(req, filePath);
                            } catch (Exception e) {
                                throw new CustomException(ErrorCode.IMG_NOT_DELETE);
                            }
                        }
                    }
                }
            }

            //스터디 그룹 삭제
            List<StudyGroup> groups = studyGroupRepository.findAllByLeaderId(user.getId());
            if (!groups.isEmpty()) {
                studyGroupRepository.deleteAll(groups);
            }

            userRepository.deleteById(id);
            return ResponseUtil.buildSuccessResponse(HttpStatus.OK, SuccessCode.DATA_DELETE.getMessage());
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

}
