package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

@Mapper
public interface FreeBoardMapper {
	List<Board> selectFreeList(Pagination pagination);
	int selectFreeListCount();
	int insertFreeBoard(Board board);
	Board selectFreeBoard(Long boardNo);
	int updateFreeBoard(Board board);

}
