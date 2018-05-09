package dentiq2.api.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import dentiq2.api.model.Hospital;
import dentiq2.api.model.JobAd;
import dentiq2.api.model.JobAttr;
import dentiq2.api.model.JobAttrGroup;
import dentiq2.api.model.JobSeekerUser;
import dentiq2.api.model.Location;
import dentiq2.api.model.LocationSummary;
import dentiq2.api.model.Notice;
import dentiq2.api.model.PaymentData;
import dentiq2.api.model.Resume;
import dentiq2.api.model.User;
import dentiq2.iamport.response.Payment;

/**
 * 
 * 
 * 
 * @author lee
 *
 */
@Mapper
public interface CommonMapper {
	
	@Select("select UNIX_TIMESTAMP()")
	public Long getUnixTimestamp() throws Exception;
	
	@Select("select * from LOCATION_CODE")
	public List<Location> listLocationCode() throws Exception;
	
	
//	/* 회원 업그레이드 결제 시작 */
//	public int startMembershipUpgradePayment(PaymentData paymentData) throws Exception;
//	
//	public int endMembershipUpgradePayment(PaymentArgument paymentArgument) throws Exception;
//	
//	@Select("select HOSPITAL_ID, HOSPITAL_EMAIL, BIZ_REG_NAME, MEMBERSHIP_UPDATE_TS, ANNUAL_MEMBERSHIP_START_YYYYMMDD, ANNUAL_MEMBERSHIP_END_YYYYMMDD from HOSPITAL where HOSPITAL_ID=#{hospitalId} and USE_YN='Y'")
//	public Map<String, String> getBuyerInfo(Long hospitalId) throws Exception;
//	
//	/*
//	update HOSPITAL set
//		ANNUAL_MEMBERSHIP_START_YYYYMMDD=date_format( date_add(curdate(), interval 1 year), '%Y%m%d'),
//		ANNUAL_MEMBERSHIP_END_YYYYMMDD=date_format(curdate(), '%Y%m%d'),
//		MEMBERSHIP_UPDATE_TS=current_timestamp()
//	where HOSPITAL_ID=	
//	 */
//	@Update(	"update HOSPITAL set"
//			+		" MEMBERSHIP_TYPE='2', "
//			+ 		" ANNUAL_MEMBERSHIP_START_YYYYMMDD=#{annualMembershipStartYyyymmdd}, "
//			+ 		" ANNUAL_MEMBERSHIP_END_YYYYMMDD=#{annualMembershipEndYyyymmdd}, "
//			+ 		" MEMBERSHIP_UPDATE_TS=current_timestamp() "
//			+	" where HOSPITAL_ID=#{hospitalId}"
//			)
//	public int updateAnnualMembership(@Param("hospitalId") Long hospitalId,
//									@Param("annualMembershipStartYyyymmdd") String annualMembershipStartYyyymmdd,
//									@Param("annualMembershipEndYyyymmdd") String annualMembershipEndYyyymmdd
//			) throws Exception;
//	
	
	
	
	/* 공고 업그레이드 */
	
	@Insert("insert PAYMENT(MERCHANT_UID, HOSPITAL_ID, JOB_AD_ID, PAYMENT_FOR, PG, PAY_METHOD, NAME, AMOUNT, CUSTOM_DATA, START_DATE, END_DATE, PERIOD) " + 
			"		values (#{merchantUid}, #{hospitalId}, #{jobAdId}, #{paymentFor}, #{pg}, #{payMethod}, #{name}, #{amount}, #{customData}, #{startDate}, #{endDate}, #{period})")
	public int startJobAdUpgradePayment(PaymentData paymentData) throws Exception;
	
	@Select("select * from PAYMENT where MERCHANT_UID=#{merchantUid}")
	public PaymentData getJobUpgradePayment(String merchantUid) throws Exception;	
	
	@Update("update PAYMENT set " + 
			"	STATUS=#{status}, TRX_END_TS=CURRENT_TIMESTAMP, " + 
			"	RES_JSON=#{resJson}, " + 
			"	FAIL_REASON=#{failReason}, " + 
			"	CARD_NAME=#{cardName}, APPLY_NUM=#{applyNum}, " + 
			"	IMP_UID=#{impUid}, PG_TID=#{pgTid} " + 
			"where " + 
			"	MERCHANT_UID=#{merchantUid} and AMOUNT=#{amount}")
	public int endJobAdUpgradePayment(Payment payment) throws Exception;
	
	
	@Update(	"update JOB_AD set "
			+		" AD_TYPE='2', "			// AD_TYPE은 여기서 넣지 않고 Batch가 하는 것이 정상이나, 당일(오늘) 바로 프리미어로 업그레이드하는 것이므로 AD_TYPE을 바로 변경해도 문제되지 않을 듯
			+		" PRIMIER_START_YYYYMMDD=#{primierStartYyyymmdd}, "
			+		" PRIMIER_END_YYYYMMDD=#{primierEndYyyymmdd}, "
			+		" PRIMIER_UPDATE_TS=current_timestamp() " 
			+	"where JOB_AD_ID=#{jobAdId}" )
	public int updateJobAdGrade(@Param("jobAdId") Long jobAdId, 
								@Param("primierStartYyyymmdd") String primierStartYyyymmdd,
								@Param("primierEndYyyymmdd") String primierEndYyyymmdd
			) throws Exception;
	
	
	
	
	
	/******** 공고 유형 배치 처리 *******/
	
	// 오늘 날짜보다 프리미어 광고 종료일이 이전이었다면, 프리미어 기간이 이미 끝난 것이므로, AD_TYPE을 '1'(일반 공고)로 변경한다.
	@Update("update JOB_AD set AD_TYPE='1' where AD_TYPE='2' and PRIMIER_END_YYYYMMDD<date_format(curdate(), '%Y%m%d')")
	public int updateJobAdTypeBatch() throws Exception;
	
//	@Update("update JOB_AD set AD_TYPE='2' where PRIMIER_START_YYYYMMDD<=date_format(curdate(), '%Y%m%d') and PRIMIER_END_YYYYMMDD>=date_format(curdate(), '%Y%m%d')")
//	public int updateJobAdTypeBatch2() throws Exception;
	
	// 활성상태(A) 공고 중에서 기간채용이고 공고 종료일이 오늘 날짜보다 이전이라면, 공고 게시 기간이 끝난 것이므로, AD_STATUS를 'E'(기간종료로 마감)으로 변경한다.
	@Update("update JOB_AD set AD_STATUS='E' where AD_STATUS='A' and HIRING_TERM_TYPE='2' and HIRING_END_DATE<date_format(curdate(), '%Y%m%d')")
	public int updateJobAdStatusBatch1() throws Exception;
	
	// 기간채용공고인데, 아직 공고 시작일이 도래하지 못해서 'P' 상태에 있던 것들을 ===> 오늘 날짜가 공고시작일과 공고종료일에 포함되면 AD_STATUS를 'A'(공고활성)으로 변경한다.
	@Update("update JOB_AD set AD_STATUS='A where AD_STATUS='P' and HIRING_TERM_TYPE='2' and HIRING_START_DATE<=date_format(curdate(), '%Y%m%d') and HIRING_END_DATE>=date_format(curdate(), '%Y%m%d')")
	public int updateJobAdStatusBatch2() throws Exception;
	
	
	// 오늘 날짜보다 연간회원 종료일이 이전이었다면, 연간회원 기간이 이미 끝난 것이므로, MEMBERSHIP_TYPE을 '1'(일반 회원)로 변경한다.
	@Update("update HOSPITAL set MEMBERSHIP_TYPE='1' where MEMBERSHIP_TYPE='2' and ANNUAL_MEMBERSHIP_END_YYYYMMDD<date_format(curdate(), '%Y%m%d')")
	public int updateHospitalMembershipTypeBatch() throws Exception;
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  공고 정보                          							          */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/* JOB_AD 테이블의 정보만 가져온다 */
	public JobAd getJobAdById(@Param("jobAdId") Long jobAdId) throws Exception;
	
	/* JOB_AD 테이블과 HOSPITAL 테이블의 전체 정보를 가져온다 */
	public JobAd getJobAdFullyById(@Param("jobAdId") Long jobAdId) throws Exception;
	
	/* JOB_AD 행을 생성한다. */
	public int createJobAd(JobAd jobAd) throws Exception;			// 변경된 행수
	
	/* JOB_AD 행을 변경한다. */
	public int updateJobAdBasic(JobAd jobAd) throws Exception; 		// 변경된 행수	
	
	/* JOB_AD 를 삭제 (실제는 USE_YN='N'으로 변경) */
	public int deleteJobAd(Long jobAdId) throws Exception;			// 변경된 행수
	
	/* 공고 속성이 변경될 때, 공고 속성 테이블(JOB_AD_ATTR)을 변경한다.
	 * 공고 속성 테이블은 해당 jobAdId에 대하여 기존 행들을 전부 delete 후, 새로운 내용을 전체 insert한다. */
	@Delete("delete from JOB_AD_ATTR where JOB_AD_ID=#{jobAdId}")
	public int deleteJobAdAttrAll(@Param("jobAdId") Long jobAdId) throws Exception;
	public int insertJobAdAttrAll(@Param("jobAdId") Long jobAdId, @Param("jobAttrList") List<JobAttr> jobAttrList) throws Exception;	// 생성하여 사용할 것이 아니므로, insertXXX 형태로 네이밍
	

	public List<LocationSummary> summaryWhole(@Param("jobAttrGroupList")	List<JobAttrGroup> jobAttrGroupList) throws Exception;
	
	public List<LocationSummary> summarySpecificSido(@Param("sidoCode") String sidoCode, @Param("jobAttrGroupList")	List<JobAttrGroup> jobAttrGroupList) throws Exception;
	
	public List<LocationSummary> summarySpecificSigu(@Param("siguCode") String siguCode, @Param("jobAttrGroupList")	List<JobAttrGroup> jobAttrGroupList) throws Exception;		// summarySpecificSigu 메소드 실행 시, 1개의 행만 나옴. 그러나, 이후의 사용편의성을 위하여 LIST로 반환함
		
	public List<LocationSummary> summarySpecificSiguListByLocationCode(@Param("locationCodeListAsSiguCode") List<String> locationCodeListAsSiguCode, @Param("jobAttrGroupList")	List<JobAttrGroup> jobAttrGroupList) throws Exception;
	
	
	/* JOB_AD_LIST_VIEW에서 게시판 형태로 조회한다. */
	public List<JobAd> listJobAd(
					@Param("sidoCodeList")		List<String> sidoCodeList,
					@Param("siguCodeList")		List<String> siguCodeList,
					
					@Param("jobAttrGroupList")	List<JobAttrGroup> jobAttrGroupList,
					@Param("adType")			String jobAdType,
					
					@Param("startIndexOnPage")	Integer startIndexOnPage,
					@Param("itemCntPerPage")	Integer itemCntPerPage
			) throws Exception;
	

	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  병원 정보                                                             */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/* 병원 정보를 가져온다 */
	public Hospital getHospitalByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;
	
	public Hospital getHospitalByHospitalUserId(@Param("userId") Long userId) throws Exception;
		
	public Hospital getHospitalBasicInfoByUserId(@Param("userId") Long userId) throws Exception;
	
	
	public int createHospital(Hospital hospital) throws Exception;
	
	public int updateHospital(Hospital hospital) throws Exception;
	
	@Update("update USER set HOSPITAL_ID=#{hospitalId} where USER_ID=#{hospitalUserId} and USE_YN='Y'")
	public int updateHospitalIdOfHospitalUser(@Param("hospitalUserId") Long hospitalUserId, @Param("hospitalId") Long hospitalId) throws Exception;
	
	// 로고 이미지를 업로드한 경우 상태값 변경
	@Update("update HOSPITAL set LOGO_IMAGE_YN=#{flag} where HOSPITAL_ID=#{hospitalId}")
	public int updateLogoImageYn(@Param("hospitalId") Long hospitalId, @Param("flag") String flag) throws Exception;
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                병원 회원 활동                                                          */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/* 특정 병원의 공고 목록을 조회 */
	public List<JobAd> listJobAdOfHospital(@Param("hospitalId") Long hospitalId) throws Exception;
	
	
	/** hr_management 관련 **/
	
	public int countResumeScrappedByHospitalIdAndResumeId(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;
	public int countResumeOfferedByHospitalIdAndResumeId(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;
	
	
	/* 특정 병원에 지원한 이력서들을 조회 */
	public List<Resume> listResumeAppliedByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;
	
	
	/* 병원이 제안(offer)한 이력서들 리스팅 */
	public List<Resume> listResumeOfferedByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;	
	
	/* 병원이 이력서(사용자)에 대하여 면접 제안 */
	@Insert("insert into HOSPITAL_OFFER (JOB_AD_ID, RESUME_ID) values (#{jobAdId}, #{resumeId})")
	public int addResumeOfferedByJobAdIdAndResumeId(@Param("jobAdId") Long jobAdId, @Param("resumeId") Long resumeId) throws Exception;
	
	
	/* 특정 병원이 스크랩한 이력서들을 조회 */
	public List<Resume> listResumeScrappedByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;
		
	/* 이력서를 스크랩 */
	@Insert("insert into HOSPITAL_SCRAPPED_RESUME (HOSPITAL_ID, RESUME_ID) values (#{hospitalId}, #{resumeId})")
	public int addResumeScrapped(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;
	
	/* 스크랩된 이력서를 스크랩 해제 */
	@Delete("delete from HOSPITAL_SCRAPPED_RESUME where HOSPITAL_ID=#{hospitalId} and RESUME_ID=#{resumeId}")
	public int deleteResumeScrapped(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;
	
	
	// 이력서 관련 정합성 Check
	// 특정 병원이 해당 공고를 소유하고 있는지 확인한다.
	@Select("select count(1) from JOB_AD where HOSPITAL_ID=#{hospitalId} and JOB_AD_ID=#{jobAdId}")
	public int hasCertainJobAdOfHospital(@Param("hospitalId") Long hospitalId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	
	
	/* 인재 추천 */
	public List<Resume> listResumeRecommended(@Param("hospitalId") Long hospitalId) throws Exception;
	
	
	
	// 인재 열람(이력서 열람) : Open된 이력서 + 현재 병원에 지원한 이력서(해당 이력서가 Open되지 않은 것일 수도 있으므로)
	// 주의 : 현재 버전에서는 현재 병원에 지원한 이력서는 포함 안함 (ONLY Open된 이력서만)
	public List<Resume> listResumeSearched(
									//@Param("hospitalId") Long hospitalId,		// 현재는 사용 안 함...
									@Param("startIndexOnPage")	Integer startIndexOnPage,
									@Param("itemCntPerPage")	Integer itemCntPerPage) throws Exception;
	public int countResumeSearched() throws Exception;
	
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  사용자 정보                                                           */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	
	public int createUser(User user) throws Exception;
	
	@Select("select SHA1(#{password})")
	public String encryptPassword(@Param("password") String password) throws Exception;
	
	@Update("update USER set PASSWORD=SHA1(#{tempPassword}), FORCED_CHANGE_PASSWORD_YN='Y', LAST_PASSWORD_CHANGED_TIMESTAMP=CURRENT_TIMESTAMP where EMAIL=#{email}")
	public int updatePasswordForReset(@Param("email") String email, @Param("tempPassword") String tempPassword) throws Exception;
	
	@Update("update USER set PASSWORD=SHA1(#{newPassword}),  FORCED_CHANGE_PASSWORD_YN='N', LAST_PASSWORD_CHANGED_TIMESTAMP=CURRENT_TIMESTAMP where EMAIL=#{email} and PASSWORD=SHA1(#{oldPassword})")
	public int updatePasswordForChange(@Param("email") String email, @Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword) throws Exception;
	
	@Select("select count(1) from USER where EMAIL=#{email} and PASSWORD=SHA1(#{password}) and USE_YN='Y'")
	public int countUserWithEmailAndPassword(@Param("email") String email, @Param("password") String password) throws Exception;
	
	@Select("select USER_ID, EMAIL, USER_TYPE, HOSPITAL_ID, KEEPING_LOGIN_TYPE, USE_YN from USER where USER_ID=#{userId} and USE_YN='Y'")
	public User getUserMinimalById(@Param("userId") Long userId) throws Exception;				// 세션 정보 생성 용도 (회원가입 직후, 회원가입결과 출력 및 세션 생성)
	
	@Select("select USER_ID, EMAIL, USER_TYPE, HOSPITAL_ID, USE_YN, PASSWORD as PASSWORD_ENCRYPTED, FORCED_CHANGE_PASSWORD_YN from USER where EMAIL=#{email} and USE_YN='Y'")
	public User getUserMinimalByEmail(@Param("email") String email) throws Exception;			// 세션 정보 생성 용도
	
		
	@Select("select count(1) from USER where EMAIL=#{email} and USE_YN='Y'")
	public int countUsersByEmail(@Param("email") String email) throws Exception;			// 회원가입 시 email 중복 확인
	
	@Select("select count(1) from USER where BIZ_REG_NO=#{bizRegNo}  and USE_YN='Y'")
	public int countUsersByBizRegNo(@Param("bizRegNo") String bizRegNo) throws Exception;	// 회원가입 시 사업자번호 중복 확인
	
		
	@Select("select SIGU_CODE from USER where USER_ID=#{userId}")
	public String getUserHomeSiguCode(@Param("userId") Long userId) throws Exception;		// 우리동네 조회 조건
	
	public JobSeekerUser getJobSeekerUserBasicInfo(@Param("userId") Long userId) throws Exception;	// 구직(개인)회원 사용자 기본 정보
	
	public JobSeekerUser getJobSeekerUserByUserId(@Param("userId") Long userId) throws Exception;	// 구직(개인)회원 사용자 정보
	
	public int updateJobSeekerUserBasicInfo(JobSeekerUser user) throws Exception;
	
	public Location getUserLocationByUserId(@Param("userId") Long userId) throws Exception;	// 사용자 주소 조회
	public int updateUserLocation(@Param("userId") Long userId, @Param("location") Location location) throws Exception;	// 사용자 주소 수정
	
	// 로고 이미지를 업로드한 경우 상태값 변경
	@Update("update USER set PROFILE_IMAGE_YN=#{flag} where USER_ID=#{userId}")
	public int updateProfileImageYn(@Param("userId") Long userId, @Param("flag") String flag) throws Exception;
	
	
	// user_setting.html용
	@Select("select AGREEMENT_NOTICE_NEWS_YN as agreementNoticeNewsYn, AGREEMENT_HIRING_NEWS_YN as agreementHiringNewsYn, "
			+ "AGREEMENT_EVENT_YN as agreementEventYn, AGREEMENT_AD_YN as agreementAdYn from USER where USER_ID=#{userId}")
	public Map<String, String> getUserSettings(@Param("userId") Long userId) throws Exception;	
	
	@Update("update USER set "
			+		"AGREEMENT_NOTICE_NEWS_YN=#{agreementNoticeNewsYn}, AGREEMENT_HIRING_NEWS_YN=#{agreementHiringNewsYn}, "
			+		"AGREEMENT_EVENT_YN=#{agreementEventYn}, AGREEMENT_AD_YN=#{agreementAdYn} "
			+"where USER_ID=#{userId}")
	public int updateUserSettings(@Param("userId") Long userId,
				@Param("agreementNoticeNewsYn") String agreementNoticeNewsYn, @Param("agreementHiringNewsYn") String agreementHiringNewsYn, 
				@Param("agreementEventYn") String agreementEventYn, @Param("agreementAdYn") String agreementAdYn) throws Exception;
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  이력서 정보                                                           */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	public Resume getResumeById(Long resumeId) throws Exception;
	
	public Resume getResumeByUserId(Long userId) throws Exception;
	
	public int createResume(Resume resume) throws Exception;
	
	public int updateResume(Resume resume) throws Exception;
	
	@Delete("delete from USER_RESUME_ATTR where RESUME_ID=#{resumeId}")
	public int deleteResumeAttrAll(@Param("resumeId") Long resumeId) throws Exception;
	public int insertResumeAttrAll(@Param("resumeId") Long userId, @Param("resumeAttrList") List<JobAttr> resumeAttrList) throws Exception;	// 생성하여 사용할 것이 아니므로, insertXXX 형태로 네이밍
	
	@Delete("delete from USER_RESUME_APPLY_LOCATION where RESUME_ID=#{resumeId}")
	public int deleteResumeLocationAll(@Param("resumeId") Long resumeId) throws Exception;
	public int insertResumeLocationAll(@Param("resumeId") Long resumeId, @Param("resumeApplyLocationCodeList") List<String> resumeApplyLocationCodeList) throws Exception;
	
	// 공고에 지원하기 위하여는, 이력서가 등록되어 있을 것이 선결조건임. 이를 확인하기 위함 ==> 구직자 활동에 정의되어 있음
	// @Select("select RESUME_ID from USER_RESUME where USER_ID=#{userId} and USE_YN='Y'")
	// public Long getResumeIdOfUser(@Param("userId") Long userId) throws Exception;
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  구직자 활동                                                           */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/** 관심 지역 **/
	@Select("select LOCATION_CODE from USER_INTEREST_LOCATION where USER_ID=#{userId}")		// 주의 : 라이브보드-관심지역에서 코드 조회 조건으로도 사용함
	public List<String> listInterestLocationCodeByUserId(@Param("userId") Long userId) throws Exception;
		
	@Delete("delete from USER_INTEREST_LOCATION where USER_ID=#{userId}")
	public int deleteInterestLocationAll(@Param("userId") Long userId) throws Exception;
	
	public int insertInterestLocationAll(@Param("userId") Long userId, @Param("locationCodeStrList") List<String> locationCodeStrList) throws Exception;	// 생성하여 사용할 것이 아니므로, insertXXX 형태로 네이밍
	
	
	
	/**************************************/
	@Select("select count(1) from USER_SCRAPPED_JOB_AD where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int countByScrappedJobAdId(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	@Select("select count(1) from USER_APPLY where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int countByAppliedJobAdId(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	@Select("select count(1) from USER_INTEREST_HOSPITAL H, JOB_AD J where J.HOSPITAL_ID=H.HOSPITAL_ID and H.USER_ID=#{userId} and J.JOB_AD_ID=#{jobAdId}")
	public int countByInterestHospitalJobAdId(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	/**************************************/
	
	
	/** 스크랩 **/
	public List<JobAd> listScrappedJobAd(@Param("userId") Long userId) throws Exception;
		
	@Delete("delete from USER_SCRAPPED_JOB_AD where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int deleteScrappedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	@Insert("insert into USER_SCRAPPED_JOB_AD(USER_ID, JOB_AD_ID) values (#{userId}, #{jobAdId})")
	public int insertScrappedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	
	
	/** 관심병원 **/
	public List<JobAd> listInterestHospitalJobAd(@Param("userId") Long userId) throws Exception;
	
	
	@Delete("delete from USER_INTEREST_HOSPITAL where USER_ID=#{userId} and HOSPITAL_ID=#{hospitalId}")
	public int deleteInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;
	
	@Insert("insert into USER_INTEREST_HOSPITAL(USER_ID, HOSPITAL_ID) values (#{userId}, #{hospitalId})")
	public int insertInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;	// 생성하여 사용할 것이 아니므로, insertXXX 형태로 네이밍
	
//	// 해당 병원이 사용자의 관심병원인지 검사한다.	주의 : 파라미터 순서는 USER_ID, HOSPITAL_ID
//	@Select("select count(1) from USER_INTEREST_HOSPITAL where USER_ID=#{userId} and HOSPITAL_ID=#{hospitalId}")
//	public int isJobSeekerSInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;
	
	
	
	/** 지원공고 **/
	public List<JobAd> listAppliedJobAd(@Param("userId") Long userId) throws Exception;
		
	@Insert("insert into USER_APPLY(USER_ID, JOB_AD_ID) values (#{userId}, #{jobAdId})")
	public int insertAppliedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
		
	@Delete("delete from USER_APPLY where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int deleteAppliedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;	// 지원 취소할 때 사용하기로 한다.
	
	// 공고에 지원하기 위하여는, 이력서가 등록되어 있을 것이 선결조건임. 이를 확인하기 위함
	@Select("select RESUME_ID from USER_RESUME where USER_ID=#{userId} and USE_YN='Y'")
	public Long getResumeIdOfUser(@Param("userId") Long userId) throws Exception;
	
	
	/** (병원이 한) 면접 요청된 공고 목록 보기 **/
	public List<JobAd> listOfferedJobAdByUserId(@Param("userId") Long userId) throws Exception;
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  	기타                                                              */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	@Select("select * from NOTICE where USE_YN='Y'")
	public List<Notice> listNoticeTitle() throws Exception;
	
	@Insert("insert into NOTICE(TITLE, CONTENT, WEB_URL) values (#{title}, #{content}, #{notice})")
	public Notice createNotice(Notice notice) throws Exception;
	
	@Update("update NOTICE set USE_YN='N' where NOTICE_ID=#{noticeId}")
	public void deleteNotice(@Param("noticeId") Long noticeId) throws Exception;
	
	@Update("update NOTICE set NOTICE_TYPE=#{noticeType}, TITLE=#{title}, CONTENT=#{content}, WEB_URL=#{webUrl} where NOTICE_ID=#{noticeId}")
	public void updateNotice(Notice notice) throws Exception;
}
