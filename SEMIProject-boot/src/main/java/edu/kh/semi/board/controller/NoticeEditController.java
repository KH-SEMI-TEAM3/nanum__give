package edu.kh.semi.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.Board;

import edu.kh.semi.board.model.service.NoticeBoardService;
import edu.kh.semi.board.model.service.NoticeEditService;
import edu.kh.semi.member.model.dto.Member;

@Controller
@RequestMapping("noticeEdit")
public class NoticeEditController {

    @Autowired
    private NoticeEditService service;

    @Autowired
    private NoticeBoardService boardService;

    /*게시글 작성*/
    @GetMapping("{boardCode:[0-9]+}/insert")
    public String boardInsert(@PathVariable("boardCode") int boardCode) {
        return "board/notice/noticeboard-write";
    }

    @PostMapping("{boardCode:[0-9]+}/insert")
    public String boardInsert(@PathVariable("boardCode") int boardCode,
                              @ModelAttribute Board inputBoard,
                              @SessionAttribute("loginMember") Member loginMember,
                              RedirectAttributes ra) throws Exception {

        inputBoard.setBoardCode(boardCode);
        inputBoard.setMemberNo(loginMember.getMemberNo());

        int boardNo = service.boardInsert(inputBoard);

        String path = null;
        String message = null;

        if (boardNo > 0) {
            message = "게시글이 작성되었습니다!";
            path = "/notice/" + boardNo ;
        } else {
            path = "insert";
            message = "게시글 작성 실패";
        }

        ra.addFlashAttribute("message", message);
        return "redirect:" + path;
    }

    /*게시글 수정*/
    @GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
    public String boardUpdate(@PathVariable("boardCode") int boardCode,
                              @PathVariable("boardNo") int boardNo,
                              @SessionAttribute("loginMember") Member loginMember,
                              Model model,
                              RedirectAttributes ra) {

        Map<String, Integer> map = new HashMap<>();
        map.put("boardCode", boardCode);
        map.put("boardNo", boardNo);

        Board board = boardService.selectOne(map);

        String message = null;
        String path = null;

        if (board == null) {
            message = "해당 게시글이 존재하지 않습니다";
            path = "redirect:/";
            ra.addFlashAttribute("message", message);

        } else if (board.getMemberNo() != loginMember.getMemberNo()) {
            message = "자신이 작성한 글만 수정 가능합니다!";
            path = String.format("redirect:/board/%d/%d", boardCode, boardNo);
            ra.addFlashAttribute("message", message);

        } else {
            path = "board/boardUpdate";
            model.addAttribute("board", board);
        }

        return path;
    }

    @PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
    public String boardUpdate(@PathVariable("boardCode") int boardCode,
                              @PathVariable("boardNo") int boardNo,
                              Board inputBoard,
                              @SessionAttribute("loginMember") Member loginMember,
                              RedirectAttributes ra,
                              @RequestParam(value = "cp", required = false, defaultValue = "1") int cp) throws Exception {

        inputBoard.setBoardCode(boardCode);
        inputBoard.setBoardNo(boardNo);
        inputBoard.setMemberNo(loginMember.getMemberNo());

        int result = service.boardUpdate(inputBoard);

        String message = null;
        String path = null;

        if (result > 0) {
            message = "게시글이 수정 되었습니다";
            path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
        } else {
            message = "수정 실패";
            path = "update";
        }

        ra.addFlashAttribute("message", message);
        return "redirect:" + path;
    }

    @RequestMapping(value = "{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete",
                    method = { RequestMethod.GET, RequestMethod.POST })
    public String boardDelete(@PathVariable("boardCode") int boardCode,
                              @PathVariable("boardNo") int boardNo,
                              @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
                              RedirectAttributes ra,
                              @SessionAttribute("loginMember") Member loginMember) {

        Map<String, Integer> map = new HashMap<>();
        map.put("boardCode", boardCode);
        map.put("boardNo", boardNo);
        map.put("memberNo", loginMember.getMemberNo());

        int result = service.boardDelete(map);

        String path = null;
        String message = null;

        if (result > 0) {
            message = "삭제 되었습니다";
            path = String.format("/board/%d?cp=%d", boardCode, cp);
        } else {
            message = "삭제 실패";
            path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
        }

        ra.addFlashAttribute("message", message);
        return "redirect:" + path;
    }
}
