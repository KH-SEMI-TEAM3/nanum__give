package edu.kh.semi.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.board.model.mapper.ShareBoardMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ShareBoardServiceImpl implements ShareBoardService {

    private final ShareBoardMapper mapper;
    
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {

				int listCount = mapper.getListCount(boardCode);

				Pagination pagination = new Pagination(cp, listCount);

				int limit = pagination.getLimit();
				int offset = (cp - 1) * limit;
				RowBounds rowBounds = new RowBounds(offset, limit);

				List<ShareBoard> boardList = mapper.selectBoardList(boardCode, rowBounds);

			    // 썸네일 추출 추가
			    for (ShareBoard board : boardList) {
			        String content = board.getBoardContent();
			        if (content != null) {
			            Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(content);
			            if (matcher.find()) {
			                board.setThumbnail(matcher.group(1));
			            }
			        }
			    }
				Map<String, Object> map = new HashMap<>();

				map.put("pagination", pagination);
				map.put("boardList", boardList);

				return map;
			}
	
	@Override
	public ShareBoard selectOne(Map<String, Integer> map) {
		return mapper.selectOne(map);
	}

	@Override
	public int updateReadCount(int boardNo) {
		int result = mapper.updateReadCount(boardNo);
		if (result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		return -1;
	}

	@Override
	public int boardJJim(Map<String, Integer> map) {
		int result = 0;

		if (map.get("jjimCheck") == 1) {

			result = mapper.deleteBoardJJim(map);

		} else {
			// 2. 좋아요가 해제된 상태인 경우(likeCheck == 0)
			// -> BOARD_LIKE 테이블에 INSERT

			result = mapper.insertBoardJJim(map);

		}

		// 3. 다시 해당 게시글의 좋아요 개수 조회해서 반환
		if (result > 0) {
			return mapper.selectJJimCount(map.get("boardNo"));
		}

		return -1;
	}

	@Override
	public List<Board> searchByKeyword(String query) {
		 return mapper.searchByKeyword(query);
	}

	@Override
	public List<Board> selectByMember(int memberNo) {
		return mapper.selectByMember(memberNo);
	}

	@Override
	public List<ShareBoard> selectRecent() {
		return mapper.selectRecent();
	}
	



}