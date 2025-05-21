package edu.kh.semi.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
	
	@RequestMapping("/") //   "/" 요청 매핑 
	public String mainPage() {
		
		// 접두사/접미사 제외
		// classpath:/templates/
		// .html
		return "common/main";
	}
	
//	// LoginFilter -> loginError 리다이렉트
//	// -> message 만들어서 메인페이지로 리다이렉트
//	@GetMapping("loginError")
//	public String loginError(RedirectAttributes ra) {
//		ra.addFlashAttribute("message", "로그인 후 이용해 주세요");
//		return "redirect:/";
//	}

@GetMapping("/search")
public String redirectSearch(@RequestParam("key") String key,
                             @RequestParam("query") String query,
                             RedirectAttributes ra) {

    ra.addAttribute("query", query);

    return switch (key) {
        case "nanum" -> "redirect:/share/search";
        case "free" -> "redirect:/free/search";
        case "notice" -> "redirect:/notice/search";
        case "help" -> "redirect:/help/search";
        default -> "redirect:/";
    };
}
}