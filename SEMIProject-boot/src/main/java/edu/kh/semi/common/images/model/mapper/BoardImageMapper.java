package edu.kh.semi.common.images.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.BoardImg;

@Mapper
public interface BoardImageMapper {

	void insertImage(BoardImg img);

}
