package dentiq2.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import dentiq2.api.ErrorCode;
import dentiq2.api.LogicalException;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.Hospital;
import dentiq2.api.model.JobAd;
import dentiq2.api.model.PaymentData;
import dentiq2.api.util.DateUtil;
import dentiq2.api.util.UserSession;
import dentiq2.iamport.IamportClient;
import dentiq2.iamport.response.IamportResponse;
import dentiq2.iamport.response.Payment;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class HospitalPaymentController {
	
	public static final Long JOB_AD_UPGRADE_FEE_FOR_ANNUAL_MEMBERSHIP = new Long(100);	// 연간회원 공고 업그레이드 요금 : 4900원
	public static final Long JOB_AD_UPGRADE_FEE_FOR_NORMAL_MEMBERSHIP = new Long(200);	// 일반회원 공고 업그레이드 요금 : 9900원
	
	public static final int PREMIER_JOB_AD_MAX_PERIOD_DAY = 31;							// 프리미어 광고 최대기간 : 31일
	
	public static final Long ANNUAL_MEMBERSHIP_FEE = new Long(99000);					// 연간회원 요금 : 99000원
	
	
	
	// 공고 업그레이드 전 기초 데이터 확인
	@RequestMapping(value="/hospital/{hospitalId}/checkJobAdUpgradable/{jobAdId}/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<Map<String, Object>>> prepareUpgradeJobAd(
							@PathVariable("hospitalId")							Long hospitalId,
							@PathVariable("jobAdId")							Long jobAdId,
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse) {
		
		JsonResponse<Map<String, Object>> res = new JsonResponse<Map<String, Object>>();
		try {
//			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
//			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);			
			
			Hospital hospital = commonMapper.getHospitalByHospitalId(hospitalId);
			if ( hospital == null ) throw new Exception("해당 병원을 찾을 수 없음 [" + hospitalId + "]");
			
			JobAd jobAd = commonMapper.getJobAdById(jobAdId);
			if ( !jobAd.getHospitalId().equals(hospitalId) )
				throw new Exception("해당 병원(" + hospitalId + ")의 공고(병원ID:" + jobAd.getHospitalId() + ")가 아닙니다.");
			
			if ( jobAd.isAdTypePremiere() && jobAd.getPrimierEndYyyymmdd()!=null ) {	// 프리미어 공고 플래그가 있고, 프리미어공고 종료일이 끝나지 않는 경우에는 프리미어 업그레이드 못함
				if ( Integer.parseInt(DateUtil.todayYYYYMMDD()) <= Integer.parseInt(jobAd.getPrimierEndYyyymmdd()) )
					throw new Exception("이미 업그레이드된 공고입니다.");
			}
				
			
			// 공고시작일자
			//	상시공고이면 ==> 오늘 날짜
			//	기간지정공고이면	==> 오늘이 공고시작일보다 이전이다 ==> 공고 시작일자
			//						==> 오늘이 공고시작일보다 이후 또는 같다 ==> 오늘 날짜
			
			String startDate = null;
			String endDate = null;
			if ( JobAd.HIRING_TERM_TYPE_ALWAYS.equals(jobAd.getHiringTermType()) ) {		// 상시채용이면
				startDate = DateUtil.todayYYYYMMDD();				// 시작일 지정
				endDate   = DateUtil.addDate(startDate, 0, 0, PREMIER_JOB_AD_MAX_PERIOD_DAY);	// 종료일 지정
				
			} else if ( JobAd.HIRING_TERM_TYPE_PERIODICAL.equals(jobAd.getHiringTermType()) ) {	// 기간채용이면
				String hiringStartDate = jobAd.getHiringStartDate();
				String hiringEndDate = jobAd.getHiringEndDate();
				String today = DateUtil.todayYYYYMMDD();

				// 시작일 지정
				if ( Integer.parseInt(today) >=  Integer.parseInt(hiringStartDate) )	startDate = today;
				else																	startDate = hiringStartDate;				
				
				// 종료일 지정
				if ( Integer.parseInt(today) >= Integer.parseInt(hiringEndDate) ) {	// 오늘이 공고종료일자보다 같거나(하루짜리 광고는 의미 없다) 큰 경우는 업그레이드 불가
					throw new Exception("공고 종료일(" + hiringEndDate + ")이 오늘(" + today + ")과 같거나 이전입니다.");
				}				
				endDate   = DateUtil.addDate(startDate, 0, 0, PREMIER_JOB_AD_MAX_PERIOD_DAY);
				if ( Integer.parseInt(endDate) > Integer.parseInt(hiringEndDate) )		endDate = hiringEndDate;
			}			
			
			// 단가 지정
			Long upgradeFee = null;
			if ( hospital.isAnnualMembership() )	upgradeFee = JOB_AD_UPGRADE_FEE_FOR_ANNUAL_MEMBERSHIP;		// 연간회원 : 일 4900원
			else									upgradeFee = JOB_AD_UPGRADE_FEE_FOR_NORMAL_MEMBERSHIP;		// 일반회원 : 일 9900원
			
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("startDate",			startDate);
			result.put("endDate",			endDate);
			result.put("upgradeFee",		upgradeFee);
			result.put("annualMembership",	hospital.isAnnualMembership());
			
			res.setResponse(result);
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Map<String, Object>>>(res, HttpStatus.OK);
	}
	// 1525762469116-1-WEB01-API_001
	
	@RequestMapping(value="/hospital/{hospitalId}/upgradeJobAd/{jobAdId}/paymentEnd/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<Map<String, Object>>> upgradeJobAd(
							@PathVariable("hospitalId")							Long hospitalId,
							@PathVariable("jobAdId")							Long jobAdId,
							@RequestParam(value="merchant_uid",	required=true)	String merchantUid,
							HttpServletRequest httpRequest,
							HttpServletResponse httpResponse) {
		
		JsonResponse<Map<String, Object>> res = new JsonResponse<Map<String, Object>>();
		try {
//			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
//			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			
			Hospital hospital = commonMapper.getHospitalByHospitalId(hospitalId);
			if ( hospital == null ) throw new Exception("해당 병원을 찾을 수 없음 [" + hospitalId + "]");

			JobAd jobAd = commonMapper.getJobAdById(jobAdId);			
			if ( !jobAd.getHospitalId().equals(hospitalId) )
				throw new Exception("해당 병원(" + hospitalId + ")의 공고(병원ID:" + jobAd.getHospitalId() + ")가 아닙니다.");
			
			Payment payment = null;
			try {
				IamportClient iamportClient = new IamportClient("2001016147233984", "ArbVoMpEOqMUCo14ajqwffVa2QWzIW4joffN1ErQ5dKIBMML5dilsevv4r5No96MplzrZ0i5e2NQPACU");
				IamportResponse<Payment> iamportResponse = iamportClient.paymentByMerchantUid(merchantUid);
				
				System.out.println(iamportResponse);
				
				payment = iamportResponse.getResponse();
				
				
				int updatedRows = commonMapper.endJobAdUpgradePayment(payment);
				if ( updatedRows != 1 )
					throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
				
			} catch(Exception ex) {
				ex.printStackTrace();
				throw ex;
			}
			
			if ( payment.getStatus().trim().equals("paid") ) {
				PaymentData data = commonMapper.getJobUpgradePayment(merchantUid);
				if ( data == null ) throw new Exception("해당 결제 내역을 찾을 수 없습니다. [" + merchantUid + "]");
				
				
				int updatedRows = commonMapper.updateJobAdGrade(jobAdId, data.getStartDate(), data.getEndDate());
				if ( updatedRows != 1 )
					throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", payment.getStatus());
			result.put("failReason", payment.getFailReason());
			result.put("merchant_uid", payment.getMerchantUid());
			result.put("cardName", payment.getCardName());
			result.put("applyNum", payment.getApplyNum());
			result.put("amount", payment.getAmount());
			res.setResponse(result);
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<Map<String, Object>>>(res, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value="/hospital/{hospitalId}/upgradeJobAd/{jobAdId}/paymentStart/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<PaymentData>> upgradeJobAdPayment(
						@PathVariable("hospitalId")							Long hospitalId,
						@PathVariable("jobAdId")							Long jobAdId,
						@RequestParam(value="startDate",	required=true)	String startDate,
						@RequestParam(value="period",		required=true)	Integer period,
						@RequestParam(value="amount",		required=true)	Long amount,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse) {
		JsonResponse<PaymentData> res = new JsonResponse<PaymentData>();
		try {
//			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
//			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			//String channelId = session.getChannelId();
			String channelId = "WEB01";
			
			
			Hospital hospital = commonMapper.getHospitalByHospitalId(hospitalId);
			if ( hospital == null ) throw new Exception("해당 병원을 찾을 수 없음 [" + hospitalId + "]");

			JobAd jobAd = commonMapper.getJobAdById(jobAdId);
			if ( !jobAd.getHospitalId().equals(hospitalId) )
				throw new Exception("해당 병원(" + hospitalId + ")의 공고(병원ID:" + jobAd.getHospitalId() + ")가 아닙니다.");
			
			// 웹에서 페이지가 reload되지 않은 상태에서는 checkJobAdUpgradable가 호출되지 않고 과거의 정보일 수 있으므로, 여기서 다시 check해야 한다.
			if ( jobAd.isAdTypePremiere() && jobAd.getPrimierEndYyyymmdd()!=null ) {	// 프리미어 공고 플래그가 있고, 프리미어공고 종료일이 끝나지 않는 경우에는 프리미어 업그레이드 못함
				if ( Integer.parseInt(DateUtil.todayYYYYMMDD()) <= Integer.parseInt(jobAd.getPrimierEndYyyymmdd()) )
					throw new Exception("이미 업그레이드된 공고입니다.");
			}
			
						
			// 금액계산해서 입력된 금액하고 맞는지 확인한다.
			Long amountCalculated;
			if ( hospital.isAnnualMembership() ) {	// 연간회원 : 일 4900원
				amountCalculated = JOB_AD_UPGRADE_FEE_FOR_ANNUAL_MEMBERSHIP * (long)period;
			} else {								// 일반회원 : 일 9900원
				amountCalculated = JOB_AD_UPGRADE_FEE_FOR_NORMAL_MEMBERSHIP * (long)period;
			}
			if ( !amountCalculated.equals(amount) )
				throw new Exception("요청된 금액(" + amount + ")과 계산된 금액(" + amountCalculated + ") 불일치. (기간:" + period + ", 유형: 프리미어 광고");
					
			
			String endDate = DateUtil.addDate(startDate, 0, 0, period);			
			
			String merchantUid	= generateMerchantUid(hospitalId, channelId, API_SERVER_ID);
						
			PaymentData data = new PaymentData();
			data.setPayMethod("card");
			data.setMerchantUid(merchantUid);
			data.setName("프리미어광고:결제테스트");
			data.setAmount(new BigDecimal(amount));
			data.setCustomData("프리미어 결제 : " + hospitalId + "-" + jobAdId + ", " + startDate + "~" + endDate + " (" + period + ")");
			data.setBuyerEmail("");
			data.setBuyerName("");
			
			data.setHospitalId(hospitalId);
			data.setJobAdId(jobAdId);
			data.setPaymentFor(PaymentData.PAYMENT_FOR_JOB_AD);
			data.setStartDate(startDate);
			data.setEndDate(endDate);
			data.setPeriod(period);
			
			
			// DB에 저장
			int updatedRows = commonMapper.startJobAdUpgradePayment(data);
			if ( updatedRows != 1 ) {
				throw new Exception("프리미어 광고 결제 데이터 생성 실패. 1행이 아님 [" + updatedRows + "]");
			}
			
			//updatdRows = commonMapper.updateJobAdPaymentStatus(jobAdId, status, )
			
			
			res.setResponse(data);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<PaymentData>>(res, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
	public static final String ANNUAL_MEMBERSHIP_UPGRADE_EXTEND	= "E";
	public static final String ANNUAL_MEMBERSHIP_UPGRADE_NEW	= "N";
	
	
	
//	@RequestMapping(value="/hospital/{hospitalId}/upgradeMembership/", method=RequestMethod.POST)
//	public ResponseEntity<JsonResponse<Map<String, Object>>> upgradeMembership( 
//									@PathVariable("hospitalId")							Long hospitalId,
//									@RequestParam(value="upgradeType",	required=true)	String upgradeType,
//									@RequestParam(value="amount",		required=true)	Long amount,
//									HttpServletRequest httpRequest,
//									HttpServletResponse httpResponse) {
//
//		JsonResponse<Map<String, Object>> res = new JsonResponse<Map<String, Object>>();
//		try {
////			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
////			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);			
//			
//			Hospital hospital = commonMapper.getHospitalByHospitalId(hospitalId);
//			if ( hospital == null ) throw new Exception("해당 병원을 찾을 수 없음 [" + hospitalId + "]");
//			
//			
//			String startDate;
//			String endDate;			
//			if ( ANNUAL_MEMBERSHIP_UPGRADE_EXTEND.equals(upgradeType) ) {				
//				// 이미 연간회원인지 확인.. 연간회원이어야만 함
//				if ( !hospital.isAnnualMembership() )
//					throw new Exception("현재 연간회원이 아니므로, 연간회원기간 연장은 불가능함");
//				
//				startDate = hospital.getAnnualMembershipStartYyyymmdd();
//				endDate = DateUtil.addDate(hospital.getAnnualMembershipEndYyyymmdd(), 1, 0, 1);	// 기존 종료일에서 1년 1일 더해준다.				
//				
//			} else if ( ANNUAL_MEMBERSHIP_UPGRADE_NEW.equals(upgradeType) ) {				
//				if ( hospital.isAnnualMembership() )
//					throw new Exception("이미 연간회원이므로, 신규 연간회원가입은 불가능함");
//								
//				startDate = DateUtil.todayYYYYMMDD();;
//				endDate   = DateUtil.addDate(startDate, 1, 0, 1);	// 현재 날짜에서 1년 1일 더해준다.				
//				
//			} else throw new Exception("연간회원 업그레이드 유형이 없음 [" + upgradeType + "]");
//			
//			
//			if ( !ANNUAL_MEMBERSHIP_FEE.equals(amount) )
//				throw new Exception("연간회원 업그레이드 비용이 올바르지 않음 [" + amount + "]");
//			
//			int updatedRows = commonMapper.updateAnnualMembership(hospitalId, startDate, endDate);
//			if ( updatedRows != 1 ) throw new Exception("변경된 행이 1행이 아님 [" + updatedRows + "]");
//			
//			
//			Map<String, Object> result = new HashMap<String, Object>();
//			result.put("hospitalId", hospitalId);
//			result.put("startDate", startDate);
//			result.put("endDate", endDate);
//			result.put("amount", amount);
//			
//			res.setResponse(result);		
//			
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<Map<String, Object>>>(res, HttpStatus.OK);
//	}
	
	
	
	public static final String API_SERVER_ID = "API_001";
	
	
	@Autowired CommonMapper commonMapper;

	@Autowired private PlatformTransactionManager trxMan;


	// 결제
	/*
		일반회원 ==> 연간회원 : X만원
		일반공고 ==> 프리미어 공고 :
						a. 일반회원인 경우 : 기간 * 000원
						b. 연간회원인 경우 : 기간 * 000원
	
	
	 */

	private synchronized String generateMerchantUid(Long hospitalId, String channelId, String apiServerId) throws Exception {
		
		String timestamp = "" + System.currentTimeMillis();
					
		StringBuffer sb = new StringBuffer(10);
		sb.append(timestamp);
		sb.append("-");
		sb.append(hospitalId);
		sb.append("-");
		sb.append(channelId);
		sb.append("-");
		sb.append(apiServerId);
		
		return sb.toString();
	}
	
	
//	@RequestMapping(value="/hospital/{hospitalId}/startPaymentForMembership/", method=RequestMethod.POST)
//	public ResponseEntity<JsonResponse<PaymentData>> startPaymentForMembershipUpgrade(
//						@PathVariable("hospitalId")							Long hospitalId,
//						HttpServletRequest httpRequest,
//						HttpServletResponse httpResponse) {
//		JsonResponse<PaymentData> res = new JsonResponse<PaymentData>();
//		try {
//			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
//			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
//			
//			//String channelId = session.getChannelId();
//			String channelId = "WEB01";
//			
//			Map<String, String> buyerInfo = commonMapper.getBuyerInfo(hospitalId);
//			if ( buyerInfo == null ) throw new Exception("병원 정보가 없습니다. [" + hospitalId + "]");
//			
//			String buyerEmail = buyerInfo.get("HOSPITAL_EMAIL");
//			String buyerName  = buyerInfo.get("BIZ_REG_NAME");
//			if ( buyerEmail==null || buyerEmail.trim().equals("") || buyerName==null || buyerName.trim().equals("") ) {
//				throw new Exception("병원 정보에 누락이 있습니다. [" + buyerEmail + "] [" + buyerName + "]");
//			}
//			if ( buyerInfo.get("MEMBERSHIP_TYPE").equals(Hospital.MEMBERSHIP_ANNUAL) ) {
//				throw new Exception("이미 연간회원입니다.");
//			}
//			
//			
//			
//			
//			
//			String merchantUid	= generateMerchantUid(hospitalId, channelId, API_SERVER_ID);
//			BigDecimal amount	= new BigDecimal(10);
//			String name			= "멤버쉽업그레이드:결제테스트";
//			
//			
//			PaymentData data = new PaymentData();
//			data.setPayMethod("card");
//			data.setMerchantUid(merchantUid);
//			data.setName(name);
//			data.setAmount(amount);
//			data.setBuyerEmail(buyerEmail);
//			data.setBuyerName(buyerName);
//			
//			data.setHospitalId(hospitalId);
//			data.setPaymentFor("AM");			// Annual Membership
//			
//			
//			// DB에 저장
//			int updatedRows = commonMapper.startMembershipUpgradePayment(data);
//			if ( updatedRows != 1 ) {
//				throw new Exception("결제 데이터 생성 실패. 1행이 아님 [" + updatedRows + "]");
//			}
//			
//			
//			res.setResponse(data);
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<PaymentData>>(res, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value="/hospital/{hospitalId}/endPaymentForMembership/", method=RequestMethod.POST)
//	public ResponseEntity<JsonResponse<PaymentArgument>> endPaymentForMembershipUpgrade(
//						@PathVariable("hospitalId")			Long hospitalId,
//						@RequestBody						PaymentArgument	paymentArg,
//						HttpServletRequest httpRequest,
//						HttpServletResponse httpResponse) {
//		JsonResponse<PaymentArgument> res = new JsonResponse<PaymentArgument>();
//		try {
////			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
////			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
//			
//			//String channelId = session.getChannelId();
//			String channelId = "WEB01";
//			
//			
//			
//			int updatedRows = commonMapper.endMembershipUpgradePayment(paymentArg);
//			if ( updatedRows != 1 ) {
//				throw new Exception("결제 완료 데이터 생성 실패. 1행이 아님 [" + updatedRows + "]");
//			}
//			
//			// 병원의 Membership 변경 작업 시작한다.
//			updatedRows = commonMapper.updateMembershipType(hospitalId, Hospital.MEMBERSHIP_ANNUAL);
//			if ( updatedRows != 1 ) {
//				throw new Exception("멤버쉽 변경 생성 실패. 1행이 아님 [" + updatedRows + "]");
//			}
//			
//			
//			res.setResponse(paymentArg);
//		} catch(Exception ex) {
//			res.setException(ex);
//		}
//		return new ResponseEntity<JsonResponse<PaymentArgument>>(res, HttpStatus.OK);
//	}
	
	
	
	
	private UserSession getHospitalUserSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();
		
		UserSession session = null;
		try {
			session = sesMan.verifyToken(httpRequest, httpResponse);
		} catch(Exception ex) {
			throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
		}		
		if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
		
					
		
		// DB와 검증이 완료 되었다면...
		
		if ( !session.isHospitalUser() )
			throw new LogicalException(ErrorCode.AUTH_001);	// 병원회원만 접근 가능합니다.
		
		
		return session;
	}
	
		
}


