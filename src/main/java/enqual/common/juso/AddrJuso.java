package enqual.common.juso;

//import com.fasterxml.jackson.annotation.JsonIgnore;

//import dentiq.api.model.LocationCode;
//import dentiq.api.service.exception.LogicalException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AddrJuso {
	@Getter @Setter protected	Integer	jusoIdx;			//	검색 결과 INDEX (검색 결과 전체에서 순번. JusoUtil에서 생성)
	
	@Getter @Setter protected	String	roadAddr;		//	전체 도로명주소	Y	String
	@Getter @Setter protected	String	roadAddrPart1;	//	도로명주소(참고항목 제외)	Y	String
	@Getter @Setter protected	String	roadAddrPart2;	//	도로명주소 참고항목	N	String
	@Getter @Setter protected	String	jibunAddr;		//	지번주소	Y	String
	@Getter @Setter protected	String	engAddr;		//	도로명주소(영문)	Y	String
	@Getter @Setter protected	String	zipNo;			//	우편번호	Y	String
	@Getter @Setter protected	String	admCd;			//	행정구역코드	Y	String					- 좌표검색 API 입력값
	@Getter @Setter protected	String	rnMgtSn;		//	도로명코드	Y	String						- 좌표검색 API 입력값
	@Getter @Setter protected	String	bdMgtSn;		//	건물관리번호	Y	String
	@Getter @Setter protected	String	detBdNmList;	//	상세건물명	N	String
	@Getter @Setter protected	String	bdNm;			//	건물명	N	String
	@Getter @Setter protected	String	bdKdcd;			//	공동주택여부(1 : 공동주택, 0 : 비공동주택)	Y	String
	@Getter @Setter protected	String	siNm;			//	시도명	Y	String
	@Getter @Setter protected	String	sggNm;			//	시군구명	Y	String
	@Getter @Setter protected	String	emdNm;			//	읍면동명	Y	String
	@Getter @Setter protected	String	liNm;			//	법정리명	N	String
	@Getter @Setter protected	String	rn;				//	도로명	Y	String
	@Getter @Setter protected	String	udrtYn;			//	지하여부(0 : 지상, 1 : 지하)	Y	String	- 좌표검색 API 입력값
	@Getter @Setter protected	String	buldMnnm;		//	건물본번	Y	Number						- 좌표검색 API 입력값
	@Getter @Setter protected	String	buldSlno;		//	건물부번	Y	Number						- 좌표검색 API 입력값
	@Getter @Setter protected	String	mtYn;			//	산여부(0 : 대지, 1 : 산)	Y	String
	@Getter @Setter protected	String	lnbrMnnm;		//	지번본번(번지)	Y	Number
	@Getter @Setter protected	String	lnbrSlno;		//	지번부번(호)	Y	Number
	@Getter @Setter protected	String	emdNo;			//	읍면동일련번호	Y	String	
	
	
//	@Getter @Setter protected	String	addrMain;		// 주 주소 - 화면으로부터 입력됨
//	@Getter @Setter protected	String	addrDetail;		// 상세주소 - 화면으로부터 입력됨	
//	
//	@Getter @Setter protected String	entX;			//	입구 좌표 X		: from juso.go.kr 좌표 검색 API 결과
//	@Getter @Setter protected String	entY;			//	입구 좌표 Y		: from juso.go.kr 좌표 검색 API 결과
//	
//	@Getter @Setter protected String	latLonX;			// WGS84 좌표 X : CoordUtil의 좌표 변환 결과
//	@Getter @Setter protected String	latLonY;			// WGS84 좌표 Y : CoordUtil의 좌표 변환 결과
	
	
	
	
	
}
