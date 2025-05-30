package edu.kh.semi.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.admin.model.service.AdminService;
import edu.kh.semi.board.model.service.CommentService;
import edu.kh.semi.board.model.service.ShareBoardEditService;
import edu.kh.semi.board.model.service.ShareCommentService;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import edu.kh.semi.admin.model.service.AdminService;
import edu.kh.semi.board.model.service.FreeBoardService;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j  
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private ShareBoardEditService boardService; // 게시판 삭제용
	
	@Autowired
	private AdminService adminService; // 멤버 삭제용
  
	@Autowired
	private FreeBoardService freeBoardService;
	
	@Autowired
	private ShareCommentService shareCommentService; // 댓글 삭제용 (나눔게시판) 
	
	

	
	
	/** 일단 나눔게시판만 글 삭제
	 * @param boardNo
	 * @param cp
	 * @param memberNo
	 * @param boardCode
	 * @return
	 */
	@GetMapping("/{boardNo:[0-9]+}/boardDelete")
	public String boardDelete(@PathVariable("boardNo") int boardNo,
	                          @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
	                          @RequestParam(value = "memberNo") int memberNo,
	                          @RequestParam(value = "boardCode") int boardCode) {

	    Map<String, Integer> map = new HashMap<>();
	    map.put("boardCode", boardCode);
	    map.put("boardNo", boardNo);
	    map.put("memberNo", memberNo);

	    int result = boardService.boardDelete(map);

	    String path;
	    String message;

	    if (result > 0) {
	        message = "삭제되었습니다";
	        path = String.format("/share/list?cp=%d", cp);
	    } else {
	        message = "삭제 실패!";
	        path = String.format("/share/detail/%d?cp=%d", boardNo, cp);
	    }

	    return "redirect:" + path;
	}

	
	
	      
	 
	/** 일단 나눔게시판만 글 작성자 삭제
	 * @param memberNo
	 * @param boardNo
	 * @param cp
	 * @return
	 */
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
			
			message = " 회원 삭제 실패 !";

			path = String.format("/share/detail/%d?cp=%d",boardNo,cp);
		}
		return "redirect:"+path;
	}
	  
	

	/** 나눔게시판만 댓글 삭제
	 * @param boardCode
	 * @param commentNo
	 * @return
	 */
	@DeleteMapping("/{boardCode:[0-9]+}/commentDelete")
	@ResponseBody
	public int adminDeleteComment(
	    @PathVariable("boardCode") int boardCode,
	    @RequestBody int commentNo // 숫자 하나만
	) {
	    
  
	    int result = shareCommentService.delete(commentNo); 
	    return result;
	}
	
   
  
	/** 나눔게시판만 댓글 작성자 삭제. 똑같은 회원이라 getMapping으로 할까 했는데 DeleteMapping 비동기로 해서 동기식 getMapping한 위 함수랑은 다르게 해야 했음
	 * @param boardCode
	 * @param memberNo
	 * @return
	 */
	@DeleteMapping("/{boardCode:[0-9]+}/deleteCommentMemeber")
	@ResponseBody
	public int deleteCommentMemeber(@PathVariable("boardCode") int boardCode, 
			@RequestParam("memberNo") int memberNo) {
		
		int result = adminService.memberDelete(memberNo);
		
		return result;
	} 
		
		
	  

	/** 관리자용: 자유게시판 글 삭제 */
	@GetMapping("/free/boardDelete")
	public String freeBoardDelete(
	    @RequestParam("boardNo") int boardNo,
	    @RequestParam(value="cp", required = false, defaultValue = "1") int cp,
	    RedirectAttributes ra
	) {
	    log.info("자유게시판 글 삭제 요청: boardNo={}", boardNo);

	    int boardCode = 2; // 자유게시판 코드

	    Map<String, Integer> map = new HashMap<>();
	    map.put("boardCode", boardCode);
	    map.put("boardNo", boardNo);
	    


	    int result = freeBoardService.deleteBoard(boardNo);

	    String path;
	    if (result > 0) {
	    	ra.addFlashAttribute("message", "게시글이 삭제되었습니다.");
	        path = String.format("/free/list?cp=%d", cp);
	    } else {
	    	ra.addFlashAttribute("message", "게시글 삭제에 실패했습니다.");
	        path = String.format("/free/view/%d?cp=%d", boardNo, cp);
	    }

	    return "redirect:" + path;
	}

	/** 관리자용: 자유게시판 회원 삭제 */
	@GetMapping("/free/memberDelete")
	public String freeMemberDelete(
	    @RequestParam("memberNo") int memberNo,
	    @RequestParam("boardNo") int boardNo,
	    @RequestParam("cp") int cp,
	    RedirectAttributes ra
	) {
	    log.info("자유게시판 회원 삭제 요청: memberNo={}, boardNo={}", memberNo, boardNo);

	    int result = adminService.memberDelete(memberNo);

	    String path;
	    if (result > 0) {
	    	ra.addFlashAttribute("message", "회원이 삭제되었습니다.");
	        path = String.format("/free/list?cp=%d", cp);
	    } else {
	    	ra.addFlashAttribute("message", "회원 삭제에 실패했습니다.");
	        path = String.format("/free/view/%d?cp=%d", boardNo, cp);
	    }

	    return "redirect:" + path;
	}
	
}
