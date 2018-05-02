package dentiq2.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


import dentiq2.api.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * (구인) 병원 정보
 * @author lee
 *
 */
@JsonInclude(Include.NON_NULL)
public class Hospital {
	
	public static final String MEMBERSHIP_NORMAL = "1";		// 일반회원
	public static final String MEMBERSHIP_ANNUAL = "2";		// 연간회원
	
	@Getter @Setter private Location location;
	
		
	@Getter @Setter private Long hospitalId;					// 병원 ID
	@Getter @Setter private String bizRegNo;					// 사업자등록번호	
	@Getter @Setter private String bizRegName;					// 사업자등록상 사업장명
	@Getter @Setter private String yKiho;						// 암호화 요양기관코드	Y_KIHO
	@Getter @Setter private String membershipType;				// 회원유형(1:무료회원, 2:연간회원)	

	@Getter @Setter private Long userId;						// 사용자 ID			USER_ID
	@Getter @Setter private String userEmail;
	
	@Getter @Setter private String hospitalName;				// 병원명				HOSPITAL_NAME	
		
	//******************* 심평원 데이터 START	
	@Getter @Setter private String clCode;						// 종별코드	
	@Getter @Setter private String clCodeName;					// 종별코드명
	//******************* 심평원 데이터 END
		
	
	@Getter @Setter private String hospitalTelNo;						// 전화번호			TEL_NO	
	@Getter @Setter private String hospitalUrl;					// 홈페이지 url		HOSPITAL_URL	
	@Getter @Setter private String hospitalEmail;				// 병원 대표 email
	@Getter @Setter private String ceoName;						// 대표자명
	@Getter @Setter private String estDate;						// 설립일자			EST_DATE
	
	//******************* 심평원 데이터 START
	@Getter @Setter private Integer tdrCnt;						// 총의사 수
	@Getter @Setter private Integer gdrCnt;
	@Getter @Setter private Integer idrCnt;
	@Getter @Setter private Integer rdrCnt;
	@Getter @Setter private Integer sdrCnt;	
	//******************* 심평원 데이터 END
	
	
	@Getter @Setter private String useYn;						// 사용여부			USE_YN
	
		
	
	@Getter @Setter private String hospitalTypeCode;			// 병원 유형 코드
	@Getter @Setter private String hospitalTypeText;			// 병원 유형 텍스트
	
	
	@Getter @Setter private List<String> holiday;				// 휴일정보 유형(checkbox에서 선택)	==> holidayJson와 연결됨	
		@JsonIgnore public String getHolidayJson() throws Exception			{	return JsonUtil.toJson(this.holiday);	}
		@JsonIgnore public void setHolidayJson(String json) throws Exception	{	this.holiday = JsonUtil.<List<String>>toGenericObject(json); }
		
	@Getter @Setter private String holidayText;					// 휴일정보 - 기타/추가
	
	@Getter @Setter private String workingTimeWeekdayStart;		// 진료시간-평일 : 시작	
	@Getter @Setter private String workingTimeWeekdayEnd;		// 진료시간-평일 : 종료	
	@Getter @Setter private String workingTimeWeekendStart;		// 진료시간-휴일 : 시작	
	@Getter @Setter private String workingTimeWeekendEnd;		// 진료시간-휴일 : 종료	
	@Getter @Setter private String workingTimeText;				// 진료시간 : 기타/추가
	
	@Getter @Setter private String lunchTimeStart;				// 점심시간 : 시작	
	@Getter @Setter private String lunchTimeEnd;				// 점심시간 : 시작
	
	@Getter @Setter private String clinicSubject;				// 진료과목
	@Getter @Setter private String totalMemberCnt;				// 총구성원 수	(문자형식으로 사용될 수도 있다. 예:) 0명, 무관, 00명 등
	
		
	@Getter @Setter private String defaultHrTelNo;				// 디폴트 채용문의 전화번호	
	@Getter @Setter private String defaultHrOfficerName;		// 디폴트 채용담당자 이름	
	@Getter @Setter private String defaultHrEmail;				// 디폴트 채용이메일 주소
	
	
	@Getter private Boolean hasLogoImage;
		@JsonIgnore public void setLogoImageYn(String flag) {
			if ( flag !=null && flag.equals("Y") ) hasLogoImage = true;
			else hasLogoImage = null;
		}
	
	// 위치/교통정보 필요함
	
	
	
	
	//@Getter @Setter private Long interestedByUserId;	// 특정 USER_ID가 관심병원으로 지정한지 여부 (jobad.html에서 관심병원인 것을 표시해주기 위하여 사용함). @see /jobAd/{jobAdId}/detail/
	
	/** 민감 정보 필터링 */
	public void filter() {
		this.bizRegNo	= "**FILTERED**";
		this.bizRegName	= "**FILTERED**";
	}
		
	/*
	@Getter @Setter private String bizRegNo;
	@Getter @Setter private String bizRegName;
	@Getter @Setter private String yKiho;
	@Getter @Setter private String membershipType;
	@Getter @Setter private String name;
	@Getter @Setter private String clCode;
	@Getter @Setter private String clCodeName;
	
	@Getter @Setter private String telNo;
	@Getter @Setter private String hospUrl;
	@Getter @Setter private String hospEmail;
	@Getter @Setter private String ceoName;
	@Getter @Setter private String estDate;
	@Getter @Setter private String tdrCnt;
	@Getter @Setter private String gdrCnt;
	@Getter @Setter private String idrCnt;
	@Getter @Setter private String rdrCnt;
	@Getter @Setter private String sdrCnt;
	
	
	
	
	@Getter @Setter private String hospitalTypeCode;			// 병원 유형 코드
	@Getter @Setter private String hospitalTypeText;			// 병원 유형 텍스트
	
	
	//@Getter @Setter private SingleColumnList holiday;				// 휴일정보 유형(checkbox에서 선택)	==> holiday value와 연결됨	
	@Getter @Setter private List<String> holiday;				// 휴일정보 유형(checkbox에서 선택)	==> holidayJson와 연결됨	
		@JsonIgnore public String getHolidayJson() throws Exception			{	return JsonUtil.toJson(this.holiday);	}
		@JsonIgnore public void setHolidayJson(String json) throws Exception	{	this.holiday = JsonUtil.<List<String>>toGenericObject(json); }
	@Getter @Setter private String holidayText;					// 휴일정보 - 기타/추가
	
	@Getter @Setter private String workingTimeWeekdayStart;		// 진료시간-평일 : 시작	
	@Getter @Setter private String workingTimeWeekdayEnd;		// 진료시간-평일 : 종료	
	@Getter @Setter private String workingTimeWeekendStart;		// 진료시간-휴일 : 시작	
	@Getter @Setter private String workingTimeWeekendEnd;		// 진료시간-휴일 : 종료	
	@Getter @Setter private String workingTimeText;				// 진료시간 : 기타/추가
	
	@Getter @Setter private String lunchTimeStart;				// 점심시간 : 시작	
	@Getter @Setter private String lunchTimeEnd;				// 점심시간 : 시작
	
	@Getter @Setter private String totalMemberCnt;				// 총구성원 수	(문자형식으로 사용될 수도 있다. 예:) 0명, 무관, 00명 등
	@Getter @Setter private String clinicSubject;				// 진료과목
	
	
	@Getter @Setter private String defaultHrTelNo;				// 디폴트 채용문의 전화번호	
	@Getter @Setter private String defaultHrOfficerName;		// 디폴트 채용담당자 이름	
	@Getter @Setter private String defaultHrEmail;				// 디폴트 채용이메일 주소
	*/
}
