package edu.kh.semi.myPage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.myPage.model.service.MyPageService;
import jakarta.mail.Multipart;
import lombok.extern.slf4j.Slf4j;


@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("myPage")
@Slf4j
public class MyPageController {
	
	@Autowired
	private MyPageService service;
	
	@GetMapping("info")  // /myPage/info GET 요청 매핑
	public String info(@SessionAttribute("loginMember") Member loginMember,
						Model model) {
		
		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 -> session에 등록된 상태(loginMember)
		
		String memberAddress = loginMember.getMemberAddress();
		// "04540^^^서울 중구 남대문로 120^^^3층, E강의장"
		// 주소가 없다면 null
		
		// 주소가 있을 경우에만 동작
		if(memberAddress != null) {
			
			// 구분자 "^^^" 를 기준으로
			// memberAddress 값을 쪼개어 String[] 로 반환
			String[] arr = memberAddress.split("\\^\\^\\^");
			// -> "04540^^^서울 중구 남대문로 120^^^3층, E강의장"
			// -> ["04540", "서울 중구 남대문로 120", "3층, E강의장"]
			//	       0                1                   2
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		
		
		return "myPage/myPage-info";
	}
	
	// 프로필 이미지 변경 화면 이동
	@GetMapping("profile") // /myPage/profile GET 요청 매핑
	public String profile() {
		return "myPage/myPage-profile";
	}
	// 비밀번호 변경 화면 이동
	@GetMapping("changePw") // /myPage/changePw GET 요청 매핑
	public String changePw() {
		return "myPage/myPage-changePw";
	}
	// 회원 탈퇴 화면 이동
	@GetMapping("secession") // /myPage/secession GET 요청 매핑
	public String secession() {
		return "myPage/myPage-secession";
	}
	// 파일 업로드 테스트 화면 이동
	@GetMapping("fileTest") // /myPage/fileTest GET 요청 매핑
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}
	
	
	/** 회원 정보 수정
	 * @param inputMember : 커맨드 객체(@ModelAttribute가 생략된 상태) 
	 * 						제출된 수정된 회원 닉네임, 전화번호, 주소
	 * @param loginMember : 로그인한 회원 정보 (회원 번호 사용할 예정)
	 * @param memberAddress : 주소만 따로 받은 String[] 구분자 ^^^ 변경 예정
	 * @param ra	: 
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember,
							@SessionAttribute("loginMember") Member loginMember,
							@RequestParam("memberAddress") String[] memberAddress,
							RedirectAttributes ra) {
		
		// inputMember에 로그인한 회원 번호 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		// inputMember : 회원 번호, 회원 닉네임, 전화번호, 주소
		
		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);
		
		
		return "redirect:info";
	}
	
	/** 비밀번호 변경
 	 * @param parmaMap : 모든 파라미터(요청 데이터)를 맵으로 저장
	 * @param loginMember : 세션에 등록된 현재 로그인한 회원 정보
	 * @param ra
	 * @return
	 */
	@PostMapping("changePw") // /myPage/changePw POST 요청 매핑
	public String changePw(@RequestParam Map<String, String> paramMap,
			@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) {
		// paramMAp = {currentPw=asd123, newPW=pass02!, newPwConfirm=pass02!}
		
		// 로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();
		
		// 현재 + 새 비번 + 회원 번호를 서비스로 전달
		int result = service.changePw(paramMap, memberNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) { // 변경 성공시
			message = "비밀번호가 변경되었습니다";
			path = "/myPage/info";
			
		} else { // 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "/myPage/changePW";
		}
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
	/** 회원 탈퇴
	 * @param memberPw : 입력 받은 비밀 번호
	 * @param loginMember : 로그인 한 회원 정보(세션)
	 * @param status : 세션 완료용 객체 -> @SessionAttribute 로 등록된 세션을 완료시킴
	 * @return
	 */
	@PostMapping("secession")
	public String secession(@RequestParam("memberPw") String memberPw,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra, SessionStatus status
			) {
		// 로그인한 회원의 회원 번호 꺼내기
		int memberNo = loginMember.getMemberNo();
		
		// 서비스 호출(입력받은 비밀번호, 로그인 한 회원 번호)
		int result = service.secession(memberPw,memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "탈퇴 되었습니다";
			path = "/";
			
			status.setComplete(); // 세션 완료 시킴
		}else {
			message = "비밀번호가 일치하지 않습니다";
			path = "secession";
		}
		ra.addFlashAttribute("message", message);
		
		// 탈퇴 성공 : -> redirect:/ ( 메인 페이지)
		// 탈퇴 실패 : -> redirect:secession (상대경로)
		// 			-> /myPage/secession (상대경로 Post)
		//          -> /myPage/secession (GET 요청)
		return "redirect:" + path;
	}
	
	/* Spring 에서 파일 업로드를 처리하는 방법
	 * 
	 * - encType =  "multipart/form-data" 로 클라이언트 요청을 받으면
	 * (문자, 숫자, 파일 등이 섞여있는 요청)
	 * 
	 * 이를 MultipartResolver(FlieConfig에 정의) 를 이용해서 섞여있는 파라미터를 분리
	 * 
	 * 문자열, 숫자 -> String
	 * 파일 -> MultipartFile
	 * 
	 * */
	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
						  @SessionAttribute("loginMember")Member loginMember,
						  RedirectAttributes ra) throws Exception {

		
		// 업로드 된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받을 예정
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) message = "변경 성공!";
		else		message = "변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile";
	}


}
