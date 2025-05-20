package edu.kh.semi.myPage.model.service;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.myPage.model.mapper.MyPageMapper;


@Service
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageMapper mapper;// DAO 연결
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
//	@Value("${my.profile.web-path}")
//	private String profileWebPath;
//	@Value("${my.profile.folder-path}")
//	private String profileFolderPath;

	
	/** 내정보 업데이트
	 *
	 */
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else {
		// 없을 경우
			inputMember.setMemberAddress(null);
		}
		
		
		return mapper.updateInfo(inputMember);
	}
	
	// 비밀번호 변경 서비스
	@Override
	public int changePw(Map<String, String> paramMap, int memberNo) {
		// 1. 현재 비밀번호가 일치하는지 확인하기
		// - 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo);
		// 입력받은 현재 비밀번호와 (평문)
		// DB에서 조회한 비밀번호(암호화)를 비교
		// -> bycript.matches(평문, 암호화된 비번) 사용
		
		// 다를 경우
		if(!bcrypt.matches(paramMap.get("currentPw"), originPw)) {
			return 0;
		}
		// 2. 같을 경우
		
		// 새 비밀번호를 암호화(bcrypt.encode(평문))
		String encPw = bcrypt.encode(paramMap.get("newPw"));
		
		// DB에 업데이트
		// SQL 전달해야 하는 데이터 2개 (암호화한 새 비밀번호 encPW, 회원번호 memberPW)
		// -> mapper에 전달할 수 있는 전달인자는 단 1개!
		// -> 묶어서 전달 (paramMap 재활용)
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo + ""); // 1 + "" => 문자열 변환
		
		return mapper.changePw(paramMap);
	}


	// 회원 탈퇴 서비스
	@Override
	public int secession(String memberPw, int memberNo) {
		// 현재 로그인한 회원의 암호화된 비밀전호 DB 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 다를 경우
		if(!bcrypt.matches(memberPw, originPw)) {
			return 0;
		} // 같은 경우
		return mapper.secession(memberNo);
	}
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) {
		// TODO Auto-generated method stub
		return 0;
	}


}
