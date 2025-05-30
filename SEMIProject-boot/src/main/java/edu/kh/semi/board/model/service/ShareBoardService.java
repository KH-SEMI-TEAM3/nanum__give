package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.dto.ShareBoard;

public interface ShareBoardService {
    
	Map<String, Object> selectBoardList(int boardCode, int cp);

	ShareBoard selectOne(Map<String, Integer> map);

	int updateReadCount(int boardNo);

	int boardJJim(Map<String, Integer> map);
	
	int shareStatus(Map<String, Object> map);

	List<ShareBoard> selectRecent();

	List<Board> selectJjimList(int memberNo);

	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

}
