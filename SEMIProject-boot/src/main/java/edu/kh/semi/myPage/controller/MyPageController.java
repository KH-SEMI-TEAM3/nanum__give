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
		model.addAttribute("member", loginMember); // loginMember를 member라는 이름으로 전달
		
		// 현재 로그인한 회원의 주소를 꺼내옴
		// 현재 로그인한 회원 정보 -> session에 등록된 상태(loginMember)
		
		String memberAddress = loginMember.getMemberAddress();
		// "04540^^^서울 중구 남대문로 120^^^3층, E강의장"
		// 주소가 없다면 null
		
		// 주소가 있을 경우에만 동작
		// 주소가 짧을때를 위해 수정 김동준 2025-05-20
			if (memberAddress != null) {
			    String[] arr = memberAddress.split("\\^\\^\\^");

			    if (arr.length == 3) {
			        model.addAttribute("postcode", arr[0]);
			        model.addAttribute("address", arr[1]);
			        model.addAttribute("detailAddress", arr[2]);
			    } else {
			        // 예외 상황: 주소 포맷이 예상과 다를 경우 기본값 설정
			        model.addAttribute("postcode", "");
			        model.addAttribute("address", memberAddress); // 전체 주소 하나로 출력
			        model.addAttribute("detailAddress", "");
			    }
			}
		
		// 리턴 주소 변경 김동준 2025-05-20
		return "member/myPage";
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
	// 업데이트 페이지 접근용 컨트롤러 추가 김동준 2025-05-20
	@GetMapping("updateInfo")
	public String updateInfoPage(@SessionAttribute("loginMember") Member loginMember, Model model) {
	    
	    model.addAttribute("member", loginMember);

	    String memberAddress = loginMember.getMemberAddress();

	    if (memberAddress != null) {
	        String[] arr = memberAddress.split("\\^\\^\\^");

	        if (arr.length == 3) {
	            model.addAttribute("postcode", arr[0]);
	            model.addAttribute("address", arr[1]);
	            model.addAttribute("detailAddress", arr[2]);
	        } else {
	            model.addAttribute("postcode", "");
	            model.addAttribute("address", memberAddress);
	            model.addAttribute("detailAddress", "");
	        }
	    }

	    return "member/updateInfo"; // 수정 페이지로 forward
	}
	// 업데이트 완료용 컨트롤러 수정 김동준 2025-05-21
	@PostMapping("updateInfo")
	public String updateInfo(
	    Member inputMember,
	    @SessionAttribute("loginMember") Member loginMember,
	    @RequestParam("memberPostcode") String postcode,
	    @RequestParam("memberAddress") String address,
	    @RequestParam("memberAddressDetail") String detailAddress,
	    RedirectAttributes ra) {

	    // 주소 조립
	    String[] memberAddress = { postcode, address, detailAddress };
	    inputMember.setMemberNo(loginMember.getMemberNo());

	    int result = service.updateInfo(inputMember, memberAddress);

	    if (result > 0) {
	        ra.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
	        loginMember.setMemberNickname(inputMember.getMemberNickname());
	        loginMember.setMemberTel(inputMember.getMemberTel());
	        loginMember.setMemberAddress(String.join("^^^", memberAddress));
	    } else {
	        ra.addFlashAttribute("message", "회원 정보 수정 실패");
	    }

	    return "redirect:/myPage/info";
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
	// 김동준 수정 2025-05-20
	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
						  @SessionAttribute("loginMember")Member loginMember,
						  RedirectAttributes ra,
						  Model model) throws Exception {

		
		// 업로드 된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받을 예정
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) message = "변경 성공!";
		else		message = "변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		model.addAttribute("loginMember", loginMember);
		
		return "redirect:/myPage/updateInfo";
	}


}
