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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	/** 로그인 페이지로 이동
	 * @return
	 */
	@GetMapping("loginPage")
	public String loginPage() {
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
	public String login(Member inputMember, RedirectAttributes ra, Model model, @RequestParam(value="saveId", required = false) String saveId, HttpServletResponse resp) {
		
		// 체크박스
		// - 체크가 된 경우 : "on"
		// - 체크가 안된 경우 : null
		
		// 로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "redirect:/member/loginPage";
			
		} else { // 로그인 성공 시
			// session scope에 loginMember 추가
			model.addAttribute("loginMember", loginMember);
			// 1단계 : model을 이용하여 request scope에 세팅됨
			// 2단계 : 클래스 위에 @SessionAttributes() 어노테이션 작성하여 session scope로 loginMember를 이동
			
			// ********** Cookie ********** 
			// 로그인 시 작성한 아이디를 쿠키에 저장
			
			// 쿠키 객체 생성(K:V)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			// saveId = user01
			
			// 쿠키가 적용될 경로 설정
			// -> 클라이언트가 어떤 요청을 할 때 쿠키를 첨부할지 지정
			// ex) "/" : IP 또는 도메인 또는 localhost
			// --> 메인페이지 + 그 하위 주소 모두
			cookie.setPath("/");
			
			// 쿠키의 만료 기간 지정
			if(saveId != null) { // 아이디 저장 체크박스 체크했을때
				cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 (초 단위로 지정)
				
			} else { // 체크 안했을 때
				cookie.setMaxAge(0); // 0초 (클라이언트 쿠키 삭제)
			}
			
			// 응답 객체에 쿠키 추가 -> 클라이언트에게 전달
			resp.addCookie(cookie);
		}
		
		return "redirect:/"; // 메인페이지 재요청 
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
		
		if(result > 0) { // 성공시
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다!";
			path = "member/login";
			
		} else { // 실패
			message = "회원가입 실패";
			path = "member/signup";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
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
			message = "확인되었습니다. 새 비밀번호를 만들어 주세요.";
			path = "member/findPwResult"; 
			
		} else {
			message = "조회하신 아이디가 없습니다.";
			path = "member/findPw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	/** 새 비밀번호로 변경하기
	 * @param memberPw
	 * @param ra
	 * @return
	 */
	@PostMapping("newPw")
	public String newPw(@RequestParam Map<String, String> paramMap, RedirectAttributes ra) {
		
		int result = service.changePw(paramMap);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			message = "새 비밀번호로 변경되었습니다. 로그인을 진행해주세요.";
			path = "member/loginPage"; 
			
		} else {
			message = "비밀번호 변경이 실패하였습니다.";
			path = "member/findPwResult";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
}