package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

@Mapper
public interface FreeBoardMapper {
	List<Board> selectFreeList(Pagination pagination);
	int selectFreeListCount();
	int insertFreeBoard(Board board);
	Board selectFreeBoard(Long boardNo);
	int updateFreeBoard(Board board);
	// 검색 기능 추가 김동준 2025-05-22
    List<Board> searchByKeyword(@Param("query") String query);
	List<Board> selectByMember(int memberNo);
	int deleteBoard(Long boardNo);

}
