package edu.kh.semi.QNABoard.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.QNABoard.model.dto.QNABoard;
import lombok.extern.slf4j.Slf4j;
@Mapper
public interface QNABoardMapper {
	

	
	/** 게시판 종류에 따라 게시글 수를 조회
	 * @param boardCode
	 * @return
	 */
	
	public int getQNAListCount(int boardCode);

	
	
	/**
	 * 보드의 타입만 알아내는 로직으로 페이지네이 전용으로 사용된다.
	 */
	
	public List<Map<String, Object>> selectBoardTypeList();
	
	
	
	/** 특정 게시판의 지정된 페이지의 목록을 조회한다
	 * @param boardCode
	 * @param rowBounds
	 * @return
	 */
	public List<QNABoard> selectQNABoardList(int boardCode, RowBounds rowBounds);




	
	
	
	


	
	


}
