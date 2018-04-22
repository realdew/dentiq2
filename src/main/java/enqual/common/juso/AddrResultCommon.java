package enqual.common.juso;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AddrResultCommon {
	@Getter @Setter String currentPage;		// Integer Y 페이지 번호 
	@Getter @Setter String countPerPage;	// Integer Y 페이지당 출력할 결과 Row 수 
	
	
	@Getter @Setter String totalCount;		// Integer Y 총 검색 데이터수 
	@Getter @Setter String errorCode;		// String Y 에러 코드 
	@Getter @Setter String errorMessage;	// String Y 에러 메시지
}
