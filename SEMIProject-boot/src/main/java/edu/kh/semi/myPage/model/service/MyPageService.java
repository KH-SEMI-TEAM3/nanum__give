package edu.kh.semi.myPage.model.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.member.model.dto.Member;



public interface MyPageService {

	/** 회원 정보 업데이트 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	/** 비밀번호 변경 서비스
	 * @param paramMap
	 * @param memberNo
	 * @return
	 */
	int changePw(Map<String, String> paramMap, int memberNo);

	/** 탈퇴 서비스
	 * @param memberPw
	 * @param memberNo
	 * @return
	 */
	int secession(String memberPw, int memberNo);

	/** 프로필 이미지 저장 서비스
	 * @param profileImg
	 * @param loginMember
	 * @return
	 */
	int profile(MultipartFile profileImg, Member loginMember);

}
