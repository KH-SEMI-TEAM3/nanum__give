package edu.kh.semi.search.controller;

import edu.kh.semi.QNABoard.model.servcie.QNABoardService;
import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.board.model.service.NoticeBoardService;
import edu.kh.semi.board.model.service.ShareBoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private FreeBoardService freeBoardService;

    @Autowired
    private NoticeBoardService noticeBoardService;

    @Autowired
    private QNABoardService qnaBoardService;

    @Autowired
    private ShareBoardService shareBoardService;

    @GetMapping("/result")
    public String searchAll(@RequestParam("query") String query, Model model) {
        List<Board> freeResults = freeBoardService.searchByKeyword(query);
        List<Board> noticeResults = noticeBoardService.searchByKeyword(query);
        List<Board> qnaResults = qnaBoardService.searchByKeyword(query);
        List<Board> shareResults = shareBoardService.searchByKeyword(query);

        model.addAttribute("query", query);
        model.addAttribute("freeBoardList", freeResults);
        model.addAttribute("noticeBoardList", noticeResults);
        model.addAttribute("qnaBoardList", qnaResults);
        model.addAttribute("shareBoardList", shareResults);

        return "search/result"; // 이 템플릿을 다음 단계에서 만듭니다
    }
}