package edu.kh.semi.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.ShareBoard;

@Mapper
public interface ShareEditMapper {

	int ShareInsert(ShareBoard inputBoard);
	
	int shareDelete(Map<String, Integer> map);

}
