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
    
    private void extractThumbnail(List<ShareBoard> boardList) {
        for (ShareBoard board : boardList) {
            String content = board.getBoardContent();
            if (content != null) {
                Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(content);
                if (matcher.find()) {
                    board.setThumbnail(matcher.group(1));
                }
            }
        }
    }
    
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {

				int listCount = mapper.getListCount(boardCode);

				Pagination pagination = new Pagination(cp, listCount);

				int limit = pagination.getLimit();
				int offset = (cp - 1) * limit;
				RowBounds rowBounds = new RowBounds(offset, limit);

				List<ShareBoard> boardList = mapper.selectBoardList(boardCode, rowBounds);

//			    // 썸네일 추출 추가
//			    for (ShareBoard board : boardList) {
//			        String content = board.getBoardContent();
//			        if (content != null) {
//			            Matcher matcher = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(content);
//			            if (matcher.find()) {
//			                board.setThumbnail(matcher.group(1));
//			            }
//			        }
//			    }
				extractThumbnail(boardList); // 썸네일 추출
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
			return mapper.selectBoardJJim(map.get("boardNo"));
		}

		return -1;
	}

//	@Override
//	public List<Board> searchByKeyword(String query) {
//		 return mapper.searchByKeyword(query);
//	}

	@Override
	public List<Board> selectByMember(int memberNo) {
		return mapper.selectByMember(memberNo);
	}

	@Override
	public List<ShareBoard> selectRecent() {
		List<ShareBoard> boardList = mapper.selectRecent();
		extractThumbnail(boardList); // 썸네일 추출
		return boardList;
	}
	
    @Override
    public Map<String, Object> searchByKeyword(String query, int page) {
        int listCount = mapper.getSearchCount(query);
        Pagination pagination = new Pagination(page, listCount, 10, 10);

        int start = (pagination.getCurrentPage() - 1) * pagination.getLimit() + 1;
        int end = start + pagination.getLimit() - 1;

        List<Board> boardList = mapper.searchByKeyword(query, start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pagination", pagination);

        return result;
    }

    @Override
    public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {
        
        // paramMap 안에는 key, query, boardCode가 들어있음

        // 1. 검색 조건 + 삭제되지 않은 게시글 수 조회
        int listCount = mapper.getCategorySearchCount(paramMap);

        // 2. 페이지네이션 객체 생성
        Pagination pagination = new Pagination(cp, listCount);

        // 3. offset, limit 계산 (페이지네이션 기반)
        int limit = pagination.getLimit();
        int offset = (cp - 1) * limit;

        // 4. RowBounds 객체 생성 (MyBatis에서 페이징 처리에 사용)
        RowBounds rowBounds = new RowBounds(offset, limit);

        // 5. 실제 검색된 게시글 목록 조회
        List<ShareBoard> boardList = mapper.selectCategorySearchList(paramMap, rowBounds);

        // 6. 결과를 map으로 포장해서 컨트롤러로 전달
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("boardList", boardList);

        return map;
    }

	@Override
	public int shareStatus(Map<String, Object> map) {
		// 필수 파라미터 체크
		if (map == null || map.get("boardNo") == null || map.get("memberNo") == null || map.get("shareStatus") == null) {
			return 0;
		}

		// 작성자 체크를 위해 게시글 정보 조회
		Map<String, Integer> paramMap = new HashMap<>();
		paramMap.put("boardNo", (Integer)map.get("boardNo"));
		paramMap.put("boardCode", 1); // 나눔 게시판 코드
		
		ShareBoard board = mapper.selectOne(paramMap);
		
		// 게시글이 존재하지 않거나 작성자가 아닌 경우
		if (board == null || board.getMemberNo() != (Integer)map.get("memberNo")) {
			return 0;
		}
		
		return mapper.updateShareStatus(map);
	}

	@Override
	public List<ShareBoard> filterByCategory(Map<String, Object> paramMap) {
		List<ShareBoard> boardList = mapper.filterByCategory(paramMap);
		extractThumbnail(boardList); // 썸네일 추출
		return boardList;
    
	@Override
	public List<Board> selectJjimList(int memberNo) {
		// TODO Auto-generated method stub
	    return mapper.selectJjimList(memberNo);

	}
}

