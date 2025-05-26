package edu.kh.semi.message.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.member.model.dto.Member;
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
	List<Message> selectReceiveMessages(Member member);

	
	
	/** 보낸 메시지함 조회
	 * @param memberNo
	 * @return
	 */
	List<Message> selectSentMessages(Member member);


	
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


	/** 페이지네이션을 위해서는 모든 쪽지수 중 살아있는 것만 불러오기 위해 보낸 쪽지함의 전체 개수를 세는 과정이 필요하다.
	 * @param memberNo
	 * @return
	 */
	int getSentCount(int memberNo);

	
	/** 페이지네이션을 위해서는 모든 쪽지수 중 살아있는 것만 불러오기 위해 받은 쪽지함의 전체 개수를 세는 과정이 필요하다.
	 * @param memberNo
	 * @return
	 */
	int getReceivedCount(int memberNo);


	/** 페이지네이션을 위해 로우바운즈를 이용해 보낸 쪽지함을 받아온다. 
	 * @param memberNo
	 * @param rowBounds
	 * @return
	 */
	List<Message> selectSentMessagesPagination(int memberNo, RowBounds rowBounds);

	
	
	/** 페이지네이션을 위해 로우바운즈를 이용해 받은 쪽지함을 받아온다. 
	 * @param memberNo
	 * @param rowBounds
	 * @return
	 */

	List<Message> selectReceivedMessageListPagination(int memberNo, RowBounds rowBounds);










}