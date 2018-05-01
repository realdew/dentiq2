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


import dentiq2.api.model.Hospital;
import dentiq2.api.util.FileUtil;
import dentiq2.api.util.PageInfo;
import dentiq2.api.util.UserSession;
import dentiq2.api.util.UserSessionManager;
import enqual.common.juso.CoordUtil;
import dentiq2.api.ErrorCode;
import dentiq2.api.LogicalException;
import dentiq2.api.code.SystemConstants;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.JobAd;
import dentiq2.api.model.JobAttr;
import dentiq2.api.model.Resume;
import dentiq2.api.model.User;

/**
 * 병원 회원을 위한 REST Controller
 * 
 * 병원 회원에 대한 접근 권한 확인을 한다.
 * 
 * 
 * @author lee
 *
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class HospitalUserController {
		
	/*	
	 * 기본 :
	 *			* 완료 ==> 병원 등록/수정  (삭제는 없음)
	 *			* 완료 ==> 공고 등록/수정/삭제, 
	 *			
	 *			공고 간편 목록 보기
	 * 
	 * 확장 :
	 * 		공고 알리기 : 일반 공고 다운그레이드 <==> 프리미어 공고 업그레이드  (공고 간편 목록 보기)
	 * 		지원자 관리
	 * 		스크랩 인재 관리
	 * 		추천인재
	 * 		인재열람	(이력서에 대한 LIVEBOARD)
	 * 		결제내역
	 * 		면접 제안
	 */
	
	private UserSession getHospitalUserSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();
		
		UserSession session = null;
		try {
			session = sesMan.verifyToken(httpRequest, httpResponse);
		} catch(Exception ex) {
			throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
		}		
		if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
		
		
		// DB에서 찾는다. token을 검증한다. 들어 있던 것인지...
		
		// Expired 되었는지 확인한다.
		
//		Long expireTimeAt = session.getExpireTimeAt();
//		Long currentTime = commonMapper.getUnixTimestamp();		// 현재 시각을 DB에서 읽어 온다.
//		
//		if ( expireTimeAt > currentTime ) {
//			if ( session.getKeepingLoginType().equals(User.KEEPING_LOGIN_PERM) ) {
//				// 여기서 재발급해야 한다.
//				
//			}
//			
//			throw new LogicalException(ErrorCode.AUTH_902);	// 사용시간 만료
//			
//		}
		
		
		// DB와 검증이 완료 되었다면...
		
		if ( !session.isHospitalUser() )
			throw new LogicalException(ErrorCode.AUTH_001);	// 병원회원만 접근 가능합니다.
		
		
		return session;
	}

	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                              로고 이미지 업로드                                                        */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	@RequestMapping(value="/hospital/{hospitalId}/logo/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<String>> registerUserPic(
												@PathVariable("hospitalId")						Long hospitalId,
												@RequestParam(value="file",		required=true)	MultipartFile uploadedFile,
												HttpServletRequest httpRequest,
												HttpServletResponse httpResponse			
			) {
		
		System.out.println("병원 로고 저장 시작");
		
		JsonResponse<String> res = new JsonResponse<String>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			if ( uploadedFile.isEmpty() )		throw new Exception("업로드된 사진을 찾을 수 없습니다.");			
			if ( uploadedFile.getSize() <= 0 )	throw new Exception("업로드된 사진의 크기가 유효하지 않습니다. (0 bytes)");
				
			
			SystemConstants systemConstants = SystemConstants.getInstance();
			
			String saveDir = systemConstants.getHOSPITAL_RESOURCE_PHYSICAL_DIR_ROOT();
			FileUtil.makeDir(saveDir, hospitalId+"");
			
			
			// small size 저장
			String smallSizeFileName = systemConstants.getHOSPITAL_RESOURCE_FILE_NAME_LOGO_SMALL();
			byte[] smallSizeFileBytes = uploadedFile.getBytes();
			FileUtil.saveFile(saveDir+"/"+hospitalId+"", smallSizeFileName, smallSizeFileBytes);
			
//				// medium size 저장
//				String mediumSizeFileName = serverConfig.getHOSPITAL_RESOURCE_FILE_NAME_LOGO_MEDIUM();
//				byte[] mediumSizeFileBytes = uploadedFile.getBytes();
//				FileUtil.saveFile(saveDir, mediumSizeFileName, mediumSizeFileBytes);
//				
//				// large size 저장
//				String largeSizeFileName = serverConfig.getHOSPITAL_RESOURCE_FILE_NAME_LOGO_LARGE();
//				byte[] largeSizeFileBytes = uploadedFile.getBytes();
//				FileUtil.saveFile(saveDir, largeSizeFileName, largeSizeFileBytes);
			
			
			commonMapper.updateLogoImageYn(hospitalId, "Y");
			
			res.setResponse("OK");
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<String>>(res, HttpStatus.OK);
	}
	
	
	
	@Autowired CommonMapper commonMapper;

	@Autowired private PlatformTransactionManager trxMan;
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  인재 관리                                                             */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	
	
	// 인재관리 공통 : 이력서에 대하여 스크랩 상태 및 제안 상태  조회
	@RequestMapping(value="/hospital/{hospitalId}/statusResumeAction/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Map<String, Long>>> getStatusForScrapAndApply(
						@PathVariable("hospitalId")							Long hospitalId,
						@RequestParam(value="resumeId",		required=true)	Long resumeId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse) {
		JsonResponse<Map<String, Long>> res = new JsonResponse<Map<String, Long>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			Map<String, Long> result = new Hashtable<String, Long>();
			
			// 스크랩 상태 조회
			int cntScrapped = commonMapper.countResumeScrappedByHospitalIdAndResumeId(hospitalId, resumeId);
			if ( cntScrapped == 1 )	result.put("scrap", resumeId);
			
			// 제안 상태 조회
			int cntApplied = commonMapper.countResumeOfferedByHospitalIdAndResumeId(hospitalId, resumeId);
			if ( cntApplied == 1 )	result.put("apply", resumeId);
		
			res.setResponse(result);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Map<String, Long>>>(res, HttpStatus.OK);
	}
	
	
	
	
	/**************************************** 지원 인재 조회 ****************************************/
	// 해당 병원에 지원한 이력서들을 조회한다.
	@RequestMapping(value="/hospital/{hospitalId}/resumeApplied/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<Resume>>> listResumeApplied(
						@PathVariable("hospitalId")			Long hospitalId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse		
		) {
	
		JsonResponse<List<Resume>> res = new JsonResponse<List<Resume>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			List<Resume> resumeList = commonMapper.listResumeAppliedByHospitalId(hospitalId);
			res.setResponse(resumeList);
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Resume>>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	
	/**************************************** 이력서 스크랩 ****************************************/
	@RequestMapping(value="/hospital/{hospitalId}/resumeScrapped/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<Resume>>> listResumeScrapped(
						@PathVariable("hospitalId")			Long hospitalId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse		
			) {
		
		JsonResponse<List<Resume>> res = new JsonResponse<List<Resume>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			List<Resume> resumeList = commonMapper.listResumeScrappedByHospitalId(hospitalId);			
			res.setResponse(resumeList);
		
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Resume>>>(res, HttpStatus.OK);	
	}
	@RequestMapping(value="/hospital/{hospitalId}/resumeIdScrapped/{resumeId}/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Long>> addResumeIdScrapped(
						@PathVariable("hospitalId")						Long hospitalId,
						@PathVariable(value="resumeId",	required=true)	Long resumeId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse		
			) {
		
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			commonMapper.addResumeScrapped(hospitalId, resumeId);
			res.setResponse(resumeId);
		} catch(Exception ex) {
			res.setException(ex);
		}		
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);	
	}
	@RequestMapping(value="/hospital/{hospitalId}/resumeIdScrapped/{resumeId}/", method=RequestMethod.DELETE)
	public ResponseEntity<JsonResponse<Long>> deleteResumeIdScrapped(
						@PathVariable("hospitalId")						Long hospitalId,
						@PathVariable(value="resumeId",	required=true)	Long resumeId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse		
			) {
		
		JsonResponse<Long> res = new JsonResponse<Long>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			commonMapper.deleteResumeScrapped(hospitalId, resumeId);
			res.setResponse(resumeId);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Long>>(res, HttpStatus.OK);	
	}
	
	
	
	
	/**************************************** 면접 제안한 인재(이력서) 목록 ****************************************/
	
	@RequestMapping(value="/hospital/{hospitalId}/resumeOffered/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<Resume>>> listResumeOffered(
						@PathVariable("hospitalId")						Long hospitalId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse
				) {
		
		JsonResponse<List<Resume>> res = new JsonResponse<List<Resume>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);			
			
			List<Resume> resumeList = commonMapper.listResumeOfferedByHospitalId(hospitalId);
			res.setResponse(resumeList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Resume>>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="/hospital/{hospitalId}/resumeIdOffered/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Map<String, Long>>> addResumeIdOffered(
						@PathVariable("hospitalId")						Long hospitalId,
						@RequestParam(value="resumeId",	required=true)	Long resumeId,
						@RequestParam(value="jobAdId",	required=true)	Long jobAdId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse
				) {
		
		JsonResponse<Map<String, Long>> res = new JsonResponse<Map<String, Long>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			
			// 해당 공고가 해당 병원의 것인지를 확인해야 한다.
			if ( commonMapper.hasCertainJobAdOfHospital(hospitalId, jobAdId) != 1 ) {
				throw new Exception("요청된 공고(" + jobAdId + ")는 해당 병원(" + hospitalId + ")의 것이 아닙니다.");
			}
			
			int updatedRows = commonMapper.addResumeOfferedByJobAdIdAndResumeId(jobAdId, resumeId);
			if ( updatedRows != 1) throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
			
			Map<String, Long> result = new Hashtable<String, Long>();
			result.put("resumeId", resumeId);
			result.put("jobAdId", jobAdId);
			
			res.setResponse(result);		
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Map<String, Long>>>(res, HttpStatus.OK);
	}
	
	
	
	
	/**************************************** 추천 인재 ****************************************/
	@RequestMapping(value="/hospital/{hospitalId}/resumeRecommended/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<Resume>>> listResumeRecommended(
							@PathVariable("hospitalId")			Long hospitalId,
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse		
			) {
		
		JsonResponse<List<Resume>> res = new JsonResponse<List<Resume>>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			List<Resume> resumeList = commonMapper.listResumeRecommended(hospitalId);
			res.setResponse(resumeList);
		
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Resume>>>(res, HttpStatus.OK);	
	
	}
	
	

	// 오픈된 이력서 (해당 병원에 지원한 이력서는 불포함 2018.04.26)
	/**************************************** 인재 열람 ****************************************/
	@RequestMapping(value="/resume/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<Resume>>> listResumeSearched(
							@RequestParam(value="sidoCode",		required=false)		List<String> sidoCodeList,
							@RequestParam(value="siguCode",		required=false)		List<String> siguCodeList,
							@RequestParam(value="attr",			required=false)		List<String> attrStrList,
			
							@RequestParam(value="pageNo",		defaultValue="1")	Integer pageNo,
							@RequestParam(value="pageSize",		required=false)		Integer pageSize,							
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse		
			) {
		
		JsonResponse<List<Resume>> res = new JsonResponse<List<Resume>>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			PageInfo pageInfo = new PageInfo(pageNo, pageSize);		// 페이지 정보 생성
			
			System.out.println("***********************   " + pageInfo.startIndexOnPage + "  " + pageInfo.itemCntPerPage);
			List<Resume> resumeList = commonMapper.listResumeSearched(pageInfo.startIndexOnPage, pageInfo.itemCntPerPage);
			res.setResponse(resumeList);
			
			if ( pageNo == 1 ) {		// 첫번째 페이지일 경우에만 전체 개수를 조회한다.
				int totalItemCnt = commonMapper.countResumeSearched();
				System.out.println("목록의 전체 개수 : " + totalItemCnt);
				pageInfo.setTotalItemCnt(totalItemCnt);
				res.setPageInfo(pageInfo);
			}
		
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Resume>>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	
	
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  공고 정보                                                             */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/**************************************** 공고 등록 ****************************************/
	@RequestMapping(value="/jobAd/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<JobAd>> createJobAd(
			@RequestBody					JobAd jobAd,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse
			) {
		
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<JobAd> res = new JsonResponse<JobAd>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			
			Long sessionHospitalId = session.getHospitalId();
			if ( sessionHospitalId==null )
				throw new LogicalException(ErrorCode.AUTH_010);	// 세션에 병원 ID 없음 ==> 공고 등록에서는 '병원 정보를 먼저 등록해주십시오'
			
			if ( !sessionHospitalId.equals(jobAd.getHospitalId()) )
				throw new LogicalException(ErrorCode.AUTH_011);	// 세션의 병원ID와 등록하려고 하는 공고의 병원 ID가 불일치 ==> 공고 등록에서는 '회원님의 정보와 공고 정보가 불일치합니다. 다시 로그인 후 사용하여 주십시오.'
			
			
			commonMapper.createJobAd(jobAd);			// DB에 새로 생성 (insert)			
			commonMapper.deleteJobAdAttrAll(jobAd.getJobAdId());	// 속성 테이블 delete			
			List<JobAttr> attrList = JobAttr.createJobAttrList(jobAd.getAttr());	// 속성 테이블에 insert
			if ( attrList != null && attrList.size()>0 )	commonMapper.insertJobAdAttrAll(jobAd.getJobAdId(), attrList);			
			
			JobAd jobAdCreated = commonMapper.getJobAdById(jobAd.getJobAdId());	// 새로 생성된 DB에서 읽어 옴
			res.setResponse(jobAdCreated);
			
			trxMan.commit(trxStatus);				// COMMIT
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
				
		return new ResponseEntity<JsonResponse<JobAd>>(res, HttpStatus.OK);
	}
	
	/**************************************** 공고 수정 ****************************************/
	@RequestMapping(value="/jobAd/{jobAdId}/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<JobAd>> updateJobAd(
								@PathVariable("jobAdId")		Long	jobAdId,
								@RequestBody					JobAd	jobAd,
								HttpServletRequest httpRequest,
								HttpServletResponse httpResponse
			) {
		
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<JobAd> res = new JsonResponse<JobAd>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			if ( !jobAd.getJobAdId().equals(jobAdId) )
				throw new Exception("jobAdId invalid. [" + jobAdId + "] != [" + jobAd.getJobAdId() + "]");
				
			
			commonMapper.updateJobAdBasic(jobAd);							// JOB_AD 테이블 수정
			
			commonMapper.deleteJobAdAttrAll(jobAd.getJobAdId());				// 속성 테이블 delete			
			List<JobAttr> attrList = JobAttr.createJobAttrList(jobAd.getAttr());	// 속성 테이블에 insert
			if ( attrList != null && attrList.size()>0 )	commonMapper.insertJobAdAttrAll(jobAd.getJobAdId(), attrList);			
						
			JobAd updatedJobAd = commonMapper.getJobAdById(jobAd.getJobAdId());	// 새로 생성된 DB에서 읽어 옴
			res.setResponse(updatedJobAd);
			
			trxMan.commit(trxStatus);				// COMMIT
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
		return new ResponseEntity<JsonResponse<JobAd>>(res, HttpStatus.OK);
	}
	
	/**************************************** 공고 조회 ****************************************/
	@RequestMapping(value="/jobAd/{jobAdId}/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<JobAd> > getJobAd(
				@PathVariable("jobAdId")		Long jobAdId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<JobAd> res = new JsonResponse<JobAd>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			JobAd jobAd = commonMapper.getJobAdById(jobAdId);			
			res.setResponse(jobAd);
		} catch(Exception ex) {
			ex.printStackTrace();
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<JobAd>>(res, HttpStatus.OK);
	}
	
	/**************************************** 공고 목록 조회 ****************************************/
	@RequestMapping(value="/hospital/{hospitalId}/jobAd/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<List<JobAd>>> listJobAd(
									@PathVariable("hospitalId")		Long hospitalId,
									HttpServletRequest httpRequest,
									HttpServletResponse httpResponse			
			) {
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			List<JobAd> jobAdList = commonMapper.listJobAdOfHospital(hospitalId);
			
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
	}
	
	
	/**************************************************************************************************************************/
	/*                                                                                                                        */
	/*                                                  병원 정보                                                             */
	/*                                                                                                                        */
	/**************************************************************************************************************************/
	
	/**************************************** 병원 등록 ****************************************/
	@RequestMapping(value="/hospital/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Hospital>> createHospitalByUserId(
										@RequestBody Hospital hospital,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<Hospital> res = new JsonResponse<Hospital>();
		try {			
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			
			
			Long sessionUserId = session.getUserId();
			hospital.setUserId(sessionUserId);	// 등록자 ID를 추가
			
			// 좌표 변환
			CoordUtil coordUtil = new CoordUtil();
			String[] coord = coordUtil.transGRS80toWGS84(hospital.getLocation().getEntX(), hospital.getLocation().getEntY());
			hospital.getLocation().setLatLonX(coord[0]);
			hospital.getLocation().setLatLonY(coord[1]);
			
			
			// 병원 등록 실행
			System.out.println("신규병원 등록 : " + hospital);
			commonMapper.createHospital(hospital);
			
			// USER_INFO에 hospitalId 추가
			int updatedRows = commonMapper.updateHospitalIdOfHospitalUser(hospital.getUserId(), hospital.getHospitalId());
			if ( updatedRows != 1 ) {
				throw new Exception("병원 정보 등록 : USER 테이블(" + hospital.getUserId() + ")에 병원ID(" + hospital.getHospitalId() + ") 추가 실패 ==> " + updatedRows);
			}
			
			
			
			//병원을 새로 생성한 경우에는 사용자세션을 다시 초기화하여야 한다.
			User user = commonMapper.getUserMinimalById(hospital.getUserId());
			if ( user == null ) {
				throw new LogicalException(ErrorCode.USER_001);	// 해당 사용자(%1)는 가입되어 있지 않습니다
			}
			UserSessionManager sesMan = UserSessionManager.create();
			sesMan.issueToken(httpResponse, user, null);
			
			
			
			Hospital hospitalCreated = commonMapper.getHospitalByHospitalId(hospital.getHospitalId());
			res.setResponse(hospitalCreated);
			
			trxMan.commit(trxStatus);				// COMMIT
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
		
		return new ResponseEntity<JsonResponse<Hospital>>(res, HttpStatus.OK);	
	}
	
	
	
	
	/**************************************** 병원 수정 ****************************************/
	@RequestMapping(value="/hospital/{hospitalId}/", method=RequestMethod.PUT)
	public ResponseEntity<JsonResponse<Hospital>> updateHospital(
										@PathVariable("hospitalId")	Long hospitalId,
										@RequestBody Hospital hospital,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		TransactionDefinition trxDef = new DefaultTransactionDefinition();
		TransactionStatus trxStatus  = trxMan.getTransaction(trxDef);
		
		JsonResponse<Hospital> res = new JsonResponse<Hospital>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			
			if ( !hospital.getHospitalId().equals(hospitalId) )
				throw new Exception("hospitalId invalid. [" + hospitalId + "] != [" + hospital.getHospitalId() + "]");
			
			
			
			
			Long sessionHospitalId = session.getHospitalId();
			if ( !hospitalId.equals(sessionHospitalId) )
				throw new LogicalException(ErrorCode.AUTH_002);	// 병원회원의 hospitalId와 입력된 hospitalId가 불일치
			
			Long sessionUserId = session.getUserId();
			if ( !hospital.getUserId().equals(sessionUserId) )
				throw new LogicalException(ErrorCode.AUTH_005);	// 입력된 병원 정보 상의 userId와 세션 userId가 불일치
			
			if ( !hospital.getHospitalId().equals(sessionHospitalId) )
				throw new LogicalException(ErrorCode.AUTH_006);	// 입력된 병원 정보 상의 hospitalId와 세션 hospitalId가 불일치
			
			
			hospital.setUserId(sessionUserId);	// 등록자 ID를 추가
						
			
			// 좌표 변환
			CoordUtil coordUtil = new CoordUtil();
			String[] coord = coordUtil.transGRS80toWGS84(hospital.getLocation().getEntX(), hospital.getLocation().getEntY());
			hospital.getLocation().setLatLonX(coord[0]);
			hospital.getLocation().setLatLonY(coord[1]);
			
			System.out.println("병원 수정 : " + hospital);
			commonMapper.updateHospital(hospital);
			
			// USER_INFO에 hospitalId 추가
			int updatedRows = commonMapper.updateHospitalIdOfHospitalUser(hospital.getUserId(), hospital.getHospitalId());
			if ( updatedRows != 1 ) {
				throw new Exception("병원 정보 등록 : USER 테이블(" + hospital.getUserId() + ")에 병원ID(" + hospital.getHospitalId() + ") 추가 실패 ==> " + updatedRows);
			}
			
			Hospital hospitalCreated = commonMapper.getHospitalByHospitalId(hospital.getHospitalId());
			res.setResponse(hospitalCreated);
			
			trxMan.commit(trxStatus);				// COMMIT
		} catch(Exception ex) {
			res.setException(ex);
			try { trxMan.rollback(trxStatus); } catch(Exception ignore) { ignore.printStackTrace(); }
		}
		
		return new ResponseEntity<JsonResponse<Hospital>>(res, HttpStatus.OK);	
	}
	
	/************************* 병원 기본 정보 조회 by 사용자ID *******************************/
	@RequestMapping(value="/user/{userId}/hospital/basicInfo/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Hospital>> getHospitaBisicInfolByHospitalId(
										@PathVariable("userId")	Long userId,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		
		JsonResponse<Hospital> res = new JsonResponse<Hospital>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			Hospital hospitalCreated = commonMapper.getHospitalBasicInfoByUserId(userId);
			res.setResponse(hospitalCreated);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Hospital>>(res, HttpStatus.OK);	
	}
	
	/**************************************** 병원 조회 ****************************************/
	@RequestMapping(value="/hospital/{hospitalId}/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Hospital>> getHospitalByHospitalId(
										@PathVariable("hospitalId")	Long hospitalId,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		
		JsonResponse<Hospital> res = new JsonResponse<Hospital>();
		try {
			getHospitalUserSession(httpRequest, httpResponse);
			
			Hospital hospitalCreated = commonMapper.getHospitalByHospitalId(hospitalId);
			res.setResponse(hospitalCreated);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Hospital>>(res, HttpStatus.OK);	
	}
	
	/**************************************** 병원 조회 ****************************************/
	@RequestMapping(value="/user/{userId}/hospital/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Hospital>> getHospitalByHospitalUserId(
										@PathVariable("userId")	Long userId,
										HttpServletRequest httpRequest,
										HttpServletResponse httpResponse
			) {
		
		JsonResponse<Hospital> res = new JsonResponse<Hospital>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);						
					
			
			Hospital hospital = commonMapper.getHospitalByHospitalUserId(userId);
			
			
			if ( hospital != null ) {
				Long sessionHospitalId = session.getHospitalId();
				
				if ( !hospital.getUserId().equals(userId) ) {
					System.out.println("** 불일치 :  병원의 사용자 ID : " + hospital.getUserId() + " <> 요청된 사용자 ID : " + userId);
					throw new LogicalException(ErrorCode.AUTH_007);		// 로딩된 병원 정보의 USER_ID와 요청된 USER_ID가 불일치
				}
				
				Long sessionUserId = session.getUserId();
				if ( !hospital.getUserId().equals(sessionUserId) )
					throw new LogicalException(ErrorCode.AUTH_005);	// 입력된 병원 정보 상의 userId와 세션 userId가 불일치
				
				if ( !hospital.getHospitalId().equals(sessionHospitalId) )
					throw new LogicalException(ErrorCode.AUTH_006);	// 입력된 병원 정보 상의 hospitalId와 세션 hospitalId가 불일치
			}
			
			res.setResponse(hospital);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Hospital>>(res, HttpStatus.OK);	
	}
	
	
	
	
	
	
	
	
	
	
	
}
