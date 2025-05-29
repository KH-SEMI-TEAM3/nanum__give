package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

public interface NoticeBoardService {
    int getNoticeListCount();

   // List<Board> selectNoticeList(Pagination pagination);
    
	/** 특정 게시판의 지정된 페이지 목록 
	 * 
	 * @param paramMap
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectNoticeList(int boardCode, int cp);


	/** 검색 서비스 
	 * @param paramMAp
	 * @param cp
	 * @return
	 */
	Map<String, Object> noticeSearchList(Map<String, Object> paramMAp, int cp);
	
	
//	// 25-05-29 검색인 경우 service 추가 
//	int getSearchListCount(Map<String, Object> paramMap);
//
//	List<Board> selectNoticeSearchList(Map<String, Object> paramMap, int cp);
//	
//	
	
	// 상세조회 Service
    Board selectNoticeDetail(Long boardNo);

	Board selectOne(Map<String, Integer> map);

	// 조회수 증가 Service 
    int updateReadCount(Long boardNo);
	
  
	
	
    // 2025-05-22 김동준 수정
	Map<String, Object> searchByKeyword(String query, int noticePage);

	List<Board> selectByMember(int memberNo);


	

	

}