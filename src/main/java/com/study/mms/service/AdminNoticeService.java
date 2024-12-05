package com.study.mms.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.constants.ErrorCode;
import com.study.mms.constants.SuccessCode;
import com.study.mms.dto.NoticeDTO;
import com.study.mms.dto.PagedResponseDTO;
import com.study.mms.exception.CustomException;
import com.study.mms.exception.ResourceNotFoundException;
import com.study.mms.model.Notice;
import com.study.mms.repository.NoticeRepository;
import com.study.mms.util.ImageUploader;
import com.study.mms.util.ResponseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

	private final NoticeRepository noticeRepository;

	// 게시글 내부 이미지 경로 변경
	private String updateImagePathInContent(String content) {
		if (content != null) {
			return content.replace("/upload/temp/", "/upload/notice/");
		}
		return content;
	}

	// 이미지 경로 수정 (임시 폴더에서 정식 폴더로 이동)
	private void moveTempImage(String imgs, HttpServletRequest req) {
		String[] imgName = imgs.split(",");
		for (String s : imgName) {
			ImageUploader.moveImage(req, s, "/upload/notice/"); // 임시 폴더에서 정식 폴더로 이미지 이동 (s는 파일 이름)
		}
	}

	// 특정 우선순위와 상단 고정 여부를 가진 공지사항이 존재하는지 여부를 확인하는 메서드
	public boolean isPriorityPinned(int priority) {
		return noticeRepository.existsByPriorityAndIsPinned(priority);
	}

	// 공지사항 생성 및 수정
	@Transactional
	public ResponseEntity<Map<String, Object>> createAndUpdateNotice(PrincipalDetail principalDetail,
			NoticeDTO noticeDTO, HttpServletRequest req) {

		Notice notice;

		// 공지사항 ID가 있는 경우: 업데이트 처리
		if (noticeDTO.getId() != null) {
			notice = handleUpdate(noticeDTO, req);
		} else {
			// 공지사항 ID가 없는 경우: 생성 처리
			notice = handleCreate(noticeDTO, req);
		}

		// 공지사항 저장
		noticeRepository.save(notice);

		// 성공 메시지 반환
		return ResponseUtil.buildSuccessResponse(HttpStatus.OK, SuccessCode.DATA_UPDATE.getMessage());
	}

	private Notice handleUpdate(NoticeDTO noticeDTO, HttpServletRequest req) {
		Optional<Notice> optionalNotice = noticeRepository.findById(noticeDTO.getId());
		if (optionalNotice.isEmpty()) {
			throw new CustomException(ErrorCode.NOTICE_NOT_FOUND); // 공지사항이 없을 경우 예외 처리
		}

		Notice notice = optionalNotice.get();

		// 기존 이미지 목록 삭제 처리
		deleteUnusedImages(notice.getImg(), noticeDTO.getImg(), req);

		// 이미지 경로 업데이트 및 임시 이미지 이동
		String updatedContent = updateImagePathInContent(noticeDTO.getContent());
		moveTempImage(noticeDTO.getImg(), req);

		// 공지사항 업데이트
		notice.update(noticeDTO.getTitle(), updatedContent, noticeDTO.getImg(), noticeDTO.getIsPinned(),
				noticeDTO.getPriority());

		// 우선순위 처리
		handlePriority(noticeDTO);

		return notice;
	}

	private Notice handleCreate(NoticeDTO noticeDTO, HttpServletRequest req) {
		// 이미지 경로 업데이트 및 임시 이미지 이동
		String updatedContent = updateImagePathInContent(noticeDTO.getContent());
		moveTempImage(noticeDTO.getImg(), req);

		// 공지사항 생성
		Notice notice = Notice.builder().title(noticeDTO.getTitle()).content(updatedContent).img(noticeDTO.getImg())
				.isPinned(noticeDTO.getIsPinned()).priority(noticeDTO.getPriority()).build();

		// 우선순위 처리
		handlePriority(noticeDTO);

		return notice;
	}

	private void deleteUnusedImages(String existingImages, String newImages, HttpServletRequest req) {
		if (existingImages == null) {
			return; // 기존 이미지가 없는 경우 처리 불필요
		}

		String[] imgList = existingImages.split(",");
		for (String img : imgList) {
			if (!newImages.contains(img)) {
				try {
					ImageUploader.deleteImage(req, "/upload/notice/" + img);
				} catch (Exception e) {
					throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
				}
			}
		}
	}

	private void handlePriority(NoticeDTO noticeDTO) {
		if (noticeDTO.getPriority() != null && noticeDTO.getIsPinned() != null && noticeDTO.getIsPinned()) {
			boolean duplicationCheck = noticeRepository.existsByPriorityAndIsPinned(noticeDTO.getPriority());
			if (duplicationCheck) {
				noticeRepository.incrementPrioritiesFrom(noticeDTO.getPriority());
			}
		}
	}

	// 공지사항 삭제
	@Transactional
	public ResponseEntity<Map<String, Object>> deleteNotice(PrincipalDetail principalDetail, Integer noticeId,
			HttpServletRequest req) {
		// TODO Auto-generated method stub

		// 공지사항 존재 여부 확인
		Notice notice = noticeRepository.findById(noticeId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

		// 만약 상단 고정으로되어져 있으면 그 수의 초과인 수를 -1 씩 당김
		if (notice.getIsPinned()) {
			noticeRepository.decrementPrioritiesAfter(notice.getPriority());
		}

		noticeRepository.delete(notice);
		return ResponseUtil.buildSuccessResponse(HttpStatus.OK, SuccessCode.DATA_DELETE.getMessage());
	}

	// 공지사항 리스트 반환(페이징 처리)
	@Transactional
	public ResponseEntity<Map<String, Object>> getNoticeList(PrincipalDetail principalDetail, Integer page) {
		// TODO Auto-generated method stub
		// Pageable 생성: 페이지 번호는 0부터 시작
		Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());

		// 페이징된 공지사항 가져오기
//		Page<Notice> noticePage = noticeRepository.findAll(pageable);

		Page<Notice> noticePage = noticeRepository.findAllNotices(pageable);
		// 공지사항 DTO로 변환
		List<NoticeDTO> notices = noticePage.getContent().stream()
				.map(notice -> NoticeDTO.builder().id(notice.getId()).title(notice.getTitle())
						.content(notice.getContent()).isPinned(notice.getIsPinned()).priority(notice.getPriority())
						.createdAt(notice.getCreatedAt()).build())
				.collect(Collectors.toList()); // Collectors.toList() 사용
		// PagedResponseDTO 생성
		PagedResponseDTO<NoticeDTO> responseData = new PagedResponseDTO<>(notices, noticePage.getNumber(),
				(int) noticePage.getTotalElements(), noticePage.getTotalPages());

		return ResponseUtil.buildSuccessResponseWithData(HttpStatus.OK, SuccessCode.DATA_LOADED.getMessage(),
				responseData);
	}

	// 공지사항 단일 데이터 반환
	@Transactional
	public NoticeDTO getNotice(PrincipalDetail principalDetail, Integer noticeId) {
		// TODO Auto-generated method stub
		// 공지사항 존재 여부 확인
		Notice notice = noticeRepository.findById(noticeId)
				.orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

		// Notice -> NoticeDTO 변환 (빌더 사용)
		return NoticeDTO.builder().id(notice.getId()).createdAt(notice.getCreatedAt()).title(notice.getTitle())
				.content(notice.getContent()).isPinned(notice.getIsPinned()).img(notice.getImg())
				.priority(notice.getPriority()).build();
	}

}
