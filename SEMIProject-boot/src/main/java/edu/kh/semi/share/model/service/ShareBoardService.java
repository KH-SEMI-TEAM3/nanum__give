package edu.kh.semi.share.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.share.model.dto.ShareBoard;

public interface ShareBoardService {
    
    /** 나눔 게시글 목록 조회
     * @param paramMap
     * @return boardList, pagination
     */
	Map<String, Object> selectBoardList(int boardCode, int cp);

	ShareBoard selectOne(Map<String, Integer> map);

	int updateReadCount(int boardNo);

	int boardJJim(Map<String, Integer> map);

}
