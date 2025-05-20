package edu.kh.semi.share.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.share.model.dto.ShareBoard;

@Mapper
public interface ShareBoardMapper {
    List<ShareBoard> selectShareBoardList();
}
