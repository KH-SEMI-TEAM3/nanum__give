package edu.kh.semi.search.controller;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.search.model.service.SearchService;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private SearchService service;

    @GetMapping("/result")
    public String searchAll(
        @RequestParam("headerSearchType") String searchType,
        @RequestParam("query") String query,

        @RequestParam(value = "freePage", required = false) Integer freePage,
        @RequestParam(value = "noticePage", required = false) Integer noticePage,
        @RequestParam(value = "sharePage", required = false) Integer sharePage,
        @RequestParam(value = "qnaPage", required = false) Integer qnaPage,

        Model model) {


        int limit = 10;
        int freeCp = (freePage != null) ? freePage : 1;
        int noticeCp = (noticePage != null) ? noticePage : 1;
        int shareCp = (sharePage != null) ? sharePage : 1;
        int qnaCp = (qnaPage != null) ? qnaPage : 1;

        model.addAttribute("freeBoardList", service.searchFreeBoard(searchType, query, freeCp, limit).get("boardList"));
        model.addAttribute("freePagination", service.searchFreeBoard(searchType, query, freeCp, limit).get("pagination"));

        model.addAttribute("noticeBoardList", service.searchNoticeBoard(searchType, query, noticeCp, limit).get("boardList"));
        model.addAttribute("noticePagination", service.searchNoticeBoard(searchType, query, noticeCp, limit).get("pagination"));

        model.addAttribute("shareBoardList", service.searchShareBoard(searchType, query, shareCp, limit).get("boardList"));
        model.addAttribute("sharePagination", service.searchShareBoard(searchType, query, shareCp, limit).get("pagination"));

        model.addAttribute("qnaBoardList", service.searchQNABoard(searchType, query, qnaCp, limit).get("boardList"));
        model.addAttribute("qnaPagination", service.searchQNABoard(searchType, query, qnaCp, limit).get("pagination"));

        model.addAttribute("query", query);
        model.addAttribute("searchType", searchType);

        return "search/result";
    }
}