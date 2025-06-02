package edu.kh.semi.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.QNABoard;
import lombok.extern.slf4j.Slf4j;

@Mapper
public interface QNABoardMapper {

	/**
	 * 게시판 종류에 따라 게시글 수를 조회
	 * 
	 * @param boardCode
	 * @return
	 */

	public int getQNAListCount(int boardCode);

	/**
	 * 보드의 타입만 알아내는 로직으로 페이지네이 전용으로 사용된다.
	 */

	public List<Map<String, Object>> selectBoardTypeList();

	/**
	 * 특정 게시판의 지정된 페이지의 목록을 조회한다
	 * 
	 * @param boardCode
	 * @param rowBounds
	 * @return
	 */
	public List<QNABoard> selectQNABoardList(int boardCode, RowBounds rowBounds);

	/**
	 * 검색 기능
	 * 
	 * @param query
	 * @return
	 */
//	public List<Board> searchByKeyword(String query);

	public List<Board> selectByMember(int memberNo);

	/**
	 * 보드의 한 페이지를 불러오기 위한 삼중 resultMap
	 * 
	 * @param map
	 * @return
	 */
	public QNABoard selectOne(Map<String, Integer> map);

	/**
	 * 조회수 1 증가시키는 업데이트문을 호출
	 * 
	 * @param boardNo
	 * @return
	 */
	public int updateReadCount(int boardNo);

	/**
	 * 증가시킨 후에 1 증가된 조회수 자체를 조회
	 * 
	 * @param boardNo
	 * @return
	 */
	public int selectReadCount(int boardNo);

	/**
	 * 보드 삽입 로직
	 * 
	 * @param inputBoard
	 * @return
	 */
	public int boardInsert(QNABoard inputBoard);

	/**
	 * 게시글 삭제
	 * 
	 * @param map
	 * @return
	 */
	public int boardDelete(Map<String, Integer> map);

	public int boardUpdate(QNABoard qnaBoard);

	public int getSearchCount(String query);

	public List<Board> searchByKeyword(String query, RowBounds rowBounds);

	/**
	 * 문의 완료 여부 바꾸기
	 * 
	 * @param paramMap
	 * @return
	 */
	public int updateCompletion(Map<String, Object> paramMap);

	
	/** 검색 시 총 개수
	 * @param paraMap
	 * @return
	 */
	public int getSearchCount(Map<String, Object> paraMap);

	
	/** 진짜 게시판 내 검색
	 * @param paraMap
	 * @param rowBounds
	 * @return
	 */
	public List<Board> selectSearchList(Map<String, Object> paraMap, RowBounds rowBounds);

	
	
	
	
	
	
	
	/** 문의중이거나 문의완료인것만 검색
	 * @param paraMap
	 * @param rowBounds
	 * @return
	 */
	public List<Board> qaSearch(Map<String, Object> paraMap, RowBounds rowBounds);

	
	/** 새로운 QNA 검색용 전체 개수 세기
	 * @param paraMap
	 * @return
	 */
	public int getSearchCountOfQna(Map<String, Object> paraMap);

}