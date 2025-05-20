// edu/kh/project/message/model/service/MessageServiceImpl.java
package edu.kh.semi.message.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.message.model.dto.Message;
import edu.kh.semi.message.model.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    
    
    /**
     * 메시지 보내서 넣기
     */
    @Override
    public int sendMessage(Message message) {
        log.info("sendMessage Service 실행 - 보낼 메시지: {}", message);
        int result = messageMapper.insertMessage(message);
        log.info("sendMessage 결과: {}", result);
        return result;
    }

    
    
    
    /**
     * 받은 메시지함으로 이동
     */
    @Override
    public List<Message> selectReceiveMessages(int memberNo) {
        log.info("getInboxMessages Service 실행 - 수신 회원 번호: {}", memberNo);
        List<Message> list = messageMapper.selectReceiveMessages(memberNo);
        log.info("getInboxMessages 조회된 메시지 개수: {}", list.size());
        return list;
    }

    
    /**
     * 보낸 메시지함으로 이동 
     */
    @Override
    public List<Message> selectSentMessages(int memberNo) {
    	return messageMapper.selectSentMessages(memberNo);
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
        log.info("selectMessageDetail 조회된 메시지: {}", detail);
        return detail;
    }

  

  

    /**
     * 메시지 삭제 
     */
    @Override
    public int deleteMessagePage(Message messages) {
        log.info("deleteMessagePage Service 실행 - 삭제할 메시지 정보: {}", messages);
        int result = messageMapper.deleteMessagePage(messages);
        log.info("deleteMessagePage 결과: {}", result);
        return result;
    }
    
}
