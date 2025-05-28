package edu.kh.semi.member.model.service;

import java.util.Map;

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

	/** 찾은 아이디 결과 가지고 페이지 이동
	 * @param email
	 * @return
	 */
	String findId(String email);

	/** 비밀번호 찾기 결과 가지고 페이지 이동
	 * @param inputMember
	 * @return
	 */
	int findPw(Member inputMember);

	/** 새 비밀번호로 변경하기
	 * @param paramMap
	 * @return
	 */
	int newPw(Map<String, String> paramMap);

	/** 비밀번호 변경
	 * @param paramMap
	 * @param memberNo
	 * @return
	 */
	int changePw(Map<String, String> paramMap, int memberNo);

	/** 회원 탈퇴
	 * @param memberPw
	 * @param memberNo
	 * @return
	 */
	int secession(String memberPw, int memberNo);

}