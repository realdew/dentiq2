package dentiq2.api.controller;


import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import dentiq2.api.ErrorCode;
import dentiq2.api.LogicalException;
import dentiq2.api.code.SystemConstants;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.JobAd;
import dentiq2.api.model.JobAdSummary;
import dentiq2.api.model.JobAttr;
import dentiq2.api.model.JobAttrGroup;
import dentiq2.api.model.JobSeekerUser;
import dentiq2.api.model.Location;
import dentiq2.api.model.LocationSummary;
import dentiq2.api.model.Resume;
import dentiq2.api.model.User;
import dentiq2.api.util.FileUtil;
import dentiq2.api.util.UserSession;
import enqual.common.juso.AddrCoordinate;
import enqual.common.juso.CoordUtil;
import enqual.common.juso.JusoUtil;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class JobSeekerUserController {

	@Autowired CommonMapper commonMapper;
	
	@Autowired private PlatformTransactionManager trxMan;
	
	private UserSession getJobSeekerUserSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();
		
		UserSession session = null;
		try {
			session = sesMan.verifyToken(httpRequest, httpResponse);
		} catch(Exception ex) {
			//throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
			//ex.printStackTrace();
			throw ex;
		}		
		if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
		
		if ( !session.isJobSeekerUser() )
			throw new LogicalException(ErrorCode.AUTH_101);	// 개인회원만 접근 가능합니다.


		return session;
	}
	
	
	
	
	@RequestMapping(value="/user/{userId}/pic/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<String>> registerUserPic(
												@PathVariable("userId")							Long userId,
												@RequestParam(value="file",		required=true)	MultipartFile uploadedFile,
												HttpServletRequest httpRequest,
												HttpServletResponse httpResponse			
			) {
		
		System.out.println("회원 사진 저장 시작");
		
		
		JsonResponse<String> res = new JsonResponse<String>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			
			if ( uploadedFile.isEmpty() )		throw new Exception("업로드된 사진을 찾을 수 없습니다.");			
			if ( uploadedFile.getSize() <= 0 )	throw new Exception("업로드된 사진의 크기가 유효하지 않습니다. (0 bytes)");
				
			
			SystemConstants systemConstants = SystemConstants.getInstance();
			
			String saveDir = systemConstants.getJOB_SEEKER_RESOURCE_PHYSICAL_DIR_ROOT();
			FileUtil.makeDir(saveDir, userId+"");
			
			
			// small size 저장
			String smallSizeFileName = systemConstants.getJOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL();
			byte[] smallSizeFileBytes = uploadedFile.getBytes();
			FileUtil.saveFile(saveDir+"/"+userId+"", smallSizeFileName, smallSizeFileBytes);
			
//				// medium size 저장
//				String mediumSizeFileName = serverConfig.getJOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_MEDIUM();
//				byte[] mediumSizeFileBytes = uploadedFile.getBytes();
//				FileUtil.saveFile(saveDir, mediumSizeFileName, mediumSizeFileBytes);
//				
//				// large size 저장
//				String largeSizeFileName = serverConfig.getJOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_LARGE();
//				byte[] largeSizeFileBytes = uploadedFile.getBytes();
//				FileUtil.saveFile(saveDir, largeSizeFileName, largeSizeFileBytes);
			
			
			commonMapper.updateProfileImageYn(userId, "Y");
			
			String profileImageUrl = systemConstants.getJOB_SEEKER_RESOURCE_URL_FULL() + "/" + userId + "/" + systemConstants.getJOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL();
			
			res.setResponse(profileImageUrl);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<String>>(res, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/user/{userId}/setting/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Map<String, String>>> getUserSettingsByUserId(
							@PathVariable("userId")		Long userId,
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse
			) {

		JsonResponse<Map<String, String>> res = new JsonResponse<Map<String, String>>();
		try {
			// JOB_SEEKER_USER, HOSPITAL_USER 공통이므로, 기본적인 세션 체크만 한다.
			dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();			
			UserSession session = null;
			try {
				session = sesMan.verifyToken(httpRequest, httpResponse);
			} catch(Exception ex) {
				throw ex;
			}
			if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.			
			if ( !session.getUserId().equals(userId) ) throw new LogicalException(ErrorCode.AUTH_102);
			
			
			
			Map<String, String> settings = commonMapper.getUserSettings(userId);			
			res.setResponse(settings);
		} catch(Exception ex) {
		res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Map<String, String>>>(res, HttpStatus.OK);	
	}
//	@RequestMapping(value="/user/{userId}/setting/", method=RequestMethod.POST)
//	public ResponseEntity<JsonResponse<Map<String, String>>> updateUserSettingByUserId(
//							@PathVariable("userId")		Long userId,
//							@RequestParam(value="agreementNoticeNewsYn",	required=false)	String agreementNoticeNewsYn,
//							@RequestParam(value="agreementHiringNewsYn",	required=false)	String agreementHiringNewsYn,
//							@RequestParam(value="agreementEventYn",			required=false)	String agreementEventYn,
//							@RequestParam(value="agreementAdYn",			required=false)	String agreementAdYn,
//							HttpServletRequest httpRequest,
//							HttpServletResponse httpResponse
//			) {
//
//		JsonResponse<Map<String, String>> res = new JsonResponse<Map<String, String>>();
//		try {
//			// JOB_SEEKER_USER, HOSPITAL_USER 공통이므로, 기본적인 세션 체크만 한다.
//			dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();			
//			UserSession session = null;
//			try {
//				session = sesMan.verifyToken(httpRequest, httpResponse);
//			} catch(Exception ex) {
//				throw ex;
//			}
//			if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.			
//			if ( !session.getUserId().equals(userId) ) throw new LogicalException(ErrorCode.AUTH_102);
//			
//			int updatedRows = commonMapper.updateUserSettings(userId, agreementNoticeNewsYn, agreementHiringNewsYn, agreementEventYn, agreementAdYn);
//			if ( updatedRows != 1 ) throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
//			
//			
//			
//			Map<String, String> settings = commonMapper.getUserSettings(userId);			
//			res.setResponse(settings);
//		} catch(Exception ex) {
//		res.setException(ex);
//		}
//		
//		return new ResponseEntity<JsonResponse<Map<String, String>>>(res, HttpStatus.OK);	
//	}
	@RequestMapping(value="/user/{userId}/setting/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Map<String, String>>> updateUserSettingsByUserId(
							@PathVariable("userId")		Long userId,
//							@RequestParam(value="agreementNoticeNewsYn",	required=true)	String agreementNoticeNewsYn,
//							@RequestParam(value="agreementHiringNewsYn",	required=true)	String agreementHiringNewsYn,
//							@RequestParam(value="agreementEventYn",			required=true)	String agreementEventYn,
//							@RequestParam(value="agreementAdYn",			required=true)	String agreementAdYn,
							@RequestBody Map<String, String> reqData,
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse
			) {

		JsonResponse<Map<String, String>> res = new JsonResponse<Map<String, String>>();
		try {
			// JOB_SEEKER_USER, HOSPITAL_USER 공통이므로, 기본적인 세션 체크만 한다.
			dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();			
			UserSession session = null;
			try {
				session = sesMan.verifyToken(httpRequest, httpResponse);
			} catch(Exception ex) {
				throw ex;
			}
			if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.			
			if ( !session.getUserId().equals(userId) ) throw new LogicalException(ErrorCode.AUTH_102);
			
			
			System.out.println(reqData);
			//int updatedRows = commonMapper.updateUserSettings(userId, agreementNoticeNewsYn, agreementHiringNewsYn, agreementEventYn, agreementAdYn);
			
			int updatedRows = commonMapper.updateUserSettings(userId, reqData.get("agreementNoticeNewsYn"), reqData.get("agreementHiringNewsYn"), reqData.get("agreementEventYn"), reqData.get("agreementAdYn"));
			if ( updatedRows != 1 ) throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
			
			
			
			Map<String, String> settings = commonMapper.getUserSettings(userId);			
			res.setResponse(settings);
		} catch(Exception ex) {
		res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Map<String, String>>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                    이력서                                                              */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	@RequestMapping(value="/user/{userId}/resume/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Resume>> getResumeByUserId(
										@PathVariable("userId")		Long userId,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		
		JsonResponse<Resume> res = new JsonResponse<Resume>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			Resume resume = commonMapper.getResumeByUserId(userId);
			res.setResponse(resume);			
			System.out.println(resume);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Resume>>(res, HttpStatus.OK);	
	}
	
	@RequestMapping(value="/user/{userId}/resume/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<Resume>> registerOrUpdateResume(
										@PathVariable("userId")		Long userId,
										@RequestBody Resume resume,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<Resume> res = new JsonResponse<Resume>();
		try {			
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			System.out.println("이력서 등록/수정 - 요청 이력서 : " + resume);
									
			Resume oldResume = commonMapper.getResumeByUserId(userId);
			if ( oldResume != null ) {
				int updatedRows = commonMapper.updateResume(resume);
				if (  updatedRows != 1 ) throw new Exception("변경된 행이 1이 아님 [" + updatedRows + "]");
				
				if ( resume.getResumeId() == null ) throw new Exception("asdfasdfasdf");
				
				// USER_RESUME_LOCATION 변경
				commonMapper.deleteResumeLocationAll(resume.getResumeId());
				if ( resume.getApplyLocationCode()!=null && resume.getApplyLocationCode().size()>0 )
					commonMapper.insertResumeLocationAll(resume.getResumeId(), resume.getApplyLocationCode());
				
				// USER_RESUME_ATTR 변경
				commonMapper.deleteResumeAttrAll(resume.getResumeId());
				List<JobAttr> attrList = JobAttr.createJobAttrList(resume.getAttr());	// 속성 테이블에 insert
				if ( attrList != null && attrList.size()>0 )	commonMapper.insertResumeAttrAll(resume.getResumeId(), attrList);
				
				
			} else {
				int updatedRows = commonMapper.createResume(resume);
				if (  updatedRows != 1 ) throw new Exception("변경된 행이 1이 아님 [" + updatedRows + "]");
				
				// USER_RESUME_LOCATION 변경
				commonMapper.deleteResumeLocationAll(resume.getResumeId());
				if ( resume.getApplyLocationCode()!=null && resume.getApplyLocationCode().size()>0 )
					commonMapper.insertResumeLocationAll(resume.getResumeId(), resume.getApplyLocationCode());
				
				// USER_RESUME_ATTR 변경
				commonMapper.deleteResumeAttrAll(resume.getResumeId());
				List<JobAttr> attrList = JobAttr.createJobAttrList(resume.getAttr());	// 속성 테이블에 insert
				if ( attrList != null && attrList.size()>0 )	commonMapper.insertResumeAttrAll(resume.getResumeId(), attrList);
			}
			
			
			Resume newResume = commonMapper.getResumeById(resume.getResumeId());
			res.setResponse(newResume);				
			trxMan.commit(trxStatus);				// COMMIT
			
			System.out.println("이력서 등록/수정 - 결과 : " + newResume);
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
		
		return new ResponseEntity<JsonResponse<Resume>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                               공고 지원  (지원현황)                                                    */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	@RequestMapping(value="/user/{userId}/appliedJobAd/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<JobAd>>> listIAppliedJobAd(
				@PathVariable("userId") Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse) {		
		
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			List<JobAd> jobAdList = commonMapper.listAppliedJobAd(userId);
			//markJobAdIsAppliedByCertainJobSeeker(userId, jobAdList);
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
	}
	
//	@RequestMapping(value="/user/{userId}/appliedJobAdId/", method=RequestMethod.GET)
//	public ResponseEntity<JsonResponse<List<Long>>> listAppliedJobAdId(
//				@PathVariable("userId")		Long userId,
//				HttpServletRequest httpRequest,
//				HttpServletResponse httpResponse) {		
//		JsonResponse<List<Long>> res = new JsonResponse<List<Long>>();
//		try {
//			getJobSeekerUserSession(httpRequest, httpResponse);
//			
//			List<Long> appliedJobAdIdList = commonMapper.listAppliedJobAdId(userId);
//			res.setResponse(appliedJobAdIdList);
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<List<Long>>>(res, HttpStatus.OK);			
//	}
//	@RequestMapping(value="/user/{userId}/appliedJobAdId/{jobAdId}/", method=RequestMethod.POST)
//	public ResponseEntity<JsonResponse<List<Long>>> addAppliedJobAd(
//				@PathVariable("userId")								Long userId,
//				@PathVariable("jobAdId")							Long jobAdId,
//				HttpServletRequest httpRequest,
//				HttpServletResponse httpResponse
//					) {
//		JsonResponse<List<Long>> res = new JsonResponse<List<Long>>();
//		try {
//			getJobSeekerUserSession(httpRequest, httpResponse);
//			
//			// 이력서가 작성되었는지 먼저 검토하여야 한다.
//			Long resumeId = commonMapper.getResumeIdOfUser(userId);
//			if ( resumeId == null ) throw new LogicalException(ErrorCode.USER_602);
//			
//			//TODO 인서트하기 전에 이미 기존재하는지 확인 필요. 현재는 개발 중이므로, 에러 발견을 위해서 하지 않는다.
//			// int cnt = commonMapper.has~~~ 이미 인서트되어 있는지 확인
//			
//			commonMapper.insertAppliedJobAd(userId, jobAdId);
//			
//			List<Long> appliedJobAdIdList = commonMapper.listAppliedJobAdId(userId);
//			res.setResponse(appliedJobAdIdList);
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<List<Long>>>(res, HttpStatus.OK);			
//	}
//	@RequestMapping(value="/user/{userId}/appliedJobAdId/{jobAdId}/", method=RequestMethod.DELETE)
//	public ResponseEntity<JsonResponse<List<Long>>> deleteAppliedJobAd(
//				@PathVariable("userId")							Long userId,
//				@PathVariable("jobAdId")						Long jobAdId,
//				HttpServletRequest httpRequest,
//				HttpServletResponse httpResponse
//					) {
//		JsonResponse<List<Long>> res = new JsonResponse<List<Long>>();
//		try {
//			getJobSeekerUserSession(httpRequest, httpResponse);
//			
//			commonMapper.deleteAppliedJobAd(userId, jobAdId);
//			
//			List<Long> appliedJobAdIdList = commonMapper.listAppliedJobAdId(userId);
//			res.setResponse(appliedJobAdIdList);
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<List<Long>>>(res, HttpStatus.OK);			
//	}
	@RequestMapping(value="/user/{userId}/appliedJobAdId/{jobAdId}/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Long>> addAppliedJobAd(
				@PathVariable("userId")								Long userId,
				@PathVariable("jobAdId")							Long jobAdId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			// 이력서가 작성되었는지 먼저 검토하여야 한다.
			Long resumeId = commonMapper.getResumeIdOfUser(userId);
			if ( resumeId == null ) throw new LogicalException(ErrorCode.USER_602);
			
			
			
			int updatedRows = commonMapper.insertAppliedJobAd(userId, jobAdId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(jobAdId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);
	}
	@RequestMapping(value="/user/{userId}/appliedJobAdId/{jobAdId}/", method=RequestMethod.DELETE)
	public ResponseEntity<JsonResponse<Long>> deleteAppliedJobAd(
				@PathVariable("userId")							Long userId,
				@PathVariable("jobAdId")						Long jobAdId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			int updatedRows = commonMapper.deleteAppliedJobAd(userId, jobAdId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(jobAdId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);
	}
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  관심 병원                                                             */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	@RequestMapping(value="/user/{userId}/interestHospitalJobAd/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<JobAd>>> listInterstHospitaJobAd(
				@PathVariable("userId") Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse) {		
		
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			List<JobAd> jobAdList = commonMapper.listInterestHospitalJobAd(userId);
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/{userId}/interestHospitalId/{hospitalId}/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Long>> addInterestHospitalId(
				@PathVariable("userId")							Long userId,
				@PathVariable("hospitalId")						Long hospitalId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			int updatedRows = commonMapper.insertInterestHospital(userId, hospitalId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(hospitalId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);			
	}
	@RequestMapping(value="/user/{userId}/interestHospitalId/{hospitalId}/", method=RequestMethod.DELETE)
	public ResponseEntity<JsonResponse<Long>> deleteInterestHospitalId(
				@PathVariable("userId")							Long userId,
				@PathVariable("hospitalId")						Long hospitalId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			int updatedRows = commonMapper.deleteInterestHospital(userId, hospitalId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(hospitalId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);			
	}
	
	
	
	
	
	// 공고에 대하여 스크랩 상태 및 지원 상태 / 관심병원 여부 조회
	@RequestMapping(value="/user/{userId}/statusJobAdAction/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Map<String, Long>>> getStatusForScrapAndApply(
						@PathVariable("userId") Long userId,
						@RequestParam(value="jobAdId",		required=true)	Long jobAdId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse) {
		JsonResponse<Map<String, Long>> res = new JsonResponse<Map<String, Long>>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			Map<String, Long> result = new Hashtable<String, Long>();
			
			// 스크랩 상태 조회
			int cntScrapped = commonMapper.countByScrappedJobAdId(userId, jobAdId);
			if ( cntScrapped == 1 )	result.put("scrap", jobAdId);
			
			// 지원 상태 조회
			int cntApplied = commonMapper.countByAppliedJobAdId(userId, jobAdId);
			if ( cntApplied == 1 )	result.put("apply", jobAdId);
			
			// 관심병원 조회
			int cntInterestHospital = commonMapper.countByInterestHospitalJobAdId(userId, jobAdId);
			if ( cntInterestHospital == 1 )	result.put("interestHospital", jobAdId);
			
			
			
			res.setResponse(result);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Map<String, Long>>>(res, HttpStatus.OK);
	}
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  스크랩                                                                */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	@RequestMapping(value="/user/{userId}/scrappedJobAd/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<JobAd>>> listScrappedJobAd(
				@PathVariable("userId") Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse) {
		
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			List<JobAd> jobAdList = commonMapper.listScrappedJobAd(userId);
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="/user/{userId}/scrappedJobAdId/{jobAdId}/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Long>> addScrappedJobAdId(
								@PathVariable("userId")				Long userId,
								@PathVariable("jobAdId")			Long jobAdId,
								HttpServletRequest httpRequest,
								HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			int updatedRows = commonMapper.insertScrappedJobAd(userId, jobAdId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(jobAdId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);			
	}
	@RequestMapping(value="/user/{userId}/scrappedJobAdId/{jobAdId}/", method=RequestMethod.DELETE)
	public ResponseEntity<JsonResponse<Long>> deleteScrappedJobAdId(
				@PathVariable("userId")							Long userId,
				@PathVariable("jobAdId")						Long jobAdId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
					) {
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			int updatedRows = commonMapper.deleteScrappedJobAd(userId, jobAdId);
			if (  updatedRows != 1 )	throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
						
			res.setResponse(jobAdId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);			
	}
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                            면접요청 (병원이 한)                                                        */
	/*                                                                                                                        */
	/**************************************************************************************************************************/	
	@RequestMapping(value="/user/{userId}/offeredJobAd/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<JobAd>>> listOfferedJobAd(
				@PathVariable("userId") Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse) {		
		
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			UserSession session = getJobSeekerUserSession(httpRequest, httpResponse);
			if ( !session.getUserId().equals(userId) )	throw new LogicalException(ErrorCode.AUTH_102);
			
			List<JobAd> jobAdList = commonMapper.listOfferedJobAdByUserId(userId);
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
	}
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  관심지역                                                              */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	
	/** 관심지역 코드 조회 및 코드 변경 **/
	@RequestMapping(value="/user/{userId}/interestLocationCode/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<String>>> listInterestLocationCode(
				@PathVariable("userId")		Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse				
			) {
		
		JsonResponse<List<String>> res = new JsonResponse<List<String>>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			List<String> locationCodeList = commonMapper.listInterestLocationCodeByUserId(userId);
			res.setResponse(locationCodeList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<String>>>(res, HttpStatus.OK);	
	}
	
	@RequestMapping(value="/user/{userId}/interestLocationCode/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<List<String>>> updateInterestLocation(
				@PathVariable("userId")									Long userId,
				@RequestParam(value="locationCode",	required=false)		List<String> locationCodeList,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse				
			) {
		
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<List<String>> res = new JsonResponse<List<String>>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			commonMapper.deleteInterestLocationAll(userId);					// 기존의 것을 모두 삭제
			if ( locationCodeList!=null && locationCodeList.size()>0 )		// 모두 insert
				commonMapper.insertInterestLocationAll(userId, locationCodeList);
						
			List<String> locationCodeListUpdated = commonMapper.listInterestLocationCodeByUserId(userId);
			res.setResponse(locationCodeListUpdated);
			
			trxMan.commit(trxStatus);				// COMMIT
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
		return new ResponseEntity<JsonResponse<List<String>>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                             개인회원 기본 정보  및  주소                                                               */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/** 구직(개인)회원 기본정보. 주의!!! 해당 기본정보에는 주소(LOCATION)이 포함됨 */
	@RequestMapping(value="/user/{userId}/basicInfo/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<User>> getBasicInfo(
				@PathVariable("userId")		Long userId,
				@RequestBody				JobSeekerUser user,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		
		JsonResponse<User> res = new JsonResponse<User>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			//TODO PathVariable의 userId와 user 객체의 user ID를 확인할 것
			
			
			//user.setUserId(userId);
			int rowsUpdated = commonMapper.updateJobSeekerUserBasicInfo(user);
			if ( rowsUpdated != 1 )
				throw new Exception("개인회원기본정보 변경실패 [" + rowsUpdated + "]");
			
			JobSeekerUser userUpdated = commonMapper.getJobSeekerUserByUserId(userId);
			res.setResponse(userUpdated);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.OK);	
	}  
	
	/** 구직(개인)회원 기본정보. */
	@RequestMapping(value="/user/{userId}/basicInfo/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<User>> getBasicInfo(
				@PathVariable("userId")		Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
				
			) {
		
		JsonResponse<User> res = new JsonResponse<User>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			JobSeekerUser user = commonMapper.getJobSeekerUserByUserId(userId);
			//JobSeekerUser user = commonMapper.getJobSeekerUserBasicInfo(userId);
			
			res.setResponse(user);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.OK);	
	}
	

	
	/** 회원 주소 변경 **/
	@RequestMapping(value="/user/{userId}/addr/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<Location>> updateUserAddr(
				@PathVariable("userId")		Long userId,
				@RequestBody				Location location,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
				
			) {
		
		JsonResponse<Location> res = new JsonResponse<Location>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			updateLocationWithAdditionalInfo(location);
						
			//int updatedRows = commonMapper.updateUserLocation(userId, location);
			commonMapper.updateUserLocation(userId, location);
			
			Location locationUpdated = commonMapper.getUserLocationByUserId(userId);
			res.setResponse(locationUpdated);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Location>>(res, HttpStatus.OK);	
	}
	
	/** 회원 주소 조회 **/
	@RequestMapping(value="/user/{userId}/addr/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Location>> getUserAddr(
				@PathVariable("userId")		Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse	
			) {
		
		JsonResponse<Location> res = new JsonResponse<Location>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			Location location = commonMapper.getUserLocationByUserId(userId);
			res.setResponse(location);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Location>>(res, HttpStatus.OK);	
	}
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                라이브보드 - 우리동네, 관심지역 : 지역별 SUMMARY                                        */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/** Liveboard - 우리동네 */
	@RequestMapping(value="/user/{userId}/homeLocationSummary/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<JobAdSummary> > summaryHomeLocationJobAd(
				@PathVariable("userId")									Long userId,
				@RequestParam(value="attr",			required=false)		List<String> attrStrList,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<JobAdSummary> res = new JsonResponse<JobAdSummary>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			// 우리동네의 시구 코드를 찾는다.									
			String siguCode = commonMapper.getUserHomeSiguCode(userId);
			if ( siguCode == null ) {
				throw new LogicalException(ErrorCode.USER_501);		// 우리동네가 설정되어 있지 않습니다.
			}
			
			List<JobAttrGroup> jobAttrGroupList = JobAttrGroup.createJobAttrGroupFromStringList(attrStrList);	// 속성 그룹 리스트 생성
			List<LocationSummary> locationSummary = commonMapper.summarySpecificSigu(siguCode, jobAttrGroupList);
			
			
			List<String> siguCodeList = new java.util.ArrayList<String>();	siguCodeList.add(siguCode);			
			JobAdSummary summary = new JobAdSummary(null, siguCodeList, attrStrList, locationSummary);
			res.setResponse(summary);
		} catch(Exception ex) {
			ex.printStackTrace();
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<JobAdSummary>>(res, HttpStatus.OK);
	}
	
	/** Liveboard - 관심지역 : 현재 버전은 시구만 */ 
	@RequestMapping(value="/user/{userId}/interestLocationSummary/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<JobAdSummary> > summaryInterestLocationJobAd(
				@PathVariable("userId")									Long userId,
				@RequestParam(value="siguCode",		required=false)		List<String> siguCodeList,	// 주의 : 데이터 검색 조건은 아님. 요청된 것 marking하기 위함
				@RequestParam(value="attr",			required=false)		List<String> attrStrList,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<JobAdSummary> res = new JsonResponse<JobAdSummary>();
		try {
			getJobSeekerUserSession(httpRequest, httpResponse);
			
			// 관심지역의 시구코드들을 찾는다.									
			List<String> locationCodeListAsSiguCode = commonMapper.listInterestLocationCodeByUserId(userId);
			if ( locationCodeListAsSiguCode == null || locationCodeListAsSiguCode.size()==0 ) {
				throw new LogicalException(ErrorCode.USER_502);		// 관심지역이 설정되어 있지 않습니다.
			}
			
			List<JobAttrGroup> jobAttrGroupList = JobAttrGroup.createJobAttrGroupFromStringList(attrStrList);	// 속성 그룹 리스트 생성
			List<LocationSummary> locationSummary = commonMapper.summarySpecificSiguListByLocationCode(locationCodeListAsSiguCode, jobAttrGroupList);
			
			if ( siguCodeList==null || siguCodeList.size()<1 )															// 요청된 siguCodeList가 없다면, 관심지역들의 SIGU_CODE를 요청된 SIGU_CODE로 보내주어야 한다.
				siguCodeList = Location.convertLocationCodeOrSiguCodeListToSiguCodeList(locationCodeListAsSiguCode);	// locationCodeListAsSiguCode가 지역코드(LOCATION_CODE)의 형태이므로, 이를 SIGU_CODE로 변경하여야 한다.
			
			JobAdSummary summary = new JobAdSummary(null, siguCodeList, attrStrList, locationSummary);
			res.setResponse(summary);
		} catch(Exception ex) {
			ex.printStackTrace();
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<JobAdSummary>>(res, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void updateLocationWithAdditionalInfo(Location location) throws Exception {
		
//		//--------------------------- LOCATION 코드 검증 및 처리 ------------------------
//		String admCd = location.getAdmCd();	// 행정구역 코드
//		try {
//			Long.parseLong(admCd);	// 숫자 형식 여부 확인
//			
//			String sidoCode = admCd.substring(0, 2);
//			String siguCode = admCd.substring(0, 5);			
//			String locCode = sidoCode + Location.CODE_DELIMETER + siguCode;
//			
//			// 여기서 DB 검증 한번 하여야 한다.			
////			locationCode = codeMapper.getLocationCode(locCode);
////			if ( locationCode != null && locationCode.getSidoCode().equals(sidoCode) && locationCode.getSiguCode().equals(siguCode) ) {
////				System.out.println("지역코드 찾았음 [" + locationCode + "]");
////			} else {
////				throw new LogicalException("LOCATION_CODE 처리 중 오류 locationCode[" + locCode + "] ==> " + locationCode);
////			}
//			
//		} catch(Exception ex) {
//			System.out.println(ex);
//			ex.printStackTrace(System.out);
//			throw new Exception("행정구역코드(admCd) 포맷 오류 [" + admCd + "] <== [" + ex + "]");
//		}
		//------------------------------------------------------------------------------
		
		// 좌표 검색 수행
		if ( location.getEntX()==null || location.getEntX().trim().equals("") || location.getEntY()==null || location.getEntY().trim().equals("") ) {
			try {
				JusoUtil jusoUtil = new JusoUtil();
				AddrCoordinate[] coordinates = jusoUtil.searchCoordinate(location.getAdmCd(), location.getRnMgtSn(), location.getUdrtYn(), location.getBuldMnnm(), location.getBuldSlno());
				if ( coordinates==null || coordinates.length < 1 ) {	//TODO 좌표가 2개 이상이면 어떻게 하나?
					throw new Exception("좌표 조회에 실패했습니다.");
				}
				
				location.setEntX(coordinates[0].getEntX());
				location.setEntY(coordinates[0].getEntY());
				
			} catch(Exception ex) {
				throw new Exception("좌표 조회에 실패했습니다.", ex);
			}
		}
		
		// 좌표 변환 수행
		CoordUtil cUtil = new CoordUtil();
		String[] newCoord = cUtil.transGRS80toWGS84(location.getEntX(), location.getEntY());
		location.setLatLonX(newCoord[0]);
		location.setLatLonY(newCoord[1]);
		
		//return location;
	}
	
	
}
