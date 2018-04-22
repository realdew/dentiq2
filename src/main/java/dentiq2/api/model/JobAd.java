package dentiq2.api.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.util.DateUtil;
import dentiq2.api.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class JobAd {
	
	public static final String AD_STATUS_ACTIVE	= "A";	// 공고 상태 : 활성
	public static final String AD_STATUS_END	= "E";	// 공고 상태 : 종료
	
	public static final String AD_TYPE_NORMAL	= "1";	// 일반 공고 : 1
	public static final String AD_TYPE_PREMIERE = "2";	// 프리미어 공고 : 2
	
	
	
	@Getter @Setter private Hospital hospital;
	
	// ------------------------------
	
	
	@Getter @Setter private Long	jobAdId;
	@Getter @Setter private String	adStatus;					// 공고상태 (공고상태(A:활성, E:마감) 마감은 접수종료일 전 수동 마감인 경우)
	
	@Getter @Setter private String	adType;						// 공고 유형(1:일반, 2:프리미어)	
		// WEB의 편의성 지원 메소드 (ex. Handlebars.js 등에서는 값을 사용한 if문이 지원되지 않음(true/false만 가능))
		public boolean isAdTypeNormal()		{	if ( this.adType != null && this.adType.equals(AD_TYPE_NORMAL) ) return true; else return false;	}
		public boolean isAdTypePremiere()	{	if ( this.adType != null && this.adType.equals(AD_TYPE_PREMIERE) ) return true; else return false;	}
	
	@Getter @Setter private Long	hospitalId;
	
	@Getter @Setter private String	title;						// 공고 제목
	
	@Getter @Setter private String	adRegDate;
	@Getter @Setter private String	adRegTime;
	@Getter @Setter private String	adOpenDate;
	@Getter @Setter private String	adOpenTime;
	@Getter @Setter private String	adCloseDate;
	@Getter @Setter private String	adCloseTime;
	
	@Getter @Setter private String	hiringTermType;				// 1:상시채용 2:기간채용
	@Getter @Setter private String	hiringStartDate;
	@Getter @Setter private String	hiringStartTime;
	@Getter @Setter private String	hiringEndDate;
	@Getter @Setter private String	hiringEndTime;
	
	public String getHiringStartDateDay() {
		if ( hiringTermType == null || !hiringTermType.trim().equals("2") ) return "";
		
		String dayStr = "";
		try {	dayStr = DateUtil.getDayOfDate(hiringStartDate);		} catch(Exception ignore) {}		
		return dayStr;
	}
	public String getHiringEndDateDay() {
		if ( hiringTermType == null || !hiringTermType.trim().equals("2") ) return "";
		
		String dayStr = "";
		try {	dayStr = DateUtil.getDayOfDate(hiringEndDate);		} catch(Exception ignore) {}		
		return dayStr;
	}
	
	
	
	////@Getter @Setter private SingleColumnList applyWay;
	@Getter @Setter private List<String> applyWay;				// 지원방법
		@JsonIgnore public String getApplyWayJson() throws Exception			{	return JsonUtil.toJson(this.applyWay);	}
		@JsonIgnore public void setApplyWayJson(String json) throws Exception	{	this.applyWay = JsonUtil.<List<String>>toGenericObject(json); }
	
	
	@Getter @Setter private String workDesc;
	
	@Getter @Setter private String hiringCnt;						// 00명 이런식으로도 입력하므로, 문자열 
	@Getter @Setter private String salaryType;
	@Getter @Setter private String salaryText;
	
	@Getter @Setter private String hiringRoleType;
	@Getter @Setter private String hiringRoleText;
	
	@Getter @Setter private String eduLevel;
	@Getter @Setter private String expType;
	@Getter @Setter private String licenseText;
	
	@Getter @Setter private String jobWorkingDay;
	@Getter @Setter private String jobWorkingTimeWeekdayStart;
	@Getter @Setter private String jobWorkingTimeWeekdayEnd;
	@Getter @Setter private String jobWorkingTimeWeekendStart;
	@Getter @Setter private String jobWorkingTimeWeekendEnd;
	@Getter @Setter private String jobWorkingTimeText;
	
	@Getter @Setter private String jobLunchTimeStart;
	@Getter @Setter private String jobLunchTimeEnd;
	
	@Getter @Setter private String hrOfficerName;
	@Getter @Setter private String hrTelNo;
	@Getter @Setter private String hrEmail;
	
	@Getter @Setter private String retirementPayType;
	@Getter @Setter private String retirementPay;
	
	@Getter @Setter private String useYn;
	
	@Getter @Setter private List<String> attr;	// Client에서 입력된 것 그대로. 예: EMP.1, EMP.2, TASK.1
		@JsonIgnore public void setAttrJson(String jsonColumnStringValue) throws Exception	{	this.attr = JsonUtil.<List<String>>toGenericObject(jsonColumnStringValue);	}
		@JsonIgnore public String getAttrJson() throws Exception							{	return JsonUtil.toJson(this.attr);	}

	@Getter @Setter private List<String> hashTag;
		@JsonIgnore public String getHashTagJson() throws Exception				{	return JsonUtil.toJson(this.hashTag);	}
		@JsonIgnore public void setHashTagJson(String json) throws Exception	{	this.hashTag = JsonUtil.<List<String>>toGenericObject(json); }
	

	
		
		
		
	// 구직자가 지원한 공고를 보는 경우에, 공고지원일자
	//TODO Date 형식이 문제를 일으킬 수 있으므로, 변환이 필요할 것으로 생각됨
	@Getter @Setter private Date applyDt;
	
	// 구직자가 지원한 공고를 보는 경우(user_interaction.html)에, 이 공고가 어느 병원인가에 '지원'한 공고인지를 알려주는 FLAG
	@Getter @Setter private Boolean appliedByCertainJobSeeker;
	
	
	// 병원이 구직자에게 면접제안을 한 경우, 구직자가 이러한 공고들을 조회하는 경우에 사용되는 면접제안일자
	@Getter @Setter private Date offerDt;
	
		
	
	
	
	
	// 상세공고에서 사용하기 위한 D-Day
	public Long getDDay() {
		if ( this.hiringEndDate==null || this.hiringEndDate.length() != 8 ) {
			return new Long(-1);
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	        Date beginDate = new Date();	//TODO DB에서 가져와야 하지 않을까?
			//Date beginDate = formatter.parse("20180311");
	        Date endDate = formatter.parse(hiringEndDate);
	        
	        long diff = endDate.getTime() - beginDate.getTime();
	        long diffDays = diff / (24*60*60*1000);
	        
	        return diffDays;
	        
		} catch(Exception ignore) {
			return new Long(-9);
		}
        
	}
	
	
	
}


