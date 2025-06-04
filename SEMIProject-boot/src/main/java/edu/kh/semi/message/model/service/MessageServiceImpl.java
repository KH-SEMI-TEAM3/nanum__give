// edu/kh/project/message/model/service/MessageServiceImpl.java
package edu.kh.semi.message.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.mapper.MemberMapper;
import edu.kh.semi.message.model.dto.Message;
import edu.kh.semi.message.model.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private MemberMapper memberMapper;

    
    
    /**
     * 메시지 보내서 넣기
     */
    @Override
    public int sendMessage(Message message) {
        log.info("sendMessage Service 실행   - 보낼 메시지: {}", message);
        int result = messageMapper.insertMessage(message);
        log.info("sendMessage 결과: {}", result);
        return result;
    }

     
     
    
    /**
     * 받은 메시지함으로 이동
     */
    @Override
    public List<Message> selectReceiveMessages(Member member) {
        log.info("getInboxMessages Service 실행 - 수신 회원 번호: {}", member.getMemberNo());
        List<Message> list = messageMapper.selectReceiveMessages(member);
        log.info("getInboxMessages 조회된 메시지 개수!: {}", list.size());
        return list;
    }

    
    /**
     * 보낸 메시지함으로 이동 
     */
    @Override
    public List<Message> selectSentMessages(Member member) {
    	return messageMapper.selectSentMessages(member);
    }
    
    
    /**
     *  메시지 상세 조회 (보내는거든 받는거든 똑같이 함)
     */
    @Override
    public Message getMessageDetail(Map<String, Object> paramMap) {
        log.info("getMessageDetail Service 실행 - 파라미터: {}", paramMap);
        int updateCount = messageMapper.updateReadFlag(paramMap);
        log.info("updateReadFlag 적용 건수: {}", updateCount);
        Message detail = messageMapper.getMessageDetail(paramMap);
        log.info("selectMessageDetail 조회된 메시지 : {}", detail);
        return detail;
    }

  

  

    /**
     * 받은 메시지 삭제 
     */
    @Override
    public int deleteMessagePageIn(Message messages) {
        log.info("deleteMessagePage Service 실행 - 삭제할 메시지 정보: {}", messages);
        int result = messageMapper.deleteMessagePageIn(messages);
        log.info("deleteMessagePage 결과: {}", result);
        return result;
    }



    
    /**
     * 보낸 메시지 삭제
     */
    @Override
	public int deleteMessagePageOut(Message messages) {
        int result = messageMapper.deleteMessagePageOut(messages);
        
		return result;
	}
    

	/**
	 * 총 메시지 개수만 얻어와서 나누려는 대상으로 삼으려고
	 */
	@Override
	public int getSentCount(int memberNo) {
		// TODO Auto-generated method stub
		int result =  messageMapper.getSentCount(memberNo);
        log.info("총 메시지의 개수: {} 개", result);

		return result;
	}




	/**
	 * 페이지네이션으로 제대로 리스트들을 얻어오기 위한 계산 과정
	 */
	@Override
	public Map<String, Object> selectSentMessageListPagination(int memberNo, int cp) {
	    log.info("selectSentMessageList() 진입 - memberNo: {}, cp: {}", memberNo, cp);

	    // 1. 전체 보낸 쪽지 개수 조회
	    int listCount = messageMapper.getSentCount(memberNo);
	    log.info("전체 보낸 쪽지 개수: {}", listCount);

	    // 2. 페이지네이션 객체 생성
	    Pagination pagination = new Pagination(cp, listCount);
	    log.info("Pagination 생성 완료: {}", pagination);

	    // 3. RowBounds 생성
	    int limit = pagination.getLimit();
	    int offset = (cp - 1) * limit;
	    RowBounds rowBounds = new RowBounds(offset, limit);
	    log.debug("RowBounds 생성 - offset: {}, limit: {}", offset, limit);

	    // 4. 현재 페이지의 보낸 쪽지 목록 조회
	    List<Message> sentList = messageMapper.selectSentMessagesPagination(memberNo, rowBounds);
	    log.info("조회된 보낸 쪽지 수: {}", sentList.size());

	    // 5. 닉네임 매핑
	    List<Map<String, Object>> nicknameReceivedList = new ArrayList<>();
	    for (Message msg : sentList) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("msg", msg);

	        Member sender = memberMapper.selectMemberByNo(msg.getReceiverNo());
	        map.put("receiverNickname", sender.getMemberNickname());

	        nicknameReceivedList.add(map);
	    }
	    log.debug("닉네임 매핑 완료 - nicknameSentList 크기: {}", nicknameReceivedList.size());

	    // 6. 결과 묶기
	    Map<String, Object> result = new HashMap<>();
	    result.put("pagination", pagination);
	    result.put("nicknameSentList", nicknameReceivedList);

	    log.info("selectSentMessageList() 완료 - 반환 map 구성 완료");
	    return result;
	}




	@Override
	public Map<String, Object> selectReceivedMessageListPagination(int memberNo, int cp) {
	    log.info("selectReceivedMessageListPagination() 진입 - memberNo: {}, cp: {}", memberNo, cp);

	    // 1. 전체 받은 쪽지 개수 조회
	    int listCount = messageMapper.getReceivedCount(memberNo);
	    log.info("전체 받은 쪽지 개수: {}", listCount);

	    // 2. 페이지네이션 객체 생성
	    Pagination pagination = new Pagination(cp, listCount);
	    log.info("Pagination 생성 완료: {}", pagination);

	    // 3. RowBounds 생성
	    int limit = pagination.getLimit();
	    int offset = (cp - 1) * limit;
	    RowBounds rowBounds = new RowBounds(offset, limit);
	    log.debug("RowBounds 생성 - offset: {}, limit: {}", offset, limit);

	    // 4. 현재 페이지의 받은 쪽지 목록 조회
	    List<Message> messageList = messageMapper.selectReceivedMessageListPagination(memberNo, rowBounds);
	    log.info("조회된 받은 쪽지 수: {}", messageList.size());

	    // 5. 보낸 사람 닉네임 매핑
	    List<Map<String, Object>> nicknameReceivedList = new ArrayList<>();
	    for (Message msg : messageList) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("msg", msg);

	        Member sender = memberMapper.selectMemberByNo(msg.getSenderNo());
	        map.put("senderNickname", sender.getMemberNickname());

	        nicknameReceivedList.add(map);
	    }
	    log.debug("닉네임 매핑 완료 - nicknameReceivedList 크기: {}", nicknameReceivedList.size());

	    // 6. 결과 묶기
	    Map<String, Object> result = new HashMap<>();
	    result.put("pagination", pagination);
	    result.put("nicknameReceivedList", nicknameReceivedList);

	    log.info("selectReceivedMessageListPagination() 완료! - 반환 map 구성 완료");
	    return result;
	}




	/**
	 *아직 안 읽은 메시지의 개수
	 */
	@Override
	public int selectUnread(Member loginMember) {
		
		return messageMapper.selectUnread(loginMember);
	}




	

    
}