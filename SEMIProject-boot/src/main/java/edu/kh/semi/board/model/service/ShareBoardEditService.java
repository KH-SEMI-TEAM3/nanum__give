package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.ShareBoard;

public interface ShareBoardEditService {

	int boardInsert(ShareBoard inputBoard);

	int boardUpdate(ShareBoard inputBoard);

	int boardDelete(Map<String, Integer> map);

}
