package edu.kh.semi.admin.controller;

import java.nio.channels.SelectableChannel;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import edu.kh.semi.admin.model.service.AdminService;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.board.model.service.QNABoardService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ShareBoardEditService shareBoardService; // 나눔 게시판 삭제용

	@Autowired
	private QNABoardService boardService;// 그 외 게시판 삭제용

	@Autowired
	private AdminService adminService; // 멤버 삭제용

	@Autowired
	private FreeBoardService freeBoardService;

	@Autowired
	private ShareCommentService shareCommentService; // 댓글 삭제용 (나눔게시판)

	@Autowired
	private CommentService commentService;
	/**
	 * 나눔게시판, 문의게시판 글삭제
	 * 
	 * @param boardNo
	 * @param cp
	 * @param memberNo
	 * @param boardCode
	 * @return
	 */
	@GetMapping("/{boardNo:[0-9]+}/boardDelete")
	public String boardDelete(@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam(value = "memberNo") int memberNo, @RequestParam(value = "boardCode") int boardCode,
			RedirectAttributes ra, HttpServletRequest request

	) {
		// prev페이지가 세션에 저장 안되니까 그냥 받은 boardCode에 따라 다른 곳으로 가는 수밖에 없다. 1<->share 2<-> 자유
		// 3<-> 공지 4<->문의 이런식으로

		String pathCode = null;

		switch (boardCode) {
		case 1:
			pathCode = "share";
			break;
		case 2:
			pathCode = "free";
			break;
		case 3:
			pathCode = "notice";
			break;
		default:
			pathCode = "help";
			break;
		}

		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", memberNo);

		int result = 0;

		// 보드에 대한 게시판마다 다른 서비스를 호출하는 과정
		if (boardCode == 1) {

			result = shareBoardService.boardDelete(map);
		}

		else {

			result = boardService.boardDelete(map);
		}

		String message = null;

		if (result > 0) {
			message = "관리자 권한으로 글이 삭제되었습니다";
			ra.addFlashAttribute("message", message);
			return "redirect:/" + pathCode + "/list?cp=" + cp;
		}

		else {
			message = "삭제 실패!";
			ra.addFlashAttribute("message", message);

			String referer = request.getHeader("Referer");

			String failUrl = (referer != null) ? referer : String.format("/" + pathCode + "/list");

			return "redirect:" + failUrl;
		}

	}

	

	/**
	 * 나눔게시판, 문의게시판 글 작성자 삭제
	 * 
	 * @param memberNo
	 * @param boardNo
	 * @param cp
	 * @return
	 */
	@GetMapping("/memberDelete")
	public String memberDelete(@RequestParam("memberNo") int memberNo, @RequestParam("boardCode") int boardCode,
			@RequestParam("boardNo") int boardNo, @RequestParam("cp") int cp, RedirectAttributes ra,
			HttpServletRequest request) {

		
		String pathCode = null;

		switch (boardCode) {
		case 1:
			pathCode = "share";
			break;
		case 2:
			pathCode = "free";
			break;
		case 3:
			pathCode = "notice";
			break;
		default:
			pathCode = "help";
			break;
		}
		int result = adminService.memberDelete(memberNo);

		String message = null;

		if (result > 0) {
			message = "관리자 권한으로 회원이 삭제되었습니다";
			ra.addFlashAttribute("message", message);
			return "redirect:/" + pathCode + "/list?cp=" + cp;

		}

		else {

			message = "회원삭제 실패!";
			ra.addFlashAttribute("message", message);

			String referer = request.getHeader("Referer");

			String failUrl = (referer != null) ? referer : String.format("/" + pathCode + "/list");

			return "redirect:" + failUrl;
		}

	}

	/**
	 * 나눔게시판, 공지게시판 댓글 삭제
	 * 
	 * @param boardCode
	 * @param commentNo
	 * @return
	 */
	@DeleteMapping("/{boardCode:[0-9]+}/commentDelete")
	@ResponseBody
	public int adminDeleteComment(@PathVariable("boardCode") int boardCode, @RequestBody int commentNo // 숫자 하나만
	) {

		int result =0;
		if(boardCode==1) {
		result = shareCommentService.delete(commentNo);}
		
		else {
			result = commentService.delete(commentNo);
			
		}
		return result;
	}

	
	
	
	/**
	 * 나눔게시판, 공지게시판  댓글 작성자 삭제. (똑같은 회원이라 위 get 메서드로 통일할까 했는데 비동기로 해서 동기식 getMapping한 위 함수랑은 다르게 해야 했음)
	 * 
	 * @param boardCode
	 * @param memberNo
	 * @return
	 */
	@DeleteMapping("/{boardCode:[0-9]+}/deleteCommentMemeber")
	@ResponseBody
	public int deleteCommentMemeber(@PathVariable("boardCode") int boardCode, @RequestParam("memberNo") int memberNo) {

		int result = adminService.memberDelete(memberNo);

		return result;
	}

	
	
	
	
	
	/** 관리자용: 자유게시판 글 삭제 */
	@GetMapping("/free/boardDelete")
	public String freeBoardDelete(@RequestParam("boardNo") int boardNo, @RequestParam("memberNo") int memberNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
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
	public String freeMemberDelete(@RequestParam("memberNo") int memberNo, @RequestParam("boardNo") int boardNo,
			@RequestParam("cp") int cp) {
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
