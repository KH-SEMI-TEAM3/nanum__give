package edu.kh.semi.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.service.NoticeBoardService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("notice")
public class NoticeBoardController {

    @Autowired
    private NoticeBoardService service;

    /** 목록 조회 */
    @GetMapping("list")
    public String list(Model model) {
        List<Board> list = service.getNoticeList();
        model.addAttribute("list", list);
        return "board/noticeBoard-list";
    }

    /** 상세 조회 */
    @GetMapping("/view/{boardNo}")
    public String view(@PathVariable Long boardNo, Model model) {
        Board board = service.getNoticeBoard(boardNo);
        model.addAttribute("board", board);
        return "board/noticeBoard-view";
    }

    /** 글쓰기 폼 (관리자만 접근 허용) */
    @GetMapping("/write")
    public String writeForm(HttpSession session) {
        Member login = (Member) session.getAttribute("loginMember");
        if (login == null || login.getAuthority() != 0) { // 0 == 관리자
            return "redirect:/noticeBoard/list";
        }
        return "board/noticeBoard-write";
    }

    /** 글쓰기 처리 */
    @PostMapping("/write")
    public String write(Board board, HttpSession session) {
        Member login = (Member) session.getAttribute("loginMember");
        board.setMemberNo((long)login.getMemberNo());
        board.setBoardCode(1); // 공지 게시판 코드 가정

        service.createNoticeBoard(board);
        return "redirect:/noticeBoard/list";
    }

 

    /** 수정 폼 (작성자만) */
    @GetMapping("/edit/{boardNo}")
    public String editForm(@PathVariable Long boardNo, HttpSession session, Model model) {
        Board board = service.getNoticeBoard(boardNo);
        Member login = (Member) session.getAttribute("loginMember");

        if (!board.getMemberNo().equals(login.getMemberNo())) {
            return "redirect:/noticeBoard/view/" + boardNo;
        }

        model.addAttribute("board", board);
        return "board/noticeBoard-edit";
    }

    /** 수정 처리 */
    @PostMapping("/edit")
    public String edit(Board board, HttpSession session) {
        Member login = (Member) session.getAttribute("loginMember");
        if (!board.getMemberNo().equals(login.getMemberNo())) {
            return "redirect:/noticeBoard/view/" + board.getBoardNo();
        }

        service.modifyNoticeBoard(board);
        return "redirect:/noticeBoard/view/" + board.getBoardNo();
    }

    /** 삭제 처리 (작성자만) */
    @PostMapping("/delete/{boardNo}")
    public String delete(@PathVariable Long boardNo, HttpSession session) {
        Board board = service.getNoticeBoard(boardNo);
        Member login = (Member) session.getAttribute("loginMember");

        if (!board.getMemberNo().equals(login.getMemberNo())) {
            return "redirect:/noticeBoard/view/" + boardNo;
        }

        service.removeNoticeBoard(boardNo);
        return "redirect:/noticeBoard/list";
    }
    
   
}
