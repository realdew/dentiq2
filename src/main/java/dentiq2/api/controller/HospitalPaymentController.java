package dentiq2.api.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import dentiq2.api.ErrorCode;
import dentiq2.api.LogicalException;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.Hospital;
import dentiq2.api.model.PaymentArgument;
import dentiq2.api.model.PaymentData;
import dentiq2.api.util.UserSession;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class HospitalPaymentController {
	
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
	
	
	@RequestMapping(value="/hospital/{hospitalId}/startPaymentForMembership/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<PaymentData>> startPaymentForMembershipUpgrade(
						@PathVariable("hospitalId")							Long hospitalId,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse) {
		JsonResponse<PaymentData> res = new JsonResponse<PaymentData>();
		try {
			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			//String channelId = session.getChannelId();
			String channelId = "WEB01";
			
			Map<String, String> buyerInfo = commonMapper.getBuyerInfo(hospitalId);
			if ( buyerInfo == null ) throw new Exception("병원 정보가 없습니다. [" + hospitalId + "]");
			
			String buyerEmail = buyerInfo.get("HOSPITAL_EMAIL");
			String buyerName  = buyerInfo.get("BIZ_REG_NAME");
			if ( buyerEmail==null || buyerEmail.trim().equals("") || buyerName==null || buyerName.trim().equals("") ) {
				throw new Exception("병원 정보에 누락이 있습니다. [" + buyerEmail + "] [" + buyerName + "]");
			}
			if ( buyerInfo.get("MEMBERSHIP_TYPE").equals(Hospital.MEMBERSHIP_ANNUAL) ) {
				throw new Exception("이미 연간회원입니다.");
			}
			
			
			
			
			
			String merchantUid	= generateMerchantUid(hospitalId, channelId, API_SERVER_ID);
			BigDecimal amount	= new BigDecimal(10);
			String name			= "멤버쉽업그레이드:결제테스트";
			
			
			PaymentData data = new PaymentData();
			data.setPayMethod("card");
			data.setMerchantUid(merchantUid);
			data.setName(name);
			data.setAmount(amount);
			data.setBuyerEmail(buyerEmail);
			data.setBuyerName(buyerName);
			
			data.setHospitalId(hospitalId);
			data.setPaymentFor("AM");			// Annual Membership
			
			
			// DB에 저장
			int updatedRows = commonMapper.startMembershipUpgradePayment(data);
			if ( updatedRows != 1 ) {
				throw new Exception("결제 데이터 생성 실패. 1행이 아님 [" + updatedRows + "]");
			}
			
			
			res.setResponse(data);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<PaymentData>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="/hospital/{hospitalId}/endPaymentForMembership/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<PaymentArgument>> endPaymentForMembershipUpgrade(
						@PathVariable("hospitalId")			Long hospitalId,
						@RequestBody						PaymentArgument	paymentArg,
						HttpServletRequest httpRequest,
						HttpServletResponse httpResponse) {
		JsonResponse<PaymentArgument> res = new JsonResponse<PaymentArgument>();
		try {
//			UserSession session = getHospitalUserSession(httpRequest, httpResponse);
//			if ( !session.getHospitalId().equals(hospitalId) )	throw new LogicalException(ErrorCode.AUTH_002);
			
			//String channelId = session.getChannelId();
			String channelId = "WEB01";
			
			
			
			int updatedRows = commonMapper.endMembershipUpgradePayment(paymentArg);
			if ( updatedRows != 1 ) {
				throw new Exception("결제 완료 데이터 생성 실패. 1행이 아님 [" + updatedRows + "]");
			}
			
			// 병원의 Membership 변경 작업 시작한다.
			updatedRows = commonMapper.updateMembershipType(hospitalId, Hospital.MEMBERSHIP_ANNUAL);
			if ( updatedRows != 1 ) {
				throw new Exception("멤버쉽 변경 생성 실패. 1행이 아님 [" + updatedRows + "]");
			}
			
			
			res.setResponse(paymentArg);
		} catch(Exception ex) {
			res.setException(ex);
		}
		return new ResponseEntity<JsonResponse<PaymentArgument>>(res, HttpStatus.OK);
	}
	
	
	
	
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


