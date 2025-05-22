package edu.kh.semi.board.model.service;

import java.util.List;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

public interface NoticeBoardService {
    int getNoticeListCount();

    List<Board> selectNoticeList(Pagination pagination);

    Board selectNoticeDetail(Long boardNo);

    int updateReadCount(Long boardNo);
    // 2025-05-22 김동준 수정
	List<Board> searchByKeyword(String query);

	List<Board> selectByMember(int memberNo);
}