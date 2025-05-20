package edu.kh.semi.board.model.service;

import java.util.List;

import edu.kh.semi.board.model.dto.Board;

public interface NoticeBoardService {

    List<Board> getNoticeList();

    Board getNoticeBoard(Long boardNo);

    int createNoticeBoard(Board board);

    int modifyNoticeBoard(Board board);

    int removeNoticeBoard(Long boardNo);

    // 조회수 증가 (상세조회 시 사용)
    int updateReadCount(Long boardNo);
}
