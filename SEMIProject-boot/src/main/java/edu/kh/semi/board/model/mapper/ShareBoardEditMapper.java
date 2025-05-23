package edu.kh.semi.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.ShareBoard;

@Mapper
public interface ShareBoardEditMapper {

	int shareInsert(ShareBoard inputBoard);
	
	int shareUpdate(ShareBoard inputBoard);

	int shareDelete(Map<String, Integer> map);

}
