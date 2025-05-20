package edu.kh.semi.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.mapper.MemberMapper;

@Transactional(rollbackFor = Exception.class)
@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberMapper mapper;	
	
	// Bcrypt 암호화 객체 의존성 주입(DI) - SecurityConfig 참고
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	/**
	 * 로그인
	 */
	@Override
	public Member login(Member inputMember) {
		
		//암호화 진행
		
		Member loginMember = mapper.login(inputMember.getMemberId());
		
		// 일치하는 아이디가 없는 경우
		if(loginMember == null) return null;
		
		// 입력받은 비밀번호와 암호화 된 비밀번호가 일치하지 않는 경우
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		// 로그인 결과에서 비밀번호 제거
		// Session 객체의 실어서 모든 브라우저에 공유해야하기에
		// 비밀번호가 남아있으면 보안상 좋지 않으므로 제거
		loginMember.setMemberPw(null);
		
		return loginMember;
	}
	
	/**
	 * 회원 아이디 중복 검사
	 */
	@Override
	public int checkMemberId(String memberId) {
		return mapper.checkMemberId(memberId);
	}
	
	/**
	 * 이메일 중복 검사
	 */
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}
	
	/**
	 * 닉네임 중복 검사
	 */
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}
	
	/**
	 * 회원가입
	 */
	@Override
	public int signup(Member inputMember, String[] memberAddress) {

		if(!inputMember.getMemberAddress().equals(",,")) { // 주소가 입력된 경우

			String address = String.join("^^^", memberAddress);
			// [12345, 서울시 중구 남대문로, 3층, E강의장]
			// -> "12345^^^서울시 중구 남대문로^^^3층, E강의장" 하나의 문자열로 만들어 반환
			inputMember.setMemberAddress(address);
			
		} else { // 주소가 입력되지 않은 경우 null로 저장
			inputMember.setMemberAddress(null);
		}
		
		// 비밀번호 암호화 진행
		
		// inputMember 안의 memberPw -> 평문
		// 비밀번호를 암호화하여 inputMember 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// 회원 가입 mapper 메서드 호출
		return mapper.signup(inputMember);
	}

	/**
	 * 멤버의 전체 정보를 memberNo을 주면 반환해주는 로직
	 */
	@Override
	public Member selectMemberByNo(int memberNo) {
		// TODO Auto-generated method stub
		return mapper.selectMemberByNo(memberNo);

	}


}