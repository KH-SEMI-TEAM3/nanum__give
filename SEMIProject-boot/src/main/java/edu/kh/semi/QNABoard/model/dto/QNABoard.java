package edu.kh.semi.QNABoard.model.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class QNABoard {
	private Long boardNo;        // BOARD_NO
    private String boardTitle;   // BOARD_TITLE
    private String boardContent; // BOARD_CONTENT
    private String boardWriteDate; // BOARD_WRITE_DATE
    private String boardUpdateDate;// BOARD_UPDATE_DATE
    private int readCount;       // READ_COUNT
    private String boardDelFl;   // BOARD_DEL_FL
    private int boardCode;       // BOARD_CODE  (4: 자유게시판)깡으로 박음
    private Long memberNo;       // MEMBER_NO
    private String qaStatus;     // QA_STATUS (문의게시판만)
    
    // 관계 필드
    private String boardTypeName;  // 조인용 (옵션)
    private String memberNickname; // 조인용 (옵션)
    
	// 목록 조회시 상관 쿼리 결과
	private int commentCount; // 댓글 수
	
	// 게시글 작성자 프로필 이미지
	private String profileImg;
	
	// 게시글 목록 썸네일 이미지
	private String thumbnail;
	
	private int authority;

}
