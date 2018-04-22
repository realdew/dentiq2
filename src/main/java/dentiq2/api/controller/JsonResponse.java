package dentiq2.api.controller;




import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.LogicalException;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class JsonResponse<T> {
	
	public static final String SUCCESS_CODE = "0000";
	
	@Getter private String _RESPONSE_CODE_ = SUCCESS_CODE;
	@Getter private String _RESPONSE_MSG_;
	
	
	
	@Getter @Setter private CommonHeader			_COMMON_HEADER_;
	@Getter @Setter private PageableResponseHeader	_PAGEABLE_RESPONSE_HEADER_;
	@Getter 		private T						_RESPONSE_;
	
	public JsonResponse() {}
	
	public JsonResponse(T response) {
		this._COMMON_HEADER_ = new CommonHeader();
		this._RESPONSE_ = response;
	}
	public JsonResponse(PageableResponseHeader pageableResponseHeader, T response) {
		this._COMMON_HEADER_ = new CommonHeader();
		this._PAGEABLE_RESPONSE_HEADER_ = pageableResponseHeader;
		this._RESPONSE_ = response;
	}
	
	public void setResponse(T response) {
		this._RESPONSE_ = response;
	}
	
	public void setException(Exception ex) {
		ex.printStackTrace();
		if ( ex instanceof LogicalException ) {
			this._RESPONSE_CODE_ = ((LogicalException) ex).getCode();
			this._RESPONSE_MSG_  = ((LogicalException) ex).getMessage();
			
			//TODO 로직오류 남길 것
			
		} else {
			
			this._RESPONSE_CODE_ = "Z999";
			//this._RESPONSE_MSG_ = "콜센터에 문의 바랍니다.";
			this._RESPONSE_MSG_  = ex.getMessage(); // 개발용
			
			//TODO 시스템 장애  로그 남길 것
		}
		
		//TODO
		ex.printStackTrace();
		ex.printStackTrace(System.out);
	}
	

}

@JsonInclude(Include.NON_NULL)
class CommonHeader {
	@Getter @Setter private String version;
	@Getter @Setter private String sessionStatus;
	@Getter @Setter private Object additionalInfo;
}

@JsonInclude(Include.NON_NULL)
class PageableResponseHeader {
	
	public static final int DEFAULT_ITEM_CNT_PER_PAGE = 10;
	
	@Getter private final int itemCntPerPage;		// 페이지 당 아이템 개수
	
	@Getter private final int pageNo;				// 현재 페이지 번호
	
	@Getter private final int totalPageCnt;			// 전체 페이지 개수
	
	@Getter private final int totalItemCnt;			// 전체 아이템 개수
	
	@Getter @Setter private int currentItemCnt;		// 현재 아이템 개수 <== List<T>의 개수
	
	
	PageableResponseHeader() {
		this(DEFAULT_ITEM_CNT_PER_PAGE);
	}
	
	PageableResponseHeader(int itemCntPerPage) {
		this.itemCntPerPage = itemCntPerPage;
		this.pageNo = 1;
		this.totalPageCnt = 1;
		this.totalItemCnt = 0;
		this.currentItemCnt = 0;
	}
	
	PageableResponseHeader(int pageNo, int totalItemCnt) throws Exception {
		this(pageNo, totalItemCnt, DEFAULT_ITEM_CNT_PER_PAGE);
	}
	
	PageableResponseHeader(int pageNo, int totalItemCnt, int itemCntPerPage ) throws Exception {
		this.pageNo = pageNo;
		this.totalItemCnt = totalItemCnt;
		this.itemCntPerPage = itemCntPerPage;
		
		this.totalPageCnt = this.totalItemCnt / this.itemCntPerPage + 1;
		
		if ( this.pageNo > this.totalPageCnt )
			throw new Exception("페이지 번호 초과, pageNo[" + this.pageNo + "] totalPage[" + this.totalPageCnt + "]");
	}
}