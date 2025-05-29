package edu.kh.semi.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.semi.admin.model.service.AdminService;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.board.model.service.ShareBoardEditService;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j  
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private ShareBoardEditService boardService;
	
	@Autowired
	private FreeBoardService freeBoardService;
	
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
			path = String.format("/share/list?cp=%d", cp);					// /help/1?cp=7
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
			path = String.format("/share/list?cp=%d", cp);		
		}		
		else {			
			message = " 회원 삭제 실패!";
			path = String.format("/share/detail/%d?cp=%d",boardNo,cp);
		}
		return "redirect:"+path;
	}
	
	/** 관리자용: 자유게시판 글 삭제 */
	@GetMapping("/free/boardDelete")
	public String freeBoardDelete(
	    @RequestParam("boardNo") int boardNo,
	    @RequestParam("memberNo") int memberNo,
	    @RequestParam(value="cp", required = false, defaultValue = "1") int cp
	) {
	    log.info("자유게시판 글 삭제 요청: boardNo={}, memberNo={}", boardNo, memberNo);

	    int boardCode = 2; // 자유게시판 코드

	    Map<String, Integer> map = new HashMap<>();
	    map.put("boardCode", boardCode);
	    map.put("boardNo", boardNo);
	    map.put("memberNo", memberNo);

	    int result = freeBoardService.deleteBoard(boardNo);

	    String path;
	    if (result > 0) {
	        path = String.format("/free/list?cp=%d", cp);
	    } else {
	        path = String.format("/free/view/%d?cp=%d", boardNo, cp);
	    }

	    return "redirect:" + path;
	}

	/** 관리자용: 자유게시판 회원 삭제 */
	@GetMapping("/free/memberDelete")
	public String freeMemberDelete(
	    @RequestParam("memberNo") int memberNo,
	    @RequestParam("boardNo") int boardNo,
	    @RequestParam("cp") int cp
	) {
	    log.info("자유게시판 회원 삭제 요청: memberNo={}, boardNo={}", memberNo, boardNo);

	    int result = adminService.memberDelete(memberNo);

	    String path;
	    if (result > 0) {
	        path = String.format("/free/list?cp=%d", cp);
	    } else {
	        path = String.format("/free/view/%d?cp=%d", boardNo, cp);
	    }

	    return "redirect:" + path;
	}
	
}
