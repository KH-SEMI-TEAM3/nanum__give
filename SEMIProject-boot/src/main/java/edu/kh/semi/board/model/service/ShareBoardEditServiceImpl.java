package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.board.model.mapper.ShareEditMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class ShareBoardEditServiceImpl implements ShareBoardEditService {
	@Autowired
	private ShareEditMapper mapper;

	@Override
	public int boardInsert(ShareBoard inputBoard) {
		int result = mapper.ShareInsert(inputBoard);
		if (result == 0) return 0;
		return inputBoard.getBoardNo();
	}

	@Override
	public int boardUpdate(ShareBoard inputBoard, List<MultipartFile> images, String deleteOrderList) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int boardDelete(Map<String, Integer> map) {
		return mapper.shareDelete(map);
	}
	
}