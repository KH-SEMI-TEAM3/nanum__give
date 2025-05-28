
package edu.kh.semi.message.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.message.model.dto.Message;


public interface MessageService {

	
	
	/** 메시지 보내기
	 * @param message
	 * @return
	 */
	int sendMessage(Message message);

	
	/** 받은 메시지 삭제
	 * @param messages
	 * @return
	 */
	int deleteMessagePageIn(Message messages);

	
	
	
	/** 보낸 메시지 삭제
	 * @param messages
	 * @return
	 */
	int deleteMessagePageOut(Message messages);
	
	
	/** 메시지 상세내용 조회 (보내는거든 받는거든 똑같이 함)
	 * @param paramMap
	 * @return
	 */
	Message getMessageDetail(Map<String, Object> paramMap);

	/** 보낸 메시지 모음
	 * @param memberNo
	 * @return
	 */
	List<Message> selectSentMessages(Member member);
	
	/** 받은 메시지 모음
	 * @param loginMember
	 * @return
	 */
	List<Message> selectReceiveMessages(Member loginMember);


	/** 총 메시지 개수
	 * @param memberNo
	 * @return
	 */
	int getSentCount(int memberNo);


	/** 보낸 메시지  페이지네이션을 위한 총 적용
	 * @param memberNo
	 * @param pagination
	 * @return
	 */
	Map<String, Object> selectSentMessageListPagination(int memberNo, int cp);


	/** 받은 메시지 페이지네이션을 위한 총 적용
	 * @param memberNo
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectReceivedMessageListPagination(int memberNo, int cp);


	/** 아직 안 읽은 메시지의 개수
	 * @param loginMember 
	 * @return
	 */
	int selectUnread(Member loginMember);






}
