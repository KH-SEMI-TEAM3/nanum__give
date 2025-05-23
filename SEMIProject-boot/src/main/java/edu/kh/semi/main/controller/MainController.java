package edu.kh.semi.main.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.board.model.service.ShareBoardService;

@Controller
public class MainController {
	@Autowired
	private ShareBoardService shareBoardService;
	
	@GetMapping("/")
	public String mainPage(Model model) {
	    List<ShareBoard> list = shareBoardService.selectRecent();

	    List<List<ShareBoard>> grouped = new ArrayList<>();
	    for (int i = 0; i < list.size(); i += 4) {
	        grouped.add(list.subList(i, Math.min(i + 4, list.size())));
	    }

	    model.addAttribute("groupedShareList", grouped);
	    return "common/main";
	}
	
//	// LoginFilter -> loginError 리다이렉트
//	// -> message 만들어서 메인페이지로 리다이렉트
//	@GetMapping("loginError")
//	public String loginError(RedirectAttributes ra) {
//		ra.addFlashAttribute("message", "로그인 후 이용해 주세요");
//		return "redirect:/";
//	}
}