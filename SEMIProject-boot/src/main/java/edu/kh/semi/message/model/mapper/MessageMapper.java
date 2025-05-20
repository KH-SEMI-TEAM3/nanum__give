package edu.kh.semi.message.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.message.model.dto.Message;

@Mapper
public interface MessageMapper {

	/** 메시지 보내는 과정
	 * @param message
	 * @return
	 */
	int insertMessage(Message message);

	
	
	/** 받은 메시지함 조회
	 * @param memberNo
	 * @return
	 */
	List<Message> selectReceiveMessages(int memberNo);

	
	
	/** 보낸 메시지함 조회
	 * @param memberNo
	 * @return
	 */
	List<Message> selectSentMessages(int memberNo);


	
	/** 업데이트 여부 확인
	 * @param paramMap
	 * @return
	 */
	int updateReadFlag(Map<String, Object> paramMap);

	
	
	/** 받은, 보낸 쪽지에 대한 상세 사항 확인
	 * @param paramMap
	 * @return
	 */
	Message getMessageDetail(Map<String, Object> paramMap);

	
	/** 메시지 삭제
	 * @param messages
	 * @return
	 */
	int deleteMessagePage(Message messages);




}
