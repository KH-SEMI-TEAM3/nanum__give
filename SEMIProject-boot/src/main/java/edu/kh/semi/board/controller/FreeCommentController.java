package edu.kh.semi.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.semi.board.model.dto.Comment;
import edu.kh.semi.board.model.service.NoticeCommentService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/freeComment")
public class FreeCommentController {

	@Autowired
	private NoticeCommentService commentService;
	
	/** 특정 게시글의 댓글 목록을 조회하는 메서드
	 * @param boardNo
	 * @return
	 */
	@GetMapping("")
	public List<Comment> select(@RequestParam("boardNo") int boardNo) {
		return commentService.select(boardNo);
	}
	
	/** 새로운 댓글 또는 답글을 등록하는 메서드
	 * @param comment
	 * @param session
	 * @return
	 */
	@PostMapping("/insert")
    public int insert(@RequestBody Comment comment, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        comment.setMemberNo(loginMember.getMemberNo());
        return commentService.insert(comment);
    }

    /** 댓글 삭제 요청을 처리하는 메서드
     * @param commentNo
     * @return
     */
    @DeleteMapping("")
    public int delete(@RequestBody int commentNo) {
        return commentService.delete(commentNo);
    }

    /** 댓글 수정 요청을 처리하는 메서드
     * @param comment
     * @return
     */
    @PutMapping("")
    public int update(@RequestBody Comment comment) {
        return commentService.update(comment);
    }
	
}
