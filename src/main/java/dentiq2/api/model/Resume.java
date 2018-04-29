package dentiq2.api.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.util.DateUtil;
import dentiq2.api.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class Resume {
	
	@Getter @Setter private Long resumeId;
	
	
	@Getter @Setter private JobSeekerUser user;
	@Getter @Setter private Long userId;
	
	
	
	@Getter @Setter private String title;	// 이력서 제목
	
	
	
	// 고용형태, 채용부문, 담당업무
	@Getter @Setter private List<String> attr;
		@JsonIgnore public String getAttrJson() throws Exception {				return JsonUtil.toJson(this.attr); }
		@JsonIgnore public void setAttrJson(String json) throws Exception {		this.attr = JsonUtil.<List<String>>toGenericObject(json); }
	
	
	@Getter @Setter private String salaryWanted;
	@Getter @Setter private String salaryWantedText;
	@Getter @Setter private String roleWanted;
	@Getter @Setter private String roleWantedText;
	
	
	@Getter @Setter private List<String> applyLocationCode;
		@JsonIgnore public String getApplyLocationCodeJson() throws Exception			{ return JsonUtil.toJson(this.applyLocationCode); }
		@JsonIgnore public void setApplyLocationCodeJson(String json) throws Exception	{ this.applyLocationCode = JsonUtil.<List<String>>toGenericObject(json); }
	
	
	@Getter @Setter private String eduLevel;	
	@Getter @Setter private String eduStatus;	
	@Getter @Setter private String eduSchoolName;
	@Getter @Setter private String eduMajor;
	@Getter @Setter private String eduStartYYYYMM;
	@Getter @Setter private String eduEndYYYYMM;
	
	@Getter private String totalCareerLength;		// 경력 기간 (년 단위, 1년 미만==>'1년 미만', 이후에는 'x년 이상')
	@Getter @Setter private List<Map<String, String>> career;	// firmName, task, joinYYYYMM, retireYYYYMM
		@JsonIgnore public String getCareerJson() throws Exception	{ return JsonUtil.toJson(this.career); }
		@JsonIgnore public void setCareerJson(String json) throws Exception {
			this.career = JsonUtil.<List<Map<String, String>>>toGenericObject(json);
			
			// 2018.03.26 이주현 추가. 경력 기간 표시 추가
			if ( this.career == null || this.career.size()<1 ) {
				this.totalCareerLength = "신입";
				return;
			}
			
			long diffMonths = 0;
			boolean inOffice = false;
			for (Map<String, String> map : this.career ) {
				String join   = map.get("joinYYYYMM");
				String retire = map.get("retireYYYYMM");
				
				if ( join != null && !join.trim().equals("") ) {
					if ( retire != null && !retire.trim().equals("") ) {
						diffMonths += DateUtil.diffMonths(join, retire);
					} else {		// 퇴직일이 입력되지 않았으면, 재직중(현재날짜까지)으로 판단
						
						if ( inOffice ) throw new Exception("경력기간에 재직중은 1개 이상이 될 수 없음");
	
						diffMonths += DateUtil.diffMonths(join, DateUtil.todayYYYYMM());
						inOffice = true;
					}
				}
			}
			
			if ( diffMonths < 12 ) this.totalCareerLength = "1년 미만";
			else {
				int years = (int) (diffMonths/12);
				this.totalCareerLength = years + "년 이상";
			}
		}
	
	@Getter @Setter private List<Map<String, String>> license;	// name, issuer, issueYYYYMM	
		@JsonIgnore public String getLicenseJson() throws Exception				{ return JsonUtil.toJson(this.license); }
		@JsonIgnore public void setLicenseJson(String json) throws Exception	{ this.license = JsonUtil.<List<Map<String, String>>>toGenericObject(json); }
	
	//@Getter @Setter private String confirmRecommendation;		// Y: 입사제안 받기, N:입사제안 받지 않기
	
	@Getter @Setter private String content;	// 내용
	
	@Getter @Setter private Date lastModDt;
	public String getLastModYYYYMMDD() {
		if ( this.lastModDt == null ) return null;
		else return DateUtil.parseToYYYYMMDD(this.lastModDt);
	}
	
	@Getter @Setter private Date regDt;
	public String getRegYYYYMMDD() {
		if ( this.regDt == null ) return null;
		else return DateUtil.parseToYYYYMMDD(this.regDt);
	}
	
	
	@Getter @Setter private String openYn;		// 이력서 공개 여부
	public boolean isOpened() {
		if ( this.openYn != null && this.openYn.equals("Y") )	return true;
		else return false;
	}
	
	
	
	/******************************************************************************************************************/
		
	
	
	@Getter @Setter private Date appliedDt;	// 이력서를 가지고 특정 공고에 지원한 경우에, 지원일자
	public String getAppliedYYYYMMDD() {
		if ( this.appliedDt == null ) return null;
		else return DateUtil.parseToYYYYMMDD(this.appliedDt);
	}
	@Getter @Setter private Long appliedJobAdId;	// 이력서를 가지고 특정 공고에 지원한 경우에, 지원한 공고 ID
	
	//@Getter @Setter public Long jobAdIdInViewOfHospital;	// 병원에서 이력서를 조회할 경우에, 해당 이력서가 어떠한 공고에 지원했는지 알기 위한, 공고의 ID
	
	
	// 병원이 이력서들을 조회할 때, 특정 병원에서 스크랩된 이력서인지 여부 (hr_management에서 사용)
	// JOB_AD의 resultMap에는 없고, 필요한 경우(병원이 이력서들을 조회하는 경우)에 Service(or Controller)에서 추가해준다.
	@Getter @Setter private Boolean scrappedByCertainHospital;

	// 병원이 이력서들을 조회할 때, 구직자에게 면접제안할 경우 이미 면접제안이 되었는지를 파악하기 위한 플래그.
	// JOB_AD resultMap에는 없고, 필요한 경우(병원이 이력서들을 조회하는 경우)에 Service(or Controller)에서 추가해준다.
	@Getter @Setter private Boolean offeredByCertainHospital;

}
