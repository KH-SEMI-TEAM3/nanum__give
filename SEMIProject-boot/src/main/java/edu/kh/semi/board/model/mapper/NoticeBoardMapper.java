package edu.kh.semi.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.semi.board.model.dto.Board;

@Mapper
public interface NoticeBoardMapper {
 // 공지게시판 목록 페이지용 ------------- 
	/**
	 *  게시글 수 조회 
	 * @param boardCode
	 * @return
	 */
    int getNoticeListCount(int boardCode);

    /** 공지 게시판의 지정된 페이지 목록 조회 
     *  목록 검색 
     * @param rowBounds
     * @return
     */
    List<Board> selectNoticeList(int boardCode, RowBounds rowBounds);
    
    // 공지게시판 내 검색 목록 --------------------
    
    /** 검새 결과 목록 조회 
     * 
     * @param paramMap
     * @param rowBounds
     * @return
     */
	List<Board> selectNoticeSearchList(Map<String, Object> paramMap, RowBounds rowBounds);
	
	/** 
	 * 검색 조건이 맞는 게시글 개수 조회 
	 * @param paramMap
	 * @return
	 */
	int getnoticeSearchCount(Map<String, Object> paramMap);
	


	// 공지게시판 상세  ------------------------------------------------


	

	/**
	 * 상세조회 
	 * @param boardNo
	 * @return
	 */
    Board selectNoticeDetail(Long boardNo);

    int updateReadCount(Long boardNo);
    
	/**
	 *  추가 
	 * @param map
	 * @return
	 */
	Board selectOne(Map<String, Integer> map);


	// 종합 -----------------------------------------
	
	//    List<Board> searchByKeyword(@Param("query") String query);

	List<Board> selectByMember(int memberNo);
	
	/**검색 조건이 맞는 게시글 수 조회 (전체용)
	 * 
	 * @param paramMap
	 * @return
	 */

	int getSearchCount(String query);

	List<Board> searchByKeyword(String query, RowBounds rowBounds);



	
	
}
