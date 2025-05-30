package edu.kh.semi.search.controller;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.service.FreeBoardService;
import edu.kh.semi.board.model.service.NoticeBoardService;
import edu.kh.semi.board.model.service.ShareBoardService;
import edu.kh.semi.board.model.service.QNABoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
    public String searchAll(
        @RequestParam("query") String query,
        @RequestParam(value = "freePage", defaultValue = "1") int freePage,
        @RequestParam(value = "noticePage", defaultValue = "1") int noticePage,
        @RequestParam(value = "qnaPage", defaultValue = "1") int qnaPage,
        @RequestParam(value = "sharePage", defaultValue = "1") int sharePage,
        Model model) {

        Map<String, Object> freeMap = freeBoardService.searchByKeyword(query, freePage);
        Map<String, Object> noticeMap = noticeBoardService.searchByKeyword(query, noticePage);
        Map<String, Object> qnaMap = qnaBoardService.searchByKeyword(query, qnaPage);
//        Map<String, Object> shareMap = shareBoardService.searchByKeyword(query, sharePage);

        model.addAttribute("query", query);

        model.addAttribute("freeBoardList", freeMap.get("boardList"));
        model.addAttribute("freePagination", freeMap.get("pagination"));

        model.addAttribute("noticeBoardList", noticeMap.get("boardList"));
        model.addAttribute("noticePagination", noticeMap.get("pagination"));

        model.addAttribute("qnaBoardList", qnaMap.get("boardList"));
        model.addAttribute("qnaPagination", qnaMap.get("pagination"));

//        model.addAttribute("shareBoardList", shareMap.get("boardList"));
//        model.addAttribute("sharePagination", shareMap.get("pagination"));

        return "search/result";
    }
}