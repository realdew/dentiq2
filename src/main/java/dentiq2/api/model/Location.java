package dentiq2.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.code.LocationCodeManager;
import enqual.common.juso.AddrJuso;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class Location extends AddrJuso {
	
	public static final String CODE_DELIMETER = ".";
	
	public static final String FORMAT_LOCATION	= "0";		// XX.XXYYY 형태
	public static final String FORMAT_SIDO		= "1";		// XX 형태
	public static final String FORMAT_SIGU		= "2";		// XXYYY 형태
	
	
	public static final String LOCATION_TYPE_SIDO		= "1";		// 해당 지역이 시도
	public static final String LOCATION_TYPE_SIGU		= "2";		// 해당 지역이 시구
	
	
	
	public Location() {}
	
	public Location(AddrJuso juso) throws Exception {
		super();
		this.jusoIdx = juso.getJusoIdx();
		
		this.roadAddr =			juso.getRoadAddr();
		this.roadAddrPart1 =	juso.getRoadAddrPart1();
		this.roadAddrPart2 =	juso.getRoadAddrPart2();
		this.jibunAddr =		juso.getJibunAddr();
		this.engAddr =			juso.getEngAddr();	
		this.zipNo =			juso.getZipNo();	
		this.admCd =			juso.getAdmCd();
		this.rnMgtSn =			juso.getRnMgtSn();
		this.bdMgtSn =			juso.getBdMgtSn();
		this.detBdNmList =		juso.getDetBdNmList();
		this.bdNm =				juso.getBdNm();
		this.bdKdcd =			juso.getBdKdcd();
		this.siNm = 			juso.getSiNm();
		this.sggNm =			juso.getSggNm();
		this.emdNm =			juso.getEmdNm();
		this.liNm =				juso.getLiNm();
		this.rn =				juso.getRn();
		this.udrtYn =			juso.getUdrtYn();
		this.buldMnnm =			juso.getBuldMnnm();
		this.buldSlno =			juso.getBuldSlno();
		this.mtYn =				juso.getMtYn();
		this.lnbrMnnm =			juso.getLnbrMnnm();
		this.lnbrSlno =			juso.getLnbrSlno();
		this.emdNo =			juso.getEmdNo();
		
		// admCd로 LOCAITON_CODE, SIDO_CODE, SIGU_CODE 생성
		if ( admCd==null || admCd.trim().length()<5 ) throw new Exception("ADM_CD Invalid. [" + admCd + "]");
		this.admCd = this.admCd.trim();
		
		String _sidoCode		= this.admCd.substring(0, 2); // 시도코드는 행정구역 코드의 처음 2자리
		String _siguCode		= this.admCd.substring(0, 5);	// 시구코드는 행정구역 코드의 처음 5자리
		this.locationCode		= _sidoCode + CODE_DELIMETER + _siguCode;
		
		this.setSidoCode(_sidoCode);
		this.setSiguCode(_siguCode);
		
		
//		this.sidoCode		= this.admCd.substring(0, 2); // 시도코드는 행정구역 코드의 처음 2자리
//		this.siguCode		= this.admCd.substring(0, 5);	// 시구코드는 행정구역 코드의 처음 5자리
//		this.locationCode	= this.sidoCode + CODE_DELIMETER + this.siguCode;
//		
//		// LocationCode 객체에서 sidoName, siguName 얻어 옴
//		LocationCodeManager locationCodeInstance = LocationCodeManager.getInstance();
//		Location locationDef = locationCodeInstance.getLocationByLocationCode(this.locationCode);
//		this.sidoName = locationDef.getSidoName();
//		this.siguName = locationDef.getSiguName();
		
	}
	
	/**
	 * 코드 문자열이 어떠한 지역코드의 형식인지 리턴한다.
	 * @param	code 지역코드 형식 문자열
	 * @return	XX 형태이면 FORMAT_SIDO 리턴. YYYYY 형태이면 FORMAT_SIGU 리턴. XX.YYYYY 형태이면 FORMAT_LOCATION 리턴
	 * @throws	Exception code가 null 또는 "". 위의 3가지 형식에 일치하지 않는 경우
	 */
	public static String checkLocationCodeFormat(String code) throws Exception {
		if ( code==null ) throw new Exception("code is null");
		code = code.trim();
		if ( code.equals("") ) throw new Exception("code is empty. [" + code + "]");
		
		int len = code.length();
		
		if ( len==8 && code.indexOf(CODE_DELIMETER)==2 ) return FORMAT_LOCATION;		
		if ( len==2 ) return FORMAT_SIDO;		
		if ( len==5 ) return FORMAT_SIGU;
		
		throw new Exception("Invalid code [" + code + "]");
	}
	
	
	
	@Getter @Setter private	String	addrMain;		//	주 주소		- 화면으로부터 입력됨
	@Getter @Setter private	String	addrDetail;		//	상세주소	- 화면으로부터 입력됨	
	
	@Getter @Setter private String	entX;			//	입구 좌표 X	: from juso.go.kr 좌표 검색 API 결과
	@Getter @Setter private String	entY;			//	입구 좌표 Y	: from juso.go.kr 좌표 검색 API 결과
	
	@Getter @Setter private String	latLonX;		// WGS84 좌표 X : CoordUtil의 좌표 변환 결과
	@Getter @Setter private String	latLonY;		// WGS84 좌표 Y : CoordUtil의 좌표 변환 결과
	
	
	@Getter @Setter protected String locationCode;	// 지역코드
	
	@Getter @Setter protected String locationType;
	
	@Getter protected String sidoCode;
	public void setSidoCode(String sidoCode) {
		this.sidoCode = sidoCode;
		this.sidoName = LocationCodeManager.getInstance().getSidoName(sidoCode);
	}
	@Getter @Setter protected String sidoName;
	
	@Getter protected String siguCode;
	public void setSiguCode(String siguCode) {
		this.siguCode = siguCode;
		this.siguName = LocationCodeManager.getInstance().getSiguName(siguCode);
	}
	@Getter @Setter protected String siguName;
	
	
	public String getLocationName() {
    	if ( this.locationType == null ) return null;
    	
		if ( this.locationType.equals(LOCATION_TYPE_SIDO) ) {
			return this.sidoName;
		} else if ( this.locationType.equals(LOCATION_TYPE_SIGU) ) {
			return this.siguName;
		} else {
			return null;
		}
	}
	
	public String getFullLocationName() {
		if ( this.locationType == null ) return null;
		
		if ( this.locationType.equals(LOCATION_TYPE_SIDO) ) {
			return this.sidoName;
		} else if ( this.locationType.equals(LOCATION_TYPE_SIGU) ) {
			return this.sidoName + " " + this.siguName;
		} else {
			return null;
		}
	}
	
	
	// 시구를 표현하는 LocationCode(예: AB.CDEFG)나 SiguCode(예: CDEFG)를 받아서, 이를 SiguCode(예: CDEFG)의 형태로 변경
	public static List<String> convertLocationCodeOrSiguCodeListToSiguCodeList(List<String> locationCodeOrSiguCodeList) throws Exception {
		if ( locationCodeOrSiguCodeList==null || locationCodeOrSiguCodeList.size()<1 ) return null;
		
		List<String> siguCodeList = new ArrayList<String>();
		
		for ( String locationOrSiguCode : locationCodeOrSiguCodeList ) {
			if ( locationOrSiguCode==null || locationOrSiguCode.trim().equals("") ) continue;
			if ( locationOrSiguCode.contains(Location.CODE_DELIMETER) ) {
				if ( locationOrSiguCode.indexOf(Location.CODE_DELIMETER)==2 ) {						
					siguCodeList.add(locationOrSiguCode.substring(3, 8));
				} else {
					throw new Exception("포맷 오류 [" + locationOrSiguCode.indexOf(Location.CODE_DELIMETER) + "]");
				}					
			} else {
				siguCodeList.add(locationOrSiguCode);
			}
		}
		
		return siguCodeList;		
	}
	
	
	/* AddrJuso.java
	@Getter @Setter private Integer	jusoIdx;		//	검색 결과 임시 INDEX (검색 결과 전체에서 순번. JusoUtil에서 생성)  **TEMP**
	
				@Getter @Setter private	String	roadAddr;		//	전체 도로명주소	Y	String
				@Getter @Setter private	String	roadAddrPart1;	//	도로명주소(참고항목 제외)	Y	String
				@Getter @Setter private	String	roadAddrPart2;	//	도로명주소 참고항목	N	String
	@Getter @Setter private	String	jibunAddr;		//	지번주소	Y	String
				@Getter @Setter private	String	engAddr;		//	도로명주소(영문)	Y	String
	@Getter @Setter private	String	zipNo;			//	우편번호	Y	String
	@Getter @Setter private	String	admCd;			//	행정구역코드	Y	String					- 좌표검색 API 입력값
	@Getter @Setter private	String	rnMgtSn;		//	도로명코드	Y	String						- 좌표검색 API 입력값
				@Getter @Setter private	String	bdMgtSn;		//	건물관리번호	Y	String
				@Getter @Setter private	String	detBdNmList;	//	상세건물명	N	String
	@Getter @Setter private	String	bdNm;			//	건물명	N	String
				@Getter @Setter private	String	bdKdcd;			//	공동주택여부(1 : 공동주택, 0 : 비공동주택)	Y	String
				@Getter @Setter private	String	siNm;			//	시도명	Y	String
				@Getter @Setter private	String	sggNm;			//	시군구명	Y	String
	@Getter @Setter private	String	emdNm;			//	읍면동명	Y	String
				@Getter @Setter private	String	liNm;			//	법정리명	N	String
				@Getter @Setter private	String	rn;				//	도로명	Y	String
	@Getter @Setter private	String	udrtYn;			//	지하여부(0 : 지상, 1 : 지하)	Y	String	- 좌표검색 API 입력값
	@Getter @Setter private	String	buldMnnm;		//	건물본번	Y	Number						- 좌표검색 API 입력값
	@Getter @Setter private	String	buldSlno;		//	건물부번	Y	Number						- 좌표검색 API 입력값
				@Getter @Setter private	String	mtYn;			//	산여부(0 : 대지, 1 : 산)	Y	String
				@Getter @Setter private	String	lnbrMnnm;		//	지번본번(번지)	Y	Number
				@Getter @Setter private	String	lnbrSlno;		//	지번부번(호)	Y	Number
				@Getter @Setter private	String	emdNo;			//	읍면동일련번호	Y	String	
	*/
	
	
	
	
	
	
	
	
	
	
//	public static Location createLocationByLocationCode(String locationCode) throws Exception {
//		if ( locationCode == null || locationCode.trim().equals("") ) return null;
//		
//		Location loc = new Location();
//		if ( locationCode.indexOf(".")<0 ) {	// 시도인 경우.			
//			if ( locationCode.length() != 2 ) throw new Exception("Invalid Format. [" + locationCode + "]");
//			
//			loc.setLocationCode(locationCode.trim());
//			loc.setSidoCode(locationCode.trim());
//			
//		} else {								// 시구인 경우.
//			if ( locationCode.length() != 8 ) throw new Exception("Invalid Format. [" + locationCode + "]");
//			String[] token = locationCode.split("\\.");
//			if ( token[0].length() != 2 ) throw new Exception("Invalid Format. [" + locationCode + "]");
//			if ( token[1].length() != 5 ) throw new Exception("Invalid Format. [" + locationCode + "]");
//				
//			loc.setLocationCode(locationCode.trim());
//			loc.setSidoCode(token[0].trim());
//			loc.setSiguCode(token[1].trim());
//		}
//		
//		return loc;		
//	}
//	
//	public static Location createLocationBySidoCode(String sidoCode) throws Exception {
//		if ( sidoCode == null || sidoCode.trim().equals("") ) return null;
//		
//		sidoCode = sidoCode.trim();
//		if ( sidoCode.length() != 2 ) throw new Exception("Invalid Format. [" + sidoCode + "]");
//		
//		Location loc = new Location();
//		loc.setLocationCode(sidoCode);
//		loc.setSidoCode(sidoCode);
//		
//		return loc;
//	}
		
}
