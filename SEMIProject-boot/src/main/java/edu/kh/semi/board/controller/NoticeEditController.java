package edu.kh.semi.board.controller;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.Board;

import edu.kh.semi.board.model.service.NoticeBoardService;
import edu.kh.semi.board.model.service.NoticeEditService;
import edu.kh.semi.member.model.dto.Member;



@Controller
@RequestMapping("noticeEdit")
public class NoticeEditController {


@Value("${my.board.folder-path}")
private String folderPath;

@Value("${my.board.web-path}")
private String webPath;

    @Autowired
    private NoticeEditService service;

    @Autowired
    private NoticeBoardService boardService;

    /*게시글 작성*/
    @GetMapping("insert")
    public String boardInsert() {
        return "board/notice/noticeboard-write";
    }

    @PostMapping("insert")
    public String boardInsert(
                              @ModelAttribute Board inputBoard,
                              @SessionAttribute("loginMember") Member loginMember,
                              @RequestParam(value = "images", required = false) List<MultipartFile> images,

                              RedirectAttributes ra) throws Exception {

    	 inputBoard.setBoardCode(3); // 게시판 코드 고정 세팅
        inputBoard.setMemberNo(loginMember.getMemberNo());


        if (images == null) images = List.of(); 

        int boardNo = service.boardInsert(inputBoard, images); 

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
    @GetMapping("{boardNo:[0-9]+}/update")
    public String boardUpdate(
                              @PathVariable("boardNo") int boardNo,
                              @SessionAttribute("loginMember") Member loginMember,
     
                              Model model,
                              RedirectAttributes ra) {

    	int boardCode = 3 ; 
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
            path = String.format("redirect:/notice/%d", boardNo);
            ra.addFlashAttribute("message", message);

        } else {
            path = "board/notice/noticeboard-update";
            model.addAttribute("board", board);
        }

        return path;
        
        
        
    }

    @PostMapping("{boardNo:[0-9]+}/update")
    public String boardUpdate(
        @PathVariable("boardNo") int boardNo,
        Board inputBoard,
        @SessionAttribute("loginMember") Member loginMember,

        @RequestParam(value = "images", required = false) List<MultipartFile> images,
        RedirectAttributes ra,
        @RequestParam(value = "cp", required = false, defaultValue = "1") int cp
    ) throws Exception {

        if (images == null) images = List.of(); // null 방지

        inputBoard.setBoardCode(3);
        inputBoard.setBoardNo(boardNo);
        inputBoard.setMemberNo(loginMember.getMemberNo());

        int result = service.boardUpdate(inputBoard, images);

        String path = (result > 0) ? String.format("/notice/%d?cp=%d", boardNo, cp) : "update";
        String message = (result > 0) ? "게시글이 수정되었습니다." : "게시글 수정 실패";

        ra.addFlashAttribute("message", message);
        return "redirect:" + path;
    }
    
    /* 게시글 삭제 */
    @RequestMapping(value = "{boardNo:[0-9]+}/delete",
                    method = { RequestMethod.GET, RequestMethod.POST })
    public String boardDelete(
                              @PathVariable("boardNo") int boardNo,
                              @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
                              RedirectAttributes ra,
                              @SessionAttribute("loginMember") Member loginMember) {

        int boardCode = 3;
    	Map<String, Integer> map = new HashMap<>();
        
        map.put("boardCode", boardCode);
        map.put("boardNo", boardNo);
        map.put("memberNo", loginMember.getMemberNo());

        int result = service.boardDelete(map);

        String path = null;
        String message = null;

        if (result > 0) {
            message = "삭제 되었습니다";
            path = String.format("/notice/list?cp=%d", cp);
        } else {
            message = "삭제 실패";
            path = String.format("/notice/%d?cp=%d",  boardNo, cp);
        }

        ra.addFlashAttribute("message", message);
        return "redirect:" + path;
    }
    
    @PostMapping("/UploadImage")
    @ResponseBody
    public String uploadImage(@RequestParam("image") MultipartFile file) throws IOException {

        String originalName = file.getOriginalFilename();
        String rename = UUID.randomUUID().toString() + "_" + originalName;

        File directory = new File(folderPath);
        if (!directory.exists()) directory.mkdirs();

        file.transferTo(new File(folderPath + "/" + rename));

        return webPath + rename;
    }

    
}
