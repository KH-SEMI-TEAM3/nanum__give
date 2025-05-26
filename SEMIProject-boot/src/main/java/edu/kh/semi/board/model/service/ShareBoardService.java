package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.dto.ShareBoard;

public interface ShareBoardService {
    
    /** 나눔 게시글 목록 조회
     * @param paramMap
     * @return boardList, pagination
     */
	Map<String, Object> selectBoardList(int boardCode, int cp);

	ShareBoard selectOne(Map<String, Integer> map);

	int updateReadCount(int boardNo);

	int boardJJim(Map<String, Integer> map);

//    List<Board> searchByKeyword(String query);

	List<Board> selectByMember(int memberNo);

	List<ShareBoard> selectRecent();

	Map<String, Object> searchByKeyword(String query, int sharePage);
	
	


}
