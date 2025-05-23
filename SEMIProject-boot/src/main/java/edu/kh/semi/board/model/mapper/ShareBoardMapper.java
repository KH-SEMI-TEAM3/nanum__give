package edu.kh.semi.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.ShareBoard;

@Mapper
public interface ShareBoardMapper {

	int getListCount(int boardCode);

	List<ShareBoard> selectBoardList(int boardCode, RowBounds rowBounds);

	ShareBoard selectOne(Map<String, Integer> map);

	int updateReadCount(int boardNo);

	int selectReadCount(int boardNo);

	int deleteBoardJJim(Map<String, Integer> map);

	int insertBoardJJim(Map<String, Integer> map);

	int selectJJimCount(Integer integer);

    List<Board> searchByKeyword(@Param("query") String query);

	List<Board> selectByMember(int memberNo);

	List<ShareBoard> selectRecent();
    

}
