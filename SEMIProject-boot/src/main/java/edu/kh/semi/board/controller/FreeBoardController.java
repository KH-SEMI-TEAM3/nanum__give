package edu.kh.semi.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/freeBoard")
public class FreeBoardController {

    @Autowired
    private FreeBoardService service;

    /** 목록 조회 */
    @GetMapping("/list")
    public String list(Model model) {
        List<Board> list = service.getFreeBoardList();
        model.addAttribute("list", list);
        return "board/freeBoard-list";
    }

    /** 상세 조회 */
    @GetMapping("/view/{boardNo}")
    public String view(@PathVariable Long boardNo, Model model) {
        Board board = service.getFreeBoard(boardNo);
        model.addAttribute("board", board);
        return "board/freeBoard-view";
    }

    /** 글쓰기 폼 */
    @GetMapping("/write")
    public String writeForm() {
        return "board/freeBoard-write";
    }

    /** 글쓰기 처리 */
    @PostMapping("/write")
    public String write(Board board, HttpSession session) {
        Member login = (Member) session.getAttribute("loginMember");
        board.setMemberNo((long)login.getMemberNo());
        service.createFreeBoard(board);
        return "redirect:/freeBoard/list";
    }

    /** 수정 폼 */
    @GetMapping("/edit/{boardNo}")
    public String editForm(@PathVariable Long boardNo, Model model) {
        Board board = service.getFreeBoard(boardNo);
        model.addAttribute("board", board);
        return "board/freeBoard-edit";
    }

    /** 수정 처리 */
    @PostMapping("/edit")
    public String edit(Board board) {
        service.modifyFreeBoard(board);
        return "redirect:/freeBoard/view/" + board.getBoardNo();
    }

    /** 삭제 처리 */
    @PostMapping("/delete/{boardNo}")
    public String delete(@PathVariable Long boardNo) {
        service.removeFreeBoard(boardNo);
        return "redirect:/freeBoard/list";
    }
}