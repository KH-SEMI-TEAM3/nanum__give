package edu.kh.semi.share.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.semi.share.model.dto.ShareBoard;
import edu.kh.semi.share.model.service.ShareBoardService;



@Controller
@RequestMapping("/share")
public class ShareBoardController {

    @Autowired
    private ShareBoardService service;

    @GetMapping("list")
    public String list(Model model){
        List<ShareBoard> list = service.getShareBoardList();
        model.addAttribute("list", list);
        return "/board/share/shareList";
    }

}