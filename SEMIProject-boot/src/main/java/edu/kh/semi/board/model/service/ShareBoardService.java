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

//    List<Board> searchByKeyword(String query);

	List<Board> selectByMember(int memberNo);

	List<ShareBoard> selectRecent();

	Map<String, Object> searchByKeyword(String query, int sharePage);


	
	/** 검색일 때 대분류일 때
	 * @param paramMap
	 * @param cp
	 * @return
	 */
	Map<String, Object> searchList(Map<String, Object> paramMap, int cp);

	List<Board> selectJjimList(int memberNo);

	
	


}
