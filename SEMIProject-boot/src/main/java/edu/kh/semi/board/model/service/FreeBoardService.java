package edu.kh.semi.board.model.service;

import java.util.List;


import edu.kh.semi.board.model.dto.Board;



public interface FreeBoardService {
    List<Board> getFreeBoardList();
    Board getFreeBoard(Long boardNo);
    int createFreeBoard(Board board);
    int modifyFreeBoard(Board board);
    int removeFreeBoard(Long boardNo);
}
