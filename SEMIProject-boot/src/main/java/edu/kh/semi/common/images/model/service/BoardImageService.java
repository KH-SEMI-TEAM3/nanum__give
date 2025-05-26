package edu.kh.semi.common.images.model.service;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;
import jakarta.servlet.http.HttpSession;

public interface BoardImageService {

	void save(BoardImg img);

	void saveBoardImage(Board board, MultipartFile image, String webPath, HttpSession session);

}
