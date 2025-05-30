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
				System.out.println(listCount);

				Pagination pagination = new Pagination(cp, listCount);

				int limit = pagination.getLimit();
				int offset = (cp - 1) * limit;
				RowBounds rowBounds = new RowBounds(offset, limit);

				List<ShareBoard> boardList = mapper.selectBoardList(boardCode, rowBounds);
				System.out.println(boardList);

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
			result = mapper.insertBoardJJim(map);
		}
		if (result > 0) {
			return mapper.selectBoardJJim(map.get("boardNo"));
		}

		return -1;
	}

	@Override
	public List<ShareBoard> selectRecent() {
		List<ShareBoard> boardList = mapper.selectRecent();
		extractThumbnail(boardList); // 썸네일 추출
		return boardList;
	}

	@Override
	public int shareStatus(Map<String, Object> map) {
		// 필수 파라미터 체크
		if (map == null || map.get("boardNo") == null || map.get("memberNo") == null || map.get("shareStatus") == null) {
			return 0;
		}

		Map<String, Integer> paramMap = new HashMap<>();
		paramMap.put("boardNo", (Integer)map.get("boardNo"));
		paramMap.put("boardCode", 1); // 나눔 게시판 코드
		
		ShareBoard board = mapper.selectOne(paramMap);
		
		if (board == null || board.getMemberNo() != (Integer)map.get("memberNo")) {
			return 0;
		}
		return mapper.updateShareStatus(map);
	}
    
	@Override
	public List<Board> selectJjimList(int memberNo) {
	    return mapper.selectJjimList(memberNo);

	}

	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {

	    String searchType = (String) paramMap.get("searchType");
	    String searchKeyword = (String) paramMap.get("searchKeyword");
	    String mainCategory = (String) paramMap.get("mainCategory");
	    String subCategory = (String) paramMap.get("subCategory");

	    boolean hasSearch = searchType != null && !searchType.isBlank() && searchKeyword != null && !searchKeyword.isBlank();
	    boolean hasCategory = mainCategory != null && !mainCategory.isBlank() && subCategory != null && !subCategory.isBlank();

	    if (hasSearch && hasCategory) {
	        return searchAndCategoryList(paramMap, cp);
	    } else if (hasSearch) {
	        return searchOnlyList(paramMap, cp);
	    } else if (hasCategory) {
	        return categoryOnlyList(paramMap, cp);
	    } else {
	        return selectBoardList((int)paramMap.get("boardCode"), cp);
	    }
	}

	private Map<String, Object> searchOnlyList(Map<String, Object> paramMap, int cp) {
	    int listCount = mapper.getSearchOnlyCount(paramMap);
	    System.out.println(listCount);
	    Pagination pagination = new Pagination(cp, listCount);
	    RowBounds rowBounds = new RowBounds((cp - 1) * pagination.getLimit(), pagination.getLimit());
	    List<ShareBoard> boardList = mapper.selectSearchOnlyList(paramMap, rowBounds);
	    extractThumbnail(boardList);
	    
	    Map<String, Object> result = new HashMap<>();
	    result.put("pagination", pagination);
	    result.put("boardList", boardList);
	    return result;
	}

	private Map<String, Object> categoryOnlyList(Map<String, Object> paramMap, int cp) {
	    int listCount = mapper.getCategoryOnlyCount(paramMap);
	    Pagination pagination = new Pagination(cp, listCount);
	    RowBounds rowBounds = new RowBounds((cp - 1) * pagination.getLimit(), pagination.getLimit());
	    List<ShareBoard> boardList = mapper.selectCategoryOnlyList(paramMap, rowBounds);
	    extractThumbnail(boardList);

	    Map<String, Object> result = new HashMap<>();
	    result.put("pagination", pagination);
	    result.put("boardList", boardList);
	    return result;
	}

	private Map<String, Object> searchAndCategoryList(Map<String, Object> paramMap, int cp) {
	    int listCount = mapper.getSearchAndCategoryCount(paramMap);
	    System.out.println(listCount);
	    Pagination pagination = new Pagination(cp, listCount);
	    RowBounds rowBounds = new RowBounds((cp - 1) * pagination.getLimit(), pagination.getLimit());
	    List<ShareBoard> boardList = mapper.selectSearchAndCategoryList(paramMap, rowBounds);
	    extractThumbnail(boardList);

	    Map<String, Object> result = new HashMap<>();
	    result.put("pagination", pagination);
	    result.put("boardList", boardList);
	    return result;
	}

	
	
}

