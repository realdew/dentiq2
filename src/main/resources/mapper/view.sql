create or replace view JOB_AD_LIST_VIEW as
select
	J.JOB_AD_ID,
	J.AD_STATUS,
	J.AD_TYPE,
	J.HOSPITAL_ID,
	J.TITLE,
	
	J.HIRING_TERM_TYPE,
	J.HIRING_START_DATE,
	J.HIRING_START_TIME,
	J.HIRING_END_DATE,
	J.HIRING_END_TIME,
	
	J.APPLY_WAY_JSON,
	
	J.HIRING_CNT,
	
	J.HIRING_ROLE_TYPE,
	J.HIRING_ROLE_TEXT,
	
	J.EXP_TYPE,
	
	J.HR_TEL_NO,
	J.HR_EMAIL,
	
	J.ATTR_JSON,
	J.HASH_TAG_JSON,
	
	H.LOCATION_CODE,
	H.SIDO_CODE,
	H.SIGU_CODE,
	
	H.HOSPITAL_NAME,
	
	H.HOSPITAL_TEL_NO,
	H.HOSPITAL_URL,
	H.HOSPITAL_EMAIL,
	
	H.ENT_X,
	H.ENT_Y,
	H.LAT_LON_X,
	H.LAT_LON_Y,
	H.UTMK_POINT,
	
	H.LOGO_IMAGE_YN
	

from
	JOB_AD J,
	HOSPITAL H
where
	J.HOSPITAL_ID = H.HOSPITAL_ID and
	(
		( HIRING_TERM_TYPE='2' and J.HIRING_START_DATE<=DATE_FORMAT(CURDATE(), '%Y%m%d') and J.HIRING_END_DATE>=DATE_FORMAT(CURDATE(), '%Y%m%d') )
		or
		HIRING_TERM_TYPE='1'
	) and
	
	H.USE_YN='Y' and 
	J.USE_YN='Y'and
	J.AD_STATUS='A'
	-- and HIRING_END_DATE와 HIRING_END_TIME이 아직 도래하지 않은 것을 찾는다.
	-- and AD_STATUS가 'A'인 것을 찾는다.
	-- and 병원 USE_YN='Y', 공고 USE_YN='Y'

	
	
create or replace view USER_RESUME_VIEW as
select
	U.EMAIL AS EMAIL,  U.NAME AS NAME,  U.REAL_NAME AS REAL_NAME,
	U.LOCATION_CODE AS LOCATION_CODE, U.SIDO_CODE AS SIDO_CODE, U.SIGU_CODE AS SIGU_CODE,
	U.ADDR_MAIN AS ADDR_MAIN, U.ADDR_DETAIL AS ADDR_DETAIL, U.JIBUN_ADDR AS JIBUN_ADDR,
	U.TEL_NO AS TEL_NO, U.BIRTHDAY AS BIRTHDAY, U.GENDER AS GENDER,
	U.USE_YN AS USER_USE_YN,
	U.PROFILE_IMAGE_YN,
	R.*
from (USER_RESUME R join USER U) 
where
	R.USER_ID = U.USER_ID and U.USE_YN = 'Y' and
	R.USE_YN='Y' and R.OPEN_YN='Y'



create or replace view USER_RESUME_VIEW_ALL as
select
	U.EMAIL AS EMAIL,  U.NAME AS NAME,  U.REAL_NAME AS REAL_NAME,
	U.LOCATION_CODE AS LOCATION_CODE, U.SIDO_CODE AS SIDO_CODE, U.SIGU_CODE AS SIGU_CODE,
	U.ADDR_MAIN AS ADDR_MAIN, U.ADDR_DETAIL AS ADDR_DETAIL, U.JIBUN_ADDR AS JIBUN_ADDR,
	U.TEL_NO AS TEL_NO, U.BIRTHDAY AS BIRTHDAY, U.GENDER AS GENDER,
	U.USE_YN AS USER_USE_YN,
	U.PROFILE_IMAGE_YN,
	R.*
from (USER_RESUME R join USER U) 
where
	R.USER_ID = U.USER_ID and U.USE_YN = 'Y' and
	R.USE_YN='Y'