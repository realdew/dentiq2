package dentiq2.api.util;

import lombok.Getter;
import lombok.ToString;

@ToString
public class PageInfo {
	public static final int DEFAULT_ITEM_CNT_PER_PAGE = 10;
	
	@Getter public final int pageNo;				// 조회 전 : 조회할 페이지 번호
	@Getter public final int itemCntPerPage;		// 조회 전 : 한 페이지당 아이템 개수
	
	@Getter public final int startIndexOnPage;		// 조회 전 (계산됨) : DB 조회 시 시작 index
	
		
	@Getter private int totalItemCnt;				// 조회 후 결과 : 조회된 전체 아이템의 개수
	@Getter private int totalPageCnt;				// 조회 후 결과 : 조회된 전체 페이지 개수
	@Getter private int currentItemCnt;				// 조회 후 결과 : 현재 페이지에서 보여질 아이템 개수
	
	public void setResult(int totalItemCnt) {
		this.totalItemCnt = totalItemCnt;
		
		this.totalPageCnt = ((int) (this.totalItemCnt / this.itemCntPerPage)) + 1;
		
		// this.currentItemCnt = ((int) (this.totalItemCnt % this.itemCntPerPage));		
	}
	
	
	public PageInfo(Integer page, Integer size) throws Exception {
		if ( page == null )	this.pageNo = 1;
		else				this.pageNo = page;		
		if ( this.pageNo < 1 ) throw new Exception("Invalid Format. pageNo should be a positive number. [" + this.pageNo + "]");
		
		if ( size == null )	this.itemCntPerPage = DEFAULT_ITEM_CNT_PER_PAGE;
		else				this.itemCntPerPage = size;
		if ( this.itemCntPerPage < 1 ) throw new Exception("Invalid Format. pageSize should be a positive number. [" + this.itemCntPerPage + "]");
		
		
		this.startIndexOnPage = (this.pageNo-1) * this.itemCntPerPage;
	}	
	
	public PageInfo(Integer pageNo) throws Exception {
		this(pageNo, DEFAULT_ITEM_CNT_PER_PAGE);
	}
	
	public PageInfo() throws Exception {
		this(1, DEFAULT_ITEM_CNT_PER_PAGE);
	}
	
}
