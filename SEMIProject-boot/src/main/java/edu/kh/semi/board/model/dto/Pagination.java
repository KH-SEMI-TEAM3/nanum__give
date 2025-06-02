package edu.kh.semi.board.model.dto;

public class Pagination {

	private int currentPage;  // 현재 페이지 번호
	private int listCount;    // 전체 게시글 수
	
	private int limit = 10;   // 한 페이지 목록에 보여지는 게시글 수
	private int pageSize = 10; // 페이지 번호 목록에 보여질 번호 개수

	private int maxPage;      // 전체 페이지 중 가장 마지막 페이지
	private int startPage;    // 페이지 번호 목록의 시작 번호
	private int endPage;      // 페이지 번호 목록의 끝 번호

	private int prevPage;     // 이전 페이지 목록의 마지막 번호
	private int nextPage;     // 다음 페이지 목록의 시작 번호

	public Pagination(int currentPage, int listCount) {
		this.currentPage = currentPage;
		this.listCount = listCount;
		calculate();
	}

	public Pagination(int currentPage, int listCount, int limit, int pageSize) {
		this.currentPage = currentPage;
		this.listCount = listCount;
		this.limit = limit;
		this.pageSize = pageSize;
		calculate();
	}

	// 페이징 계산 수행
	private void calculate() {
		if (limit <= 0) limit = 10;
		if (pageSize <= 0) pageSize = 10;
		if (currentPage <= 0) currentPage = 1;
		if (listCount < 0) listCount = 0;

		maxPage = (int) Math.ceil((double) listCount / limit);
		if(maxPage == 0) maxPage = 1; // 검색 결과 없을 시 1 뒤에 0 페이지 추가되는 오류
		startPage = (currentPage - 1) / pageSize * pageSize + 1;
		endPage = startPage + pageSize - 1;
		if (endPage > maxPage) endPage = maxPage;

		prevPage = (currentPage <= pageSize) ? 1 : startPage - 1;
		nextPage = (endPage == maxPage) ? maxPage : endPage + 1;
	}

	public int getStartRow() {
		return (currentPage - 1) * limit;
	}
	
	public int getEndRow() {
		return currentPage * limit;
	}

	// Getters
	public int getCurrentPage() { return currentPage; }
	public int getListCount() { return listCount; }
	public int getLimit() { return limit; }
	public int getPageSize() { return pageSize; }
	public int getMaxPage() { return maxPage; }
	public int getStartPage() { return startPage; }
	public int getEndPage() { return endPage; }
	public int getPrevPage() { return prevPage; }
	public int getNextPage() { return nextPage; }

	// Setters (필요 시 동적 변경 가능)
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		calculate();
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
		calculate();
	}

	public void setLimit(int limit) {
		this.limit = limit;
		calculate();
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculate();
	}

	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", listCount=" + listCount
				+ ", limit=" + limit + ", pageSize=" + pageSize + ", maxPage=" + maxPage
				+ ", startPage=" + startPage + ", endPage=" + endPage
				+ ", prevPage=" + prevPage + ", nextPage=" + nextPage + "]";
	}
}
