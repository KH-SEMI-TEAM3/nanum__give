package edu.kh.semi.message.model.service;

import java.util.List;
import java.util.Map;


import edu.kh.semi.message.model.dto.Message;


public interface MessageService {

	
	
	/** 메시지 보내기
	 * @param message
	 * @return
	 */
	int sendMessage(Message message);

	
	/** 메시지 삭제
	 * @param messages
	 * @return
	 */
	int deleteMessagePage(Message messages);

	
	/** 메시지 상세내용 조회 (보내는거든 받는거든 똑같이 함)
	 * @param paramMap
	 * @return
	 */
	Message getMessageDetail(Map<String, Object> paramMap);

	/** 보낸 메시지 모음
	 * @param memberNo
	 * @return
	 */
	List<Message> selectSentMessages(int memberNo);
	
	/** 받은 메시지 모음
	 * @param memberNo
	 * @return
	 */
	List<Message> selectReceiveMessages(int memberNo);




}
