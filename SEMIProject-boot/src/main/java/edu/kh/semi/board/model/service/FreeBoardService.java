package edu.kh.semi.board.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;



public interface FreeBoardService {
    
	List<Board> getList(Pagination pagination);
	int getListCount();
	int insertFreeBoard(Board board);
	Board getFreeBoard(Long boardNo);	
	void updateReadCount(Long boardNo);
	int updateBoard(Board board, MultipartFile boardImage);
  
	// 김동준 검색기능 추가
	List<Board> searchByKeyword(String query);
	List<Board> selectByMember(int memberNo);
	int deleteBoard(Long boardNo);
	
}
