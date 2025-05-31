package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Comment;

@Mapper
public interface FreeCommentMapper {

	List<Comment> select(int boardNo);

	int insert(Comment comment);

	int delete(int commentNo);

	int update(Comment comment);

	
}
