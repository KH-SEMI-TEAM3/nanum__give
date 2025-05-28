package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

public interface NoticeBoardService {
    int getNoticeListCount();

    List<Board> selectNoticeList(Pagination pagination);

    Board selectNoticeDetail(Long boardNo);

    int updateReadCount(Long boardNo);
    // 2025-05-22 김동준 수정
	Map<String, Object> searchByKeyword(String query, int noticePage);

	List<Board> selectByMember(int memberNo);

	/**
	 * 검색 
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);
}