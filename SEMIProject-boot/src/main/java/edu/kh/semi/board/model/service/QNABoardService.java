package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.QNABoard;

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
	Map<String, Object> searchByKeyword(String query, int cp);

	List<Board> selectByMember(int memberNo);

	/** 보드 상세 내용 하나 전체를 삼중 select하는 구문  (여기서는 이중으로 할 것)
	 * @param map
	 * @return
	 */
	QNABoard selectOne(Map<String, Integer> map);


	/** 쿠키기반 조회수 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);


	
	/** 게시판 추가하기
	 * @param inputBoard
	 * @param images
	 * @return
	 */
	int boardInsert(QNABoard inputBoard, List<MultipartFile> images);


	/** 게시판 없애기
	 * @param map
	 * @return
	 */
	int boardDelete(Map<String, Integer> map);


	
	/** 게시판 수정 행위
	 * @param qnaBoard
	 * @return
	 */
	int boardUpdate(QNABoard qnaBoard);


	
	 
	
	
	
	

}
