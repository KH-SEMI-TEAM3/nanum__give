package edu.kh.semi.admin.model.service;

public interface AdminService {

	/** 관리자의 권한으로 해당 memberNo와 일치하는 것을 회원탈퇴시킨다
	 * @param memberNo
	 * @return
	 */
	int memberDelete(int memberNo);

	

}
