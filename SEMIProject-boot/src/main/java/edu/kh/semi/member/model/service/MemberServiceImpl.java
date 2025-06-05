package edu.kh.semi.member.model.service;

import java.util.Map;

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

	/**
	 * 찾은 아이디 결과 가지고 페이지 이동
	 */
	@Override
	public String findId(String email) {
		return mapper.findId(email);
	}
	
	/**
	 * 비밀번호 찾기 결과 가지고 페이지 이동
	 */
	@Override
	public int findPw(Member inputMember) {
		return mapper.findPw(inputMember);
	}
	
	/**
	 * 새 비밀번호로 변경하기
	 */
	@Override
	public int newPw(Map<String, String> paramMap) {
		
		// 1. 평문 비밀번호 가져오기
	    String inputPw = paramMap.get("memberPw");

	    // 2. 암호화
	    String encPw = bcrypt.encode(inputPw);

	    // 3. 암호화된 비밀번호로 paramMap 수정
	    paramMap.put("memberPw", encPw);

	    // 4. Mapper 호출하여 업데이트
	    return mapper.newPw(paramMap.get("memberId"), encPw);
	    
	}
	
	/**
	 * 비밀번호 변경
	 */
	@Override
	public int changePw(Map<String, String> paramMap, int memberNo) {
		
		// 1. 현재 비밀번호가 일치하는지 확인하기
		// - 현재 로그인한 회원의 암호화된 ㅣ밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 입력 받은 현재 비밀번호와(평문)
		// DB에서 조회한 비밀번호(암호화)를 비교
		// -> bcypt.matches(평문, 암호화비번) 사용
		
		// 다를 경우
		if(!bcrypt.matches(paramMap.get("currentPw"), originPw)) {
			return 0;
		}
		
		// 2. 같은 경우
		// 새 비밀번호를 암호화 (bcrypt.encode(평문))
		String encPw = bcrypt.encode(paramMap.get("newPw"));
		
		// DB에 업데이트
		// SQL 전달 해야하는 데이터 2개 (암호화한 새 비번 encPw, 회원번호 memberNo)
		// -> mapper에 전달할 수 있는 전달인자는 단 1개
		// -> 묶어서 전달 (paramMap 재활용)
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo + ""); // 1 + "" => 문자열
		
		return mapper.changePw(paramMap);
	}
	
	/**
	 * 회원 탈퇴
	 */
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호 DB조회
		String originPw = mapper.selectPw(memberNo);
		
		// 다를 경우
		if(!bcrypt.matches(memberPw, originPw)) {
			return 0;
		}
		
		return mapper.secession(memberNo);
	}

	@Override
	public String getMemberDelFl(int memberNo) {

		return mapper.selectMemberDelFl(memberNo);
	}
}