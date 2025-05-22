package edu.kh.semi.QNABoard.model.servcie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.QNABoard.model.dto.QNABoard;
import edu.kh.semi.QNABoard.model.mapper.QNABoardMapper;
import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)

public class QNABoardServiceImpl implements QNABoardService {
	
	@Autowired
	private QNABoardMapper mapper;
	
	/**
	 * 보드의 타입만 알아내는 로직으로 페이지네이 전용으로 사용된다.
	 */
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		// TODO Auto-generated method stub
		return mapper.selectBoardTypeList();
	}
	
	
	
	/** 검색이 아닌 그냥 게시판일 때 QNA게시판에서의 조회
	 * @param boardCode
	 * @param cp
	 * @return
	 */
	@Override
	public Map<String, Object> selectQNABoardList(int boardCode, int cp) {
		
		
	
		
		
		
		// TODO Auto-generated method stub
		// 페이지네이션 >의 의미: 다음 목록의 첫 페이지로 이동하기 위함
				// 맨 끝 목록으로 이동하려면 >>>를 쓰곤 한다
				
				// 1. 지정된 게시판 (boardCode)에서 삭제되지 않은 게시글 수를 조회 (pagination에 쓰기 위함이다.)
				
				int listCount = mapper.getQNAListCount(boardCode);
				// 몇 번 게시판에 있는 게시글의 총 개수
				
				
				// 2. 페이지네이션의 생성 => 1번의 결과와 컨트롤러에서 전달받은 cp를 이용하여 
				// 페이지네이션 객체 자체를 생성한다
				
				// Pagination 객체는 게시글 목록 구성에 필요한 값들을 저장하는 객체
				
				Pagination pagination = new Pagination(cp, listCount); // 현재 목록, 전체 게시글

				
				// 3. 특정 게시판만의 지정된 페이지 목록만 조회 => 딱 limit인 10개씩만
				
				/* 
				
				mybatis framework가 제공하는 객체 
				=> 지정된 크기만큼 건너 뛰고 제한된 크기만큼의 행을 조회하는 객체
				
				가령 31~40이면 1~30은 건너뛰고 10개만
				
				지정된 크기만큼 건너 뛰는 인자 offset
				제한된 크기 = limit
				
				*/
				
				int limit = pagination.getLimit(); //10이 반환
				int offset = (cp-1) * limit;
				
				RowBounds rowBounds = new RowBounds(offset,limit);
				
				
				// Mapper 메서드 호출 시에는 매개변수로 하나만 전달 가능 
				// => rowBounds를 이용할 때는 둘 다 이용가능
				// 첫 인자 = SQL에 전달할 파라미터, 두번째 인자  = rowBounds
				
				// 게시판의 종류
				List<QNABoard> boardList = mapper.selectQNABoardList(boardCode, rowBounds);
				
				log.debug("boardList결과"+boardList);
	
				log.debug("boardList결과 : {}", boardList);
				
				
				
				// 컨트롤러에게 boardList와 pagenation을 돌려줘야 한다
				
				
				// 4. 목록 조회결과인 boardList와 Pagenation 객체를 map으로 묶어 반환
				
				
				Map<String, Object> map = new HashMap<>();
				map.put("pagination", pagination);
				map.put("boardList", boardList);

				
				return map ;
	}
	
	
	

	/** QNA게시판에 있는 글이지만 검색일 떄의 조회
	 * @param paraMap
	 * @param cp
	 * @return
	 */
	@Override
	public Map<String, Object> searchQNAList(Map<String, Object> paraMap, int cp) {
		

		return null;
	}



	@Override
	public List<Board> searchByKeyword(String query) {
	    return mapper.searchByKeyword(query);

	}



	@Override
	public List<Board> selectByMember(int memberNo) {
		// TODO Auto-generated method stub
		return mapper.selectByMember(memberNo);
	}






}
