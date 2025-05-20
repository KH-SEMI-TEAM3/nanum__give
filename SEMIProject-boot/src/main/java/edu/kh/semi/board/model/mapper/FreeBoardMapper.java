package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;

@Mapper
public interface FreeBoardMapper {
	List<Board> selectFreeList(Pagination pagination);
	int selectFreeListCount();

    Board selectFree(Long boardNo);
    int insertFree(Board board);
    int updateFree(Board board);
    int deleteFree(Long boardNo);
}
