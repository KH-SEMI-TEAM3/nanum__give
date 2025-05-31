package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

@Mapper
public interface FreeBoardMapper {
	List<Board> selectFreeList(Pagination pagination);
	int selectFreeListCount();
	int insertFreeBoard(Board board);
	Board selectFreeBoard(int boardNo);
	int updateReadCount(int boardNo);
	int updateFreeBoard(Board board);
	// 검색 기능 추가 김동준 2025-05-22
//    List<Board> searchByKeyword(@Param("query") String query);
	List<Board> selectByMember(int memberNo);
	int deleteBoard(int boardNo);
	void deleteBoardImage(int i);
	void insertBoardImage(BoardImg image);
	int insertBoardImage(@Param("boardNo") int boardNo, @Param("imgPath") String imgPath);
	// 검색기능에 페이지네이션 추가 김동준 2025-05-26
	List<Board> searchByKeyword(String query, RowBounds rowBounds);
	int countSearch(String query);
	int countSearchByKey( @Param("key") String key, @Param("query") String query);
	List<Board> searchByKeyAndQuery(@Param("key") String key, @Param("query") String query, RowBounds rowBounds);
	
	/** 삭제된 회원인지 추가
	 * @param memberNo
	 * @return
	 */
	String selectMemberDelFl(int memberNo);
	

}
