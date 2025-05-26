package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Comment;
import edu.kh.semi.board.model.dto.Pagination;



public interface FreeBoardService {
    
	List<Board> getList(Pagination pagination);
	int getListCount();

	Board getFreeBoard(int boardNo);	
	void updateReadCount(int boardNo);
	int updateBoard(Board board, MultipartFile boardImage);
  
	// 김동준 검색기능 추가
	Map<String, Object> searchByKeyword(String query, int cp);
	List<Board> selectByMember(int memberNo);
	int deleteBoard(int boardNo);
	List<Comment> getCommentList(int boardNo);
	
	int insertFreeBoard(Board board); // 
	
}
