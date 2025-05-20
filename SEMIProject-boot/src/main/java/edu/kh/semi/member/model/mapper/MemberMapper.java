package edu.kh.semi.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

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

}