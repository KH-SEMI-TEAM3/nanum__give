package edu.kh.semi.board.model.service;

import java.util.List;

import edu.kh.semi.board.model.dto.Comment;

public interface FreeCommentService {

	List<Comment> select(int boardNo);

	int insert(Comment comment);

	int delete(int commentNo);

	int update(Comment comment);

}
