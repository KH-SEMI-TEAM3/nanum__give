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

		private Long commentNo;
		private String commentContent;
		private String commentWriteDate;
		private String commentDelFl;
		private Long boardNo;
		private Long parentCommentNo;
		private Long memberNo;
		    
		    
		 // 댓글 조회 시 회원 프로필 , 닉네임
		private String memberNickname;
		private String profileImg;
		
		private List<Comment> commentList;

	}

