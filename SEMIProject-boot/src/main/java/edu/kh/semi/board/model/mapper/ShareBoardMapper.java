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

	int selectBoardJJim(Integer integer);
	
	List<Board> selectJjimList(int memberNo);

	List<ShareBoard> selectRecent(Integer memberNo);

    int updateShareStatus(Map<String, Object> map);

    int getSearchOnlyCount(Map<String, Object> paramMap);
    List<ShareBoard> selectSearchOnlyList(Map<String, Object> paramMap, RowBounds rowBounds);

    int getCategoryOnlyCount(Map<String, Object> paramMap);
    List<ShareBoard> selectCategoryOnlyList(Map<String, Object> paramMap, RowBounds rowBounds);

    int getSearchAndCategoryCount(Map<String, Object> paramMap);
    List<ShareBoard> selectSearchAndCategoryList(Map<String, Object> paramMap, RowBounds rowBounds);

	

}
