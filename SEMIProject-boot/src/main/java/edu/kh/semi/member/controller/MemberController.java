package edu.kh.semi.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SessionAttributes({"loginMember", "tempMemberId"})
@Controller
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	/** 로그인 페이지로 이동
	 * @return
	 */
	@GetMapping("loginPage")
	public String loginPage(HttpServletRequest req, HttpSession session) {
		
		// 사용자가 로그인 페이지로 오기 바로 전의 페이지 주소(URL) 를 Referer 헤더에서 가져옴.
		// Referer - 웹 브라우저가 현재 요청을 보내기 전에 사용자가 보고 있던 웹 페이지의 주소(URL) 를 의미.
	    String referer = req.getHeader("Referer");

	    // 메인 페이지 또는 로그인 관련 페이지는 저장하지 않음.
	    
	    // referer != null : 이전 페이지가 존재할 때만 실행.
	    // !referer.contains("/login") : 로그인 관련 페이지가 referer인 경우는 제외.
	    // 								→ 로그인 → 실패 → 다시 로그인 같은 경우엔 무한 반복될 수 있으므로 필터링.
	    // !referer.contains("/signup"): 회원가입 페이지도 제외.
	    // 								 → 회원가입 완료 후 로그인 시 다시 회원가입 페이지로 가는 걸 방지하기 위해서.
	    if (referer != null && !referer.contains("/login") && !referer.contains("/signup")) {
	    	
	    	// 위 조건을 통과하면, 세션에 "prevPage" 라는 이름으로 이전 페이지 주소를 저장.
	    	// → 이 값은 로그인 성공 시 꺼내서 해당 페이지로 리디렉션할 때 사용.
	        session.setAttribute("prevPage", referer);
	    }

	    return "member/login";
	}
	 
	/** 로그인 
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략) memberEmail, memberPw 세팅 된 상태
	 * @param ra : 리다이렉트 시 request scope -> session scope -> request로 데이터 전달
	 * @param model : 데이터 전달용 객체(기본 request scope)
	 * 				/ (@SessionAttributes 어노테이션과 함께 사용시 session scope 이동)
	 * 
	 * @return
	 */
	@PostMapping("login")
	public String login(Member inputMember,
	                    RedirectAttributes ra,
	                    Model model,
	                    @RequestParam(value="saveId", required = false) String saveId,
	                    HttpServletResponse resp,
	                    HttpSession session) {

	    Member loginMember = service.login(inputMember);

	    if(loginMember == null) {
	        ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
	        return "redirect:/member/loginPage";
	    }

	    model.addAttribute("loginMember", loginMember);

	    // 아이디 저장 쿠키 설정
	    Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
	    cookie.setPath("/");
	    if(saveId != null) {
	        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
	    } else {
	        cookie.setMaxAge(0); // 삭제
	    }
	    resp.addCookie(cookie);

	    // 로그인 전 페이지로 이동
	    String prevPage = (String) session.getAttribute("prevPage"); // 이전 페이지 주소를 세션에서 꺼냄
	    session.removeAttribute("prevPage"); // 재사용 방지를 위해 즉시 삭제

	    // 이전 페이지가 있다면 그 주소로 이동
	    if (prevPage != null) {
	        return "redirect:" + prevPage;
	    }

	    return "redirect:/"; // 기본은 메인페이지
	}
	
	/** 로그아웃 : session에 저장된 로그인 된 회원 정보를 없앰
	 * @param status : @SessionAttributes로 지정된 특정 속성을 세션에서 제거 기능 제공 객체
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료 시킴 (== @SessionAttributes로 등록된 세션 제거)
		
		return "redirect:/";
	}
	
	/** 회원가입 페이지로 이동
	 * @return
	 */
	@GetMapping("signupPage")
	public String signupPage() {
		return "member/signup";
	}
	
	/** 회원 아이디 중복 검사(비동기 요청)
	 * @param memberId
	 * @return
	 */
	@ResponseBody
	@GetMapping("checkMemberId")
	public int checkId(@RequestParam("memberId") String memberId) {
		return service.checkMemberId(memberId);
	}
	
	/** 이메일 중복 검사(비동기 요청)
	 * @param memberEmail
	 * @return
	 */
	@ResponseBody
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
	
	/** 닉네임 중복 검사(비동기 요청)
	 * @param memberNickname
	 * @return
	 */
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}
	
	/** 회원가입
	 * @param inputMember : 입력 된 회원 번호 (주소도 있지만 따로 배열로 받아 처리)
	 * @param memberAddress : 입력 된 주소 3개의 값을 배열로 전달 
	 * @param ra : 리다이렉트 시 request -> session -> request로 데이터 전달하는 객체
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember, @RequestParam("memberAddress") String[] memberAddress, RedirectAttributes ra) {
		
		// 회원가입 서비스 호출
		int result = service.signup(inputMember, memberAddress);
		
		String path = null;
		String message = null;
		
//		if(result > 0) { // 성공시
//			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다!";
//			path = "/member/login";
//			
//		} else { // 실패
//			message = "회원가입 실패";
//			path = "/member/signup";
//		}
//		
//		ra.addFlashAttribute("message", message);
//		
//		return path;
		
		if (result > 0) {
		    ra.addFlashAttribute("message", inputMember.getMemberNickname() + "님의 가입을 환영합니다!");
		    return "redirect:/member/loginPage";
		} else {
		    ra.addFlashAttribute("message", "회원가입 실패");
		    return "redirect:/member/signupPage";
		}
		// 성공 -> redirect:/
		// 성공 -> redirect:signup (상대경로)
		// 현재 주소 /member/signup (GET 방식 요청)
	}
	
	/** 아이디 찾기 페이지로 이동
	 * @return
	 */
	@GetMapping("findIdPage")
	public String findIdPage() {
		return "member/findId";
	}
	
	/** 아이디 찾기 시 가입한 회원 이메일 조회
	 * @param memberEmail
	 * @return
	 */
	@ResponseBody
	@GetMapping("findCheckEmail")
	public int findCheckEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
	
	/** 찾은 아이디 결과 가지고 페이지 이동
	 * @param email
	 * @param model
	 * @return
	 */
	@PostMapping("findId")
	public String findId(@RequestParam("email") String email, Model model) {
		String memberId = service.findId(email);

	    if (memberId != null) {
	        model.addAttribute("memberId", memberId);
	        return "member/findIdResult"; // 아이디 결과 보여줄 뷰
	    } else {
	        model.addAttribute("errorMsg", "입력하신 이메일로 가입된 계정이 없습니다.");
	        return "member/findId"; // 다시 찾기 폼 페이지로
	    }
	}
	
	/** 비밀번호 찾기 페이지로 이동
	 * @return
	 */
	@GetMapping("findPwPage")
	public String findPwPage() {
		return "member/findPw";
	}
	
	/** 비밀번호 찾기 결과 가지고 페이지 이동
	 * @return
	 */
	@PostMapping("findPw")
	public String findPw(Member inputMember, Model model, RedirectAttributes ra) {
		
		int result = service.findPw(inputMember);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			
			// 인증된 사용자 아이디를 세션에 저장
			model.addAttribute("tempMemberId", inputMember.getMemberId());
			
			message = "확인되었습니다. 새 비밀번호를 만들어 주세요.";
			path = "member/findPwResult"; 
			
		} else {
			message = "조회하신 아이디가 없습니다.";
			path = "member/findPw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/" + path;
	}
	
	@GetMapping("findPwResult")
	public String findPwResult() {
	    return "member/findPwResult"; // 메시지 출력할 뷰
	}
	
	/** 새 비밀번호로 변경하기
	 * @param paramMap
	 * @param memberId
	 * @param ra
	 * @return
	 */
	@PostMapping("newPw")
	public String newPw(@RequestParam Map<String, String> paramMap, @SessionAttribute("tempMemberId") String memberId, RedirectAttributes ra, SessionStatus status) {
		
		// 중복 제거 로직
	    if (!paramMap.get("memberPw").equals(paramMap.get("memberPwConfirm"))) {
	        ra.addFlashAttribute("message", "비밀번호가 일치하지 않습니다.");
	        return "redirect:/member/findPwResult";
	    }
		
		paramMap.put("memberId", memberId); // memberId를 paramMap에 넣음
		int result = service.newPw(paramMap);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			
			status.setComplete();
			
			message = "새 비밀번호로 변경되었습니다. 로그인을 진행해주세요.";
			path = "member/loginPage"; 
			
		} else {
			message = "비밀번호 변경이 실패하였습니다.";
			path = "member/findPwResult";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/" + path;
	}
	
	/** 비밀번호 변경 화면 이동
	 * @return
	 */
	@GetMapping("changePw") // /myPage/profile GET 요청 매핑
	public String changePw(Model model) {
		return "member/myPage-changePw";
	}
	
	/** 비밀번호 변경
	 * @param paramMap : 모든 파라미터를(요청 데이터)를 맵으로 저장
	 * @param loginMember : 세션에 등록된 현재 로그인한 회원 정보
	 * @param ra 
	 * @return
	 */
	@PostMapping("changePw") // /myPage/changePw POST 요청 매핑
	public String changePw(@RequestParam Map<String, String> paramMap,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) {
		// paramMap = {currentPw=asd123, newPw=pass02!, newPwConfirm=pass02!}
		
		// 로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();
		
		// 현재 + 새 비번 + 회원번호를 서비스로 전달
		int result = service.changePw(paramMap, memberNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			// 변경 성공 시
			message = "비밀번호가 변경되었습니다!";
			path = "/myPage/info";
			
		} else {
			// 변경 실패 시
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "/member/changePw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path ;
	}
	
	/** 회원 탈퇴 화면 이동
	 * @return
	 */
	@GetMapping("quit")
	public String quit() {
		return "member/myPage-quit";
	}
	
	/** 회원 탈퇴
	 * @param memberPw : 입력 받은 비밀번호
	 * @param loginMember : 로그인한 회원 정보(세션)
	 * @param status : 세션 완료 용도의 객체 -> @SessionAttributes 로 등록된 세션을
	 * @return
	 */
	@PostMapping("quit")
	public String secession(@RequestParam("memberPw") String memberPw,
							@SessionAttribute(value = "loginMember", required = false) Member loginMember,
							RedirectAttributes ra,
							SessionStatus status) {
		
		// 로그인한 회원의 회원번호 꺼내기
		int memberNo = loginMember.getMemberNo();
		
		// 서비스 호출 (입력받은 비밀번호, 로그인한 회원번호)
		int result = service.secession(memberPw, memberNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			message = "탈퇴 되었습니다.";
			path = "/";
			
			status.setComplete();
			
		} else {		
			message = "비밀번호가 일치하지 않습니다.";
			path = "quit";
		}
		
		ra.addFlashAttribute("message", message);
		
		// 탈퇴 성공 -> redirect:/ (메인페이지)
		// 탈퇴 실패 -> redirect:secession (상대경로)
		//				/myPage/secession (현재경로 Post)
		//				/myPage/secession (GET 요청)
		
		return "redirect:" + path;
	}
}