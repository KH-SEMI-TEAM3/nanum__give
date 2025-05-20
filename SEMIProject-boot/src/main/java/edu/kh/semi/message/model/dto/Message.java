package edu.kh.semi.message.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Message {
	private int messageNo;
	private String messageText;
	private String messageWriteDate;
	private String messageReadFl;
	private String messageDelFl;
	private int boardNo;
	private int senderNo;
	private int receiverNo;
}
