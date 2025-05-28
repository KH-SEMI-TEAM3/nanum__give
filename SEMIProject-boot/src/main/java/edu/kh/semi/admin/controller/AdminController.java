package edu.kh.semi.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.admin.model.service.AdminService;
import edu.kh.semi.board.model.service.ShareBoardEditService;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j  
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private ShareBoardEditService boardService;
	
	@Autowired
	private AdminService adminService;
	
	@GetMapping("/{boardNo:[0-9]+}/boardDelete")
	public String boardDelete (@PathVariable("boardNo") int boardNo,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value = "memberNo") int memberNo) {
		 
		
		log.info("컨트롤러에 도달함");
		int boardCode =1;
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", memberNo);
		
		int result = boardService.boardDelete(map);
		
		String path = null;
		String message =null;
		
		if(result >0) {
			message = "삭제되었습니다";
			path = String.format("/share/list?cp=%d", cp);
					// /help/1?cp=7
		}
		
		else {
			
			message = "삭제 실패!";
			path = String.format("/share/detail/%d?cp=%d",boardNo,cp);

		}

		
		return "redirect:"+path;
	}
	
	
	
	
	
	@GetMapping("/memberDelete")
	public String memberDelete (@RequestParam("memberNo") int memberNo, @RequestParam("boardNo") int boardNo, @RequestParam("cp") int cp
		) {
		 
		
		log.info("컨트롤러에 도달함");
		int boardCode =1;
		
		
		
		int result = adminService.memberDelete(memberNo);
		
		String path = null;
		String message =null;
		
		if(result >0) {
			message = "회원이 삭제되었습니다";
			path = String.format("/share/detail/%d?cp=%d",boardNo,cp);
		
		}
		
		else {
			
			message = " 회원 삭제 실패!";
			path = String.format("/share/detail/%d?cp=%d",boardNo,cp);

		}

		
		return "redirect:"+path;
	}
	
	
	


}
