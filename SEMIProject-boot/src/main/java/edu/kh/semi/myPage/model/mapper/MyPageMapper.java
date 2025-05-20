package edu.kh.semi.myPage.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	int updateInfo(Member inputMember);
	
	/** 회원의 비밀번호 조회
	 * @param memberNo
	 * @return
	 */
	String selectPw(int memberNo);

	/** 회원의 비밀번호 변경
	 * @param paramMap
	 * @return
	 */
	int changePw(Map<String, String> paramMap);

	/** 회원 탈퇴
	 * @param memberNo
	 * @return
	 */
	int secession(int memberNo);
 
	/** 파일 정보를 DB에 삽입 
	 * @param uf
	 * @return
	 */

//	List<UploadFile> fileList(int memberNo);

	/** 프로필 이미지 변경
	 * @param member
	 * @return
	 */
	int profile(Member member);

}
