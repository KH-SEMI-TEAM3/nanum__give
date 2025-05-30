package edu.kh.semi.board.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

		private int commentNo;
		private String commentContent;
		private String commentWriteDate;
		private String commentDelFl;
		private int boardNo;
		private int parentCommentNo;
		private int memberNo;
		    
		    
		 // 댓글 조회 시 회원 프로필 , 닉네임
		private String memberNickname;
		private String memberImg;
		
		// LEVEL용 가상
		private int level;
		
		// 관리자 여부
		private boolean isAdmin;

	}

