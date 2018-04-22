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
import dentiq2.api.model.Resume;
import dentiq2.api.model.User;

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
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  공고 정보                                                             */
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
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                병원 회원 활동                                                          */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/* 특정 병원의 공고 목록을 조회 */
	public List<JobAd> listJobAdOfHospital(@Param("hospitalId") Long hospitalId) throws Exception;
	
	/* 특정 병원에 지원한 이력서들을 조회 */
	public List<Resume> listResumeApplied(@Param("hospitalId") Long hospitalId) throws Exception;
	
	/* 특정 병원이 스크랩한 이력서들을 조회 */
	public List<Resume> listResumeScrapped(@Param("hospitalId") Long hospitalId) throws Exception;
	
	@Select("select RESUME_ID from HOSPITAL_SCRAPPED_RESUME where HOSPITAL_ID=#{hospitalId}")
	public List<Long> listResumeIdScrapped(@Param("hospitalId") Long hospitalId) throws Exception;
	
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
	
//	/* 병원이 제안(offer)한 이력서들 리스팅 */
//	public List<Resume> listResumeOfferedByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;
	
	/* 병원이 제안(offer)한 이력서 ID들 리스팅 */
	// TODO 중요!!! : 해당 공고가 ACTIVE한 것인지 확인해야 한다.
	@Select("select O.JOB_AD_ID as jobAdId, O.RESUME_ID as resumeId from HOSPITAL_OFFER O, JOB_AD J where O.JOB_AD_ID=J.JOB_AD_ID and J.HOSPITAL_ID=#{hospitalId} and J.USE_YN='Y'")
	public List<Map<String, Long>> listResumeOfferedIdAndJobAdIdByHospitalId(@Param("hospitalId") Long hospitalId) throws Exception;
	
	/* 병원이 이력서(사용자)에 대하여 면접 제안 */
	@Insert("insert into HOSPITAL_OFFER (JOB_AD_ID, RESUME_ID) values (#{jobAdId}, #{resumeId})")
	public int addResumeOfferedByJobAdIdAndResumeId(@Param("jobAdId") Long jobAdId, @Param("resumeId") Long resumeId) throws Exception;
	
//	/* 병원이 이력서(사용자)에 대하여 면접 제안한 것 취소 */
//	@Delete("delete from HOSPITAL_OFFER where JOB_AD_ID=#{jobAdId} and RESUME_ID=#{resumeId}")
//	public int deleteResumeOfferedByByJobAdIdAndResumeId(@Param("jobAdId") Long jobAdId, @Param("resumeId") Long resumeId) throws Exception;
	


// 기존 코드 
//
//		/* 병원이 제안(offer)한 이력서들 리스팅 */
//		public List<Resume> listOfferedResume(@Param("hospitalId") Long hospitalId) throws Exception;
//		
//		/* 병원이 제안(offer)한 이력서 ID들 리스팅 */
//		@Select("select RESUME_ID from HOSPITAL_OFFER where HOSPITAL_ID=#{hospitalId}")
//		public List<Long> listOfferedResumeId(@Param("hospitalId") Long hospitalId) throws Exception;
//		
//		/* 병원이 이력서(사용자)에 대하여 면접 제안 */
//		@Insert("insert into HOSPITAL_OFFER (HOSPITAL_ID, RESUME_ID) values (#{hospitalId}, #{resumeId})")
//		public int addResumeOffered(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;
//		
//		/* 병원이 이력서(사용자)에 대하여 면접 제안한 것 취소 */
//		@Delete("delete from HOSPITAL_OFFER where HOSPITAL_ID=#{hospitalId} and RESUME_ID=#{resumeId}")
//		public int deleteResumeOffered(@Param("hospitalId") Long hospitalId, @Param("resumeId") Long resumeId) throws Exception;

	
	
	/* 인재 추천 */
	public List<Resume> listResumeRecommended(@Param("hospitalId") Long hospitalId) throws Exception;
	
	
	
	
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  사용자 정보                                                           */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	
	public int createUser(User user) throws Exception;
	
	@Select("select SHA1(#{password})")
	public String encryptPassword(@Param("password") String password) throws Exception;
	
	
	@Select("select USER_ID, EMAIL, USER_TYPE, HOSPITAL_ID, KEEPING_LOGIN_TYPE, USE_YN from USER where USER_ID=#{userId} and USE_YN='Y'")
	public User getUserMinimalById(@Param("userId") Long userId) throws Exception;				// 세션 정보 생성 용도 (회원가입 직후, 회원가입결과 출력 및 세션 생성)
	
	@Select("select USER_ID, EMAIL, USER_TYPE, HOSPITAL_ID, USE_YN, PASSWORD as PASSWORD_ENCRYPTED from USER where EMAIL=#{email}")
	public User getUserMinimalByEmail(@Param("email") String email) throws Exception;			// 세션 정보 생성 용도
	
		
	@Select("select count(1) USER where EMAIL=#{email}")
	public int countUsersByEmail(@Param("email") String email) throws Exception;			// 회원가입 시 email 중복 확인
	
	@Select("select count(1) from USER where BIZ_REG_NO=#{bizRegNo}")
	public int countUsersByBizRegNo(@Param("bizRegNo") String bizRegNo) throws Exception;	// 회원가입 시 사업자번호 중복 확인
	
		
	@Select("select SIGU_CODE from USER where USER_ID=#{userId}")
	public String getUserHomeSiguCode(@Param("userId") Long userId) throws Exception;		// 우리동네 조회 조건
	
	
	
	public JobSeekerUser getJobSeekerUserByUserId(@Param("userId") Long userId) throws Exception;				// 구직(개인)회원 사용자 정보
	
	public int updateJobSeekerUserBasicInfo(JobSeekerUser user) throws Exception;
	
	public Location getUserLocationByUserId(@Param("userId") Long userId) throws Exception;	// 사용자 주소 조회
	public int updateUserLocation(@Param("userId") Long userId, @Param("location") Location location) throws Exception;	// 사용자 주소 수정
	
	
	
	
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
	
	
	/** 스크랩 **/
	public List<JobAd> listScrappedJobAd(@Param("userId") Long userId) throws Exception;
	
	@Select("select JOB_AD_ID from USER_SCRAPPED_JOB_AD where USER_ID=#{userId}")
	public List<Long> listScrappedJobAdId(@Param("userId") Long userId) throws Exception;
	
	@Delete("delete from USER_SCRAPPED_JOB_AD where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int deleteScrappedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	@Insert("insert into USER_SCRAPPED_JOB_AD(USER_ID, JOB_AD_ID) values (#{userId}, #{jobAdId})")
	public int insertScrappedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
	
	
	
	/** 관심병원 **/
	public List<JobAd> listInterestHospitalJobAd(@Param("userId") Long userId) throws Exception;
	
	@Select("select HOSPITAL_ID from USER_INTEREST_HOSPITAL where USER_ID=#{userId}")
	public List<Long> listInterestHospitalId(@Param("userId") Long userId) throws Exception;
	
	@Delete("delete from USER_INTEREST_HOSPITAL where USER_ID=#{userId} and HOSPITAL_ID=#{hospitalId}")
	public int deleteInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;
	
	@Insert("insert into USER_INTEREST_HOSPITAL(USER_ID, HOSPITAL_ID) values (#{userId}, #{hospitalId})")
	public int insertInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;	// 생성하여 사용할 것이 아니므로, insertXXX 형태로 네이밍
	
	// 해당 병원이 사용자의 관심병원인지 검사한다.	주의 : 파라미터 순서는 USER_ID, HOSPITAL_ID
	@Select("select count(1) from USER_INTEREST_HOSPITAL where USER_ID=#{userId} and HOSPITAL_ID=#{hospitalId}")
	public int isJobSeekerSInterestHospital(@Param("userId") Long userId, @Param("hospitalId") Long hospitalId) throws Exception;
	
	
	
	/** 지원공고 **/
	public List<JobAd> listAppliedJobAd(@Param("userId") Long userId) throws Exception;
	
	@Select("select JOB_AD_ID from USER_APPLY where USER_ID=#{userId}")
	public List<Long> listAppliedJobAdId(@Param("userId") Long userId) throws Exception;
	
	@Insert("insert into USER_APPLY(USER_ID, JOB_AD_ID) values (#{userId}, #{jobAdId})")
	public int insertAppliedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;
		
	@Delete("delete from USER_APPLY where USER_ID=#{userId} and JOB_AD_ID=#{jobAdId}")
	public int deleteAppliedJobAd(@Param("userId") Long userId, @Param("jobAdId") Long jobAdId) throws Exception;	// 지원 취소할 때 사용하기로 한다.
	
	// 공고에 지원하기 위하여는, 이력서가 등록되어 있을 것이 선결조건임. 이를 확인하기 위함
	@Select("select RESUME_ID from USER_RESUME where USER_ID=#{userId} and USE_YN='Y'")
	public Long getResumeIdOfUser(@Param("userId") Long userId) throws Exception;
	
	
	/** (병원이 한) 면접 요청된 공고 목록 보기 **/
	public List<JobAd> listOfferedJobAdByUserId(@Param("userId") Long userId) throws Exception;
	
	
	
	
	
	
	
	
	
}
