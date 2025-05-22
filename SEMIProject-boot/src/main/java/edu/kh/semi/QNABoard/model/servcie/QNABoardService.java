package edu.kh.semi.QNABoard.model.servcie;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Board;

public interface QNABoardService {
	
	/** 게시판 종류 조회 서비스
	 * @return
	 */
	List<Map<String, Object>> selectBoardTypeList();
	

	/** 검색이 아닌 그냥 게시판일 때 QNA게시판에서의 조회
	 * @param boardCode
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectQNABoardList(int boardCode, int cp);

	
	
	/** QNA게시판에 있는 글이지만 검색일 떄의 조회
	 * @param paraMap
	 * @param cp
	 * @return
	 */
	Map<String, Object> searchQNAList(Map<String, Object> paraMap, int cp);

	// 김동준 검색기능 추가 2025-05-22
	List<Board> searchByKeyword(String query);


	List<Board> selectByMember(int memberNo);
	
	 

}
