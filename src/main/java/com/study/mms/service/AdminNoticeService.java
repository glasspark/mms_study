package com.study.mms.service;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.constants.ErrorCode;
import com.study.mms.constants.SuccessCode;
import com.study.mms.dto.NoticeDTO;
import com.study.mms.exception.CustomException;
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
			ImageUploader.moveImage(req, s, "/upload/studyBoard/"); // 임시 폴더에서 정식 폴더로 이미지 이동 (s는 파일 이름)
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
		// TODO Auto-generated method stub

		Notice notice;

		Optional<Notice> optionalNotice = noticeRepository.findById(noticeDTO.getId());

		// 순위 관련 데이터 처리
		// 우선 순위가 있다면
		if (noticeDTO.getPriority() != null) {
			// 중복된 순위가 있는지 확인
			boolean duplicationCheck = noticeRepository.existsByPriorityAndIsPinned(noticeDTO.getPriority());
			if (duplicationCheck) {
				// 중복된 순위가 있다면 그 숫자부터 +1 씩 증가 시키기
				noticeRepository.incrementPrioritiesFrom(noticeDTO.getPriority());
			}
		}

		// 공지사항이 존재 하는 경우(update)
		if (optionalNotice.isPresent()) {

			notice = optionalNotice.get();

			// 기존 이미지 목록 가져오기
			String[] imgList = notice.getImg().split(",");
			if (imgList != null) {
				String newImg = notice.getImg(); // 업데이트할 이미지 정보
				for (String s : imgList) {
					// 새로운 이미지 목록에 기존 이미지가 포함되지 않으면 해당 이미지 삭제
					if (!newImg.contains(s)) {
						try {
							ImageUploader.deleteImage(req, "/upload/notice/" + s);
						} catch (Exception e) {
							// e.printStackTrace();
							throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
						}
					}
				}
			}

			String changeImgePath = updateImagePathInContent(noticeDTO.getContent());
			moveTempImage(noticeDTO.getImg(), req);

			// 공지사항 업데이트
			notice.update(noticeDTO.getTitle(), changeImgePath, noticeDTO.getImg(), noticeDTO.getIsPinned(),
					noticeDTO.getPriority());

		} else {

			String changeImgePath = updateImagePathInContent(noticeDTO.getContent());
			moveTempImage(noticeDTO.getImg(), req);

			// 공지사항이 존재하지 않는 경우 (create)
			notice = Notice.builder().title(noticeDTO.getTitle()).content(changeImgePath).img(noticeDTO.getImg())
					.isPinned(noticeDTO.getIsPinned()).priority(noticeDTO.getPriority()).build();
		}

		// 공지사항 저장
		noticeRepository.save(notice);

		// 성공 메세지 보내기
		return ResponseUtil.buildSuccessResponse(HttpStatus.OK, SuccessCode.DATA_UPDATE.getMessage());
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
	public ResponseEntity<Map<String, Object>> getNoticeList(PrincipalDetail principalDetail) {
		// TODO Auto-generated method stub

		return null;
	}

}
