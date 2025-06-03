package edu.kh.semi.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.common.images.model.service.BoardImageService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/free")
public class FreeBoardController {

	@Autowired
	private FreeBoardService service;
	
	@Autowired
	private BoardImageService boardImageService;

	/** 목록 조회 */
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {
		int listCount = service.getListCount();
		Pagination pagination = new Pagination(cp, listCount);

		List<Board> list = service.getList(pagination);
		model.addAttribute("pagination", pagination);
		model.addAttribute("boardList", list);
		model.addAttribute("cp", cp);
		return "board/free/freeboard";
	}

	/**
	 * 게시판 글쓰기
	 * 
	 * @param board
	 * @param session
	 * @param image
	 * @return
	 */
	@PostMapping("/write")
	public String Write(@ModelAttribute Board board, HttpSession session,
			@RequestParam(value = "boardImage", required = false) MultipartFile image,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {

		System.out.println("🧪 제목: " + board.getBoardTitle());
		System.out.println("🧪 내용: " + board.getBoardContent());
		
		// 로그인 여부 확인
		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null) {
			// 로그인 안 되어 있으면 로그인 페이지로 리다이렉트
			return "redirect:/member/login";
		}

		// Member 객체의 번호 설정 (int 캐스팅은 DTO 타입에 따라 유지)
		board.setMemberNo((int) loginMember.getMemberNo());
		// 자유 게시판 코드 설정
		board.setBoardCode(2); // 2: 자유게시판

		int boardNo = service.insertFreeBoard(board);
		board.setBoardNo(boardNo); // 이미지 저장용

		if (image != null && !image.isEmpty()) {
			String webPath = "/images/board/";
			boardImageService.saveBoardImage(board, image, webPath, session);
		}

		return "redirect:/free/view/" + boardNo;
	}

	@GetMapping("/write")
	public String showWriteForm(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
            Model model) {
		
		model.addAttribute("cp", cp);
		return "board/free/freeboardwriting"; // 버튼 get요청 용
	}

	/**
	 * 게시판 조회
	 * 
	 * @param boardNo
	 * @param model
	 * @return
	 */
	@GetMapping("/view/{boardNo}")
	public String detail(@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "edit", required = false, defaultValue = "false") boolean edit,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			Model model,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		
		System.out.println("▶ cp param = " + cp);
		service.updateReadCount(boardNo); // 조회수 증가

		
		Board board = service.getFreeBoard(boardNo);

		model.addAttribute("board", board);

		model.addAttribute("editMode", edit);
		
		model.addAttribute("cp", cp);
		
		// 관리자 여부 판단 후 model에 전달
		boolean isAdmin = loginMember != null && loginMember.getAuthority() == 0;
		model.addAttribute("isAdmin", isAdmin);

	    String memberDelFl = service.getMemberDelFlByMemberNo(board.getMemberNo());
	    model.addAttribute("writerDeleted", "Y".equals(memberDelFl));
		// 수정모드(edit)가 아닐 때만 댓글 불러오기
		if (!edit) {
			model.addAttribute("commentList", service.getCommentList(boardNo));
		}
		return "board/free/freeboarddetail";
	}

	/**
	 * 게시글 수정 (AJAX 방식)
	 * 
	 * @param boardNo
	 * @param boardTitle
	 * @param boardContent
	 * @param memberNo
	 * @param boardImage
	 * @param session
	 * @return
	 */
	@PostMapping("/update")
	@ResponseBody
	public Map<String, Object> updateFreeBoard(@RequestParam("boardNo") int boardNo,
			@RequestParam("boardTitle") String boardTitle, @RequestParam("boardContent") String boardContent,
			@RequestParam("memberNo") int memberNo,
			@RequestParam(value = "boardImage", required = false) MultipartFile boardImage, HttpSession session) {

		Map<String, Object> response = new HashMap<>();
		Member loginMember = (Member) session.getAttribute("loginMember");

		if (loginMember == null || loginMember.getMemberNo() != memberNo) {
			response.put("success", false);
			response.put("message", "수정 권한이 없습니다.");
			return response;
		}

		// DTO에 값 채우기
		Board board = new Board();
		board.setBoardNo(boardNo);
		board.setBoardTitle(boardTitle);
		board.setBoardContent(boardContent);
		board.setMemberNo(memberNo);

		// 서비스 호출
		int result = service.updateBoard(board, boardImage);

		response.put("success", result > 0);
		return response;
	}

	/**
	 * 게시판 삭제 기능 추가
	 * 
	 * @param boardNo
	 * @param session
	 * @param ra
	 * @return
	 */
	@GetMapping("/delete/{boardNo}")
	public String deleteBoard(@PathVariable("boardNo") int boardNo, HttpSession session, RedirectAttributes ra) {
		Member loginMember = (Member) session.getAttribute("loginMember");
		Board board = service.getFreeBoard(boardNo);

		if (loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
			ra.addFlashAttribute("message", "삭제 권한이 없습니다.");
			return "redirect:/free/list";
		}

		int result = service.deleteBoard(boardNo);
		if (result > 0) {
			ra.addFlashAttribute("message", "삭제되었습니다.");
			return "redirect:/free/list";
		} else {
			ra.addFlashAttribute("message", "삭제 실패");
			return "redirect:/error";
		}
	}
	
	@GetMapping("/search")
	public String searchFreeBoard(
	    @RequestParam("key") String key,
	    @RequestParam("query") String query,
	    @RequestParam(value = "page", defaultValue = "1") int page,
	    Model model) {

	    Map<String, Object> result = service.searchByKeyAndQuery(key, query, page);

	    model.addAttribute("boardList", result.get("boardList"));
	    model.addAttribute("pagination", result.get("pagination"));
	    model.addAttribute("key", key);
	    model.addAttribute("query", query);

	    return "board/free/freeboard"; // 기존 목록 페이지 그대로 사용
	}
}