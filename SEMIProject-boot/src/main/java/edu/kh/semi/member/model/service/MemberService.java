package edu.kh.semi.member.model.service;

import edu.kh.semi.member.model.dto.Member;

public interface MemberService {
	
	/** 로그인 서비스
	 * @param inputMember
	 * @return loginMember(Member)
	 */
	Member login(Member inputMember);
	
	/** 회원 아이디 중복 검사 서비스
	 * @param memberId
	 * @return
	 */
	int checkMemberId(String memberId);

	/** 이메일 중복 검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복 검사 서비스
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원가입 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int signup(Member inputMember, String[] memberAddress);

	/** 멤버의 전체 정보를 memberNo을 주면 반환해주는 로직
	 * @param memberNo
	 * @return
	 */
	Member selectMemberByNo(int memberNo);

}