package edu.kh.semi.board.model.service;

import java.util.List;


import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;



public interface FreeBoardService {
    
	List<Board> getList(Pagination pagination);
	int getListCount();
	int insertFreeBoard(Board board);
	Board getFreeBoard(Long boardNo);
	
	int updateBoard(Board board);
}
