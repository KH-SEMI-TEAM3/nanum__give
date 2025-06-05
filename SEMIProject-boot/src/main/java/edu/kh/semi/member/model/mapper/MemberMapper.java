package edu.kh.semi.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.semi.member.model.dto.Member;


@Mapper
public interface MemberMapper {
	
	/** 로그인
	 * @param memberId
	 * @return
	 */
	Member login(String memberId);
	
	/** 회원 아이디 중복 검사
	 * @param memberId
	 * @return
	 */
	int checkMemberId(String memberId);
	
	/** 이메일 중복 검사
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복 검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원가입
	 * @param inputMember
	 * @return
	 */
	int signup(Member inputMember);

	/**  멤버의 전체 정보를 memberNo을 주면 반환해주는 로직
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
	 * @param memberId
	 * @param memberPw
	 * @return
	 */
	int newPw(@Param("memberId") String memberId, @Param("memberPw") String memberPw);

	/** 회원의 비밀번호 조회
	 * @param memberNo
	 * @return
	 */
	String selectPw(int memberNo);

	/** 회원 비밀번호 변경
	 * @param paramMap
	 * @return
	 */
	int changePw(Map<String, String> paramMap);

	/** 회원 탈퇴
	 * @param memberNo
	 * @return
	 */
	int secession(int memberNo);

	String selectMemberDelFl(int memberNo);

}