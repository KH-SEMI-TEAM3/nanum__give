// NoticeController.java
package edu.kh.semi.board.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.service.NoticeBoardService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/notice")
public class NoticeBoardController {

    @Autowired
    private NoticeBoardService service;
    
     

    /*목록조회*/
    @GetMapping("list")
    public String noticeList(@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
                              Model model,
                              @RequestParam Map<String, Object> paramMap) {
    	   int boardCode = 3;

    	    Map<String, Object> map = null;

    	    // 검색이 아닌 경우 
    	  if(paramMap.get("key") == null) {

    	
    	       map = service.selectNoticeList(boardCode, cp); // 검색 조건 포함 리스트

    	       // 검색하는 경우 
    	    } else {
    	        
    	 
    	        paramMap.put("boardCode", boardCode);
    	        
    	        map = service.noticeSearchList(paramMap, cp);
    	    }

    	    model.addAttribute("noticeBoardList", map.get("boardList"));
    	    model.addAttribute("pagination", map.get("pagination"));

    	    return "board/notice/noticeboard-list";
    	}
    

    /*상세조회*/
    @GetMapping("/{boardNo}")
    public String noticeDetail(@PathVariable("boardNo") Long boardNo,
                                Model model,
                                HttpServletRequest req,
                                HttpServletResponse resp) {

        Board board = service.selectNoticeDetail(boardNo);
    	//Board board = service.selectOne(boardNo);

        
        if (board == null) {
            return "redirect:/notice";
        }

        Cookie[] cookies = req.getCookies();
        boolean isRead = false;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("readBoardNo") && c.getValue().contains("[" + boardNo + "]")) {
                    isRead = true;
                    break;
                }
            }
        }

        if (!isRead) {
            service.updateReadCount(boardNo);

            Cookie cookie = new Cookie("readBoardNo", "[" + boardNo + "]");
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            resp.addCookie(cookie);

            board.setReadCount(board.getReadCount() + 1);
        }

        model.addAttribute("board", board);
   
        
        
        return "board/notice/noticeboard-detail";
    }
}
