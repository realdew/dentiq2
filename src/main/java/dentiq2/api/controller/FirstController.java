package dentiq2.api.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dentiq2.api.code.LocationCodeManager;
import dentiq2.api.code.LocationCodeManager.LocationSido;
import dentiq2.api.code.SystemConstants;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.JobAd;
import dentiq2.api.model.JobAttrGroup;
import dentiq2.api.model.Location;
import dentiq2.api.model.LocationSummary;
import dentiq2.api.model.Notice;
import dentiq2.api.model.Resume;
import dentiq2.api.model.JobAdSummary;
import dentiq2.api.util.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class FirstController {
	
	@Autowired CommonMapper commonMapper;
	
	
	
	
	@RequestMapping(value="/code/location/tree/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<Map<String, LocationSido>> > listLocationTree() {
		LocationCodeManager locationCode = LocationCodeManager.getInstance();
		
		JsonResponse<Map<String, LocationSido>> res = new JsonResponse<Map<String, LocationSido>>();
		Map<String, LocationSido> locationTree = locationCode.getLocationTree();
		res.setResponse(locationTree);
		return new ResponseEntity< JsonResponse<Map<String, LocationSido>> >(res, HttpStatus.OK);
	}
	@RequestMapping(value="/code/location/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<List<Location>> > listLocation() {
		LocationCodeManager locationCode = LocationCodeManager.getInstance();
		
		JsonResponse<List<Location>> res = new JsonResponse<List<Location>>();
		List<Location> locationList = locationCode.getLocationList();
		res.setResponse(locationList);
		return new ResponseEntity< JsonResponse<List<Location>> >(res, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value="/code/constants/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<SystemConstants> > getSystemConstants() {
		
		JsonResponse<SystemConstants> res = new JsonResponse<SystemConstants>();
		res.setResponse(SystemConstants.getInstance());
		return new ResponseEntity< JsonResponse<SystemConstants> >(res, HttpStatus.OK);
	}
	
	
	
	
	
	
	@RequestMapping(value="/resume/{resumeId}/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<Resume> > getResume(
				@PathVariable("resumeId")		Long resumeId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<Resume> res = new JsonResponse<Resume>();
		try {
			
			//보안 권한 문제 관련하여, 먼저 이력서를 가져오기로 한다.
			Resume resume = commonMapper.getResumeById(resumeId);
			
//			if ( resume == null )
//				throw new Exception("해당 이력서 존재하지 않음 [" + resumeId + "]");
			
			/*
			UserSessionManager sesMan = UserSessionManager.get();
			UserSession session = sesMan.verifyToken(httpRequest, httpResponse);
			
			Long resumeOwnerId = resume.getUser().getUserId();
			
			if ( resumeOwnerId.equals(session.getUserId()) ) {		// 조회자가 작성자 본인이면 OK
				// OK
				
			} else if ( session.getUserType().equals(User.USER_TYPE_HOSPITAL) ) {	// 조회자가 병원이면
				
				if ( resume.isOpened() ) {	// 병원에게 공개된 이력서이면, OK
					// OK
					
				} else {					// 비공개 이력서이면, 지원받은 병원만 볼 수 있음
					Long hospitalUserId = session.getUserId();
					commonMapper.checkUserAppliedToHospitalByJobSeekerIdAndHosptailUserId(resumeOwnerId, hospitalUserId);
					
				}
			}
			*/
			
			res.setResponse(resume);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<Resume>>(res, HttpStatus.OK);
	}
	
	
	
	
	
	
	/*
	 * 공고 조회 : 공고는 누구나 볼 수 있다. 따라서 권한 확인 없음
	 */
	@RequestMapping(value="/jobAd/{jobAdId}/detail/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<JobAd> > getJobAdDetail(
				@PathVariable("jobAdId")							Long jobAdId,
				@RequestParam(value="userId",	required=false)		Long userId,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<JobAd> res = new JsonResponse<JobAd>();
		try {			
			JobAd jobAd = commonMapper.getJobAdFullyById(jobAdId);
			
			// 해당 기능 필요 없음 WEB에서 처리함. 이렇게 하려면 관심병원/스크랩여부/지원여부 까지 다 보여주어야 함
//			// userId가 입력되면, 해당 공고가 관심병원의 것인지를 조회하여서 보여준다.
//			if ( userId!=null ) {
//				int cnt = commonMapper.isJobSeekerSInterestHospital(userId, jobAd.getHospitalId());
//				if ( cnt > 0 )	jobAd.getHospital().setInterestedByUserId(userId);
//			}
			
			res.setResponse(jobAd);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<JobAd>>(res, HttpStatus.OK);
	}
	
	
	
	
	@RequestMapping(value="/jobAd/summary/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<JobAdSummary> > summaryJobAd(
				@RequestParam(value="sidoCode",		required=false)		String sidoCode,			// 주의: 스칼라 타입 (List 아님)
				@RequestParam(value="siguCode",		required=false)		List<String> siguCodeList,	// 주의 : 데이터 검색 조건은 아님. 요청된 것 marking하기 위함
				@RequestParam(value="attr",			required=false)		List<String> attrStrList,
				HttpServletRequest httpRequest,
				HttpServletResponse httpResponse
			) {
		JsonResponse<JobAdSummary> res = new JsonResponse<JobAdSummary>();
		try {
			
			List<JobAttrGroup> jobAttrGroupList = JobAttrGroup.createJobAttrGroupFromStringList(attrStrList);	// 속성 그룹 리스트 생성
			List<LocationSummary> locSumList= null;			
			if ( sidoCode!=null && sidoCode.trim().length()==2 ) {
				locSumList = commonMapper.summarySpecificSido(sidoCode.trim(), jobAttrGroupList);	// 특정 시도에 대해 요약(==> 하위 시구들)
			} else {
				locSumList = commonMapper.summaryWhole(jobAttrGroupList);							// 전체(대한민국)에 대해 요약(==> 시도들)
			}
			
			JobAdSummary summary = new JobAdSummary(sidoCode, siguCodeList, attrStrList, locSumList);
			
			res.setResponse(summary);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<JobAdSummary>>(res, HttpStatus.OK);
	}
	
	
	
	
	
	
	/*
	 * 공고 목록 조회는 누구나 가능. 권한 확인 없음
	 */
	@ApiOperation(value = "공고목록조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sidoCode", value = "시도코드", paramType = "query"),
		@ApiImplicitParam(name = "siguCode", value = "시구코드", paramType = "query")
		//@ApiImplicitParam(name = "siguCode", value = "내용", required = true, dataType = "string", paramType = "query", defaultValue = ""),
	})
	@RequestMapping(value="/jobAd/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<List< JobAd> > > listJobAd(
			//@RequestParam(value="location",		required=false)		List<String> locationCodeList,
			@RequestParam(value="sidoCode",		required=false)		List<String> sidoCodeList,
			@RequestParam(value="siguCode",		required=false)		List<String> siguCodeList,
			@RequestParam(value="adType",		required=false)		String adType,
			@RequestParam(value="attr",			required=false)		List<String> attrStrList,
			@RequestParam(value="pageNo",		required=false)		Integer pageNo,
			@RequestParam(value="pageSize",		required=false)		Integer pageSize
			) {
		
		JsonResponse<List<JobAd>> res = new JsonResponse<List<JobAd>>();
		try {
			List<JobAttrGroup> jobAttrGroupList = JobAttrGroup.createJobAttrGroupFromStringList(attrStrList);	// 속성 그룹 리스트 생성
			PageInfo pageInfo = new PageInfo(pageNo, pageSize);		// 페이지 정보 생성
			
			List<JobAd> jobAdList = commonMapper.listJobAd(sidoCodeList, siguCodeList, jobAttrGroupList, adType, pageInfo.startIndexOnPage, pageInfo.itemCntPerPage);
			res.setResponse(jobAdList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<JobAd>>>(res, HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value="/notice/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse<List<Notice> > > listNoticeTitle() {
		
		JsonResponse<List<Notice>> res = new JsonResponse<List<Notice>>();
		try {
			List<Notice> noticeList = commonMapper.listNoticeTitle();
			
			res.setResponse(noticeList);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<List<Notice>>>(res, HttpStatus.OK);
		
	}
	
	
}
