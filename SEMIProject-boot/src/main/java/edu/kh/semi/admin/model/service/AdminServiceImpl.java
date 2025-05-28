package edu.kh.semi.admin.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.semi.admin.model.mapper.AdminMapper;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private AdminMapper adminMapper;
	
	/**
	 * 관리자의 권한으로 memberNo과 일치하는게 있으면 억지로 삭제시킨다.
	 */
	@Override
	public int memberDelete(int memberNo) {
		// TODO Auto-generated method stub
		log.debug("서비스임플까지 도달함"); 
		 
		return adminMapper.memberDelete(memberNo);
	}

}
