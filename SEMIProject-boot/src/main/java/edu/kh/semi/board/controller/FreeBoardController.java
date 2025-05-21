package edu.kh.semi.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/free")
public class FreeBoardController {

	@Autowired
	private FreeBoardService service;

	/** 목록 조회 */
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
		int listCount = service.getListCount();
		Pagination pagination = new Pagination(cp, listCount);

		List<Board> list = service.getList(pagination);
		model.addAttribute("pagination", pagination);
		model.addAttribute("boardList", list);
		return "board/free/freeboard";
	}

	@PostMapping("/freeBoard/write")
	public String Write(Board board, HttpSession session,
			@RequestParam(value = "boardImage", required = false) MultipartFile image) {
		Member loginMember = (Member) session.getAttribute("loginMember");
		board.setMemberNo((long) loginMember.getMemberNo());
		board.setBoardCode(2);

		int result = service.insertFreeBoard(board);

		if (result > 0) {
			// 성공 시
			// 이미지 저장은 선택 처리
			if (image != null && !image.isEmpty()) {
				// 별도 이미지 처리 메서드
			}
			return "redirect:/free/list";

		} else {
			// 실패 시
			return "redirect:/error";
		}
	}
	
	@GetMapping("/view/{boardNo}")
	public String detail(@PathVariable("boardNo") Long boardNo, Model model) {
		Board board = service.getFreeBoard(boardNo);
		
		model.addAttribute("board", board);
		
		// model.addAttribute("commentList", service.getCommentList(boardNo));
		return "board/free/freeboarddetail";
	}
	
	 /** 게시글 수정 (AJAX 방식) */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateFreeBoard(@RequestBody Board board) {
        Map<String, Object> response = new HashMap<>();
        int result = service.updateBoard(board);
        response.put("success", result > 0);
        return response;
    }
	
	

}



//    /** 수정 처리 */
//    @PostMapping("/edit")
//    public String edit(Board board) {
//        service.modifyFreeBoard(board);
//        return "redirect:/free/view/" + board.getBoardNo();
//    }
//
//    /** 삭제 처리 */
//    @PostMapping("/delete/{boardNo}")
//    public String delete(@PathVariable Long boardNo) {
//        service.removeFreeBoard(boardNo);
//        return "redirect:/free";
//    }
