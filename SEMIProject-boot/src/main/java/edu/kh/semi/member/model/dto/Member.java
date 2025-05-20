package edu.kh.semi.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	private int memberNo;
	private String memberId;
	private String memberPw;
	private String memberNickname;
	private String memberEmail;
	private String memberTel;
	private String memberAddress;
	private String enrollDate;
	private String memberDelFl;
	private int authority;
	private String memberImg;
}
