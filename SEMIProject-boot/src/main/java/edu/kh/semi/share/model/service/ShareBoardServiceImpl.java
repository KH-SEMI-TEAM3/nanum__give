package edu.kh.semi.share.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.semi.share.model.dto.ShareBoard;
import edu.kh.semi.share.model.mapper.ShareBoardMapper;

@Service
public class ShareBoardServiceImpl implements ShareBoardService {

    @Autowired
    private ShareBoardMapper mapper;

	@Override
	public List<ShareBoard> getShareBoardList() {
		// TODO Auto-generated method stub
		return null;
	}


}