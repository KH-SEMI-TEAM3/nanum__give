package edu.kh.semi.board.model.service;

import java.util.List;


import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;



public interface FreeBoardService {
    
	List<Board> getList(Pagination pagination);
	int getListCount();
//    Board getFreeBoard(Long boardNo);
//    int createFreeBoard(Board board);
//    int modifyFreeBoard(Board board);
//    int removeFreeBoard(Long boardNo);
}
