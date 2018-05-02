package dentiq2.api.util;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dentiq2.api.model.User;


public class UserSessionManager {
	
	
	
	private static final long TOKEN_TIMEOUT = (long)( 10 * 1000 * 60 );		// 1분
	
	private static final String SALT =   "$2a$12$WApznUOJfkEGSmYRfnkrPO";
	
	
	public static UserSessionManager create() {
		return new UserSessionManager();
	}
	
	public void issueToken(HttpServletResponse res, User user, String keepingLoginType) throws Exception {
		
		UserSession userSession = new UserSession();
		userSession.setUserId(user.getUserId());
		userSession.setUserType(user.getUserType());
		userSession.setHospitalId(user.getHospitalId());
		userSession.setEmail(user.getEmail());
		
		userSession.setKeepingLoginType(user.getKeepingLoginType());
		
		
		//issueTokenWithSession(res, userSession, keepingLoginType);
		issueTokenWithSession(res, userSession, keepingLoginType);
	}
	
	private void issueTokenWithSession(HttpServletResponse res, UserSession session, String keepingLoginType) throws Exception {
		System.out.println("토큰 생성함");
		
		//System.out.println("테스트 SALT : " + BCrypt.gensalt(20));
		
		if ( keepingLoginType != null && ( keepingLoginType.equals(User.KEEPING_LOGIN_PERM) || keepingLoginType.equals(User.KEPPING_LOGIN_SESSION) ) ) {
			session.setKeepingLoginType(keepingLoginType);
		}
		
		Long issuedTimeAt = System.currentTimeMillis();
		Long expireTimeAt = issuedTimeAt + TOKEN_TIMEOUT;
		
		session.setIssuedTimeAt(issuedTimeAt);
		session.setExpireTimeAt(expireTimeAt);
		
		Encoder base64Encoder = Base64.getEncoder();
		
		String sessionJson = toJsonString(session);
		byte[] src = sessionJson.getBytes("UTF-8");
		String encodedSession = base64Encoder.encodeToString(src);
		
		// hash 생성
		byte[] hashBytes = hmac(encodedSession.getBytes("UTF-8"));
		String encodedHash = base64Encoder.encodeToString(hashBytes);
		
		
		String token = encodedSession + "." + encodedHash;
		
		
		res.addHeader("Access-Control-Expose-Headers", UserSession.TOKEN_NAME);
		res.addHeader(UserSession.TOKEN_NAME, token);
		
		System.out.println("세션 발급 완료 : " + token);
	}
	
	SecretKeySpec signKey = new SecretKeySpec(SALT.getBytes(), "HmacSHA256");
	
	private byte[] hmac(byte[] src) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signKey);
		return mac.doFinal(src);
	}
	
	public UserSession verifyToken(HttpServletRequest req, HttpServletResponse res) throws Exception {
				
		
		String tokenStr = req.getHeader(UserSession.TOKEN_NAME);
		System.out.println("verifyToken() : 토큰 검증 요청됨 [" + tokenStr + "]");
		//if ( tokenStr==null ) throw new Exception("로그인되어 있지 않습니다.");
		if ( tokenStr==null ) return null;
		
		
		String[] tokenPart = tokenStr.split("\\.");
		if ( tokenPart.length != 2 ) {
			//throw new Exception("올바른 토큰 형식 아님 [" + tokenStr + "]");
			System.out.println("verifyToken() : 올바른 토큰 형식이 아님 [" + tokenStr + "]");
			return null;
		}		
		String encodedSession = tokenPart[0];
		String encodedHash = tokenPart[1];
			
		UserSession session = null;
		try {			
			Decoder base64decoder = Base64.getDecoder();
			byte[] decodedSessionBytes = base64decoder.decode(encodedSession);
			
			String sessionJson = new String(decodedSessionBytes, "UTF-8");
			System.out.println("verifyToken() : 디코딩 완료 : " + sessionJson);
			
			
			// 해쉬 생성
			Encoder base64encoder = Base64.getEncoder();
			byte[] hashBytes = hmac(encodedSession.getBytes("UTF-8"));
			
			// 해쉬 비교
			String encodedHashCheck = base64encoder.encodeToString(hashBytes);
			System.out.println("verifyToken() : 해쉬 비교 : " + encodedHash + "    " + encodedHashCheck);
			if ( !encodedHash.equals(encodedHashCheck) ) {
				throw new Exception("HASH 훼손되었음");
			}
			
			
			session = fromJsonString(sessionJson);	// 임시
		} catch(Exception ex) {
			ex.printStackTrace();
			//throw new Exception("사용자 세션 토큰 검증에 실패했습니다. (" + ex + ")");
			System.out.println("verifyToken() : 사용자 세션 토큰 검증에 실패했습니다. (" + ex + ")");
			return null;
		}
		
		if ( session.getUserId() == null ) {
			//throw new Exception("사용자 세션에 userId가 존재하지 않습니다.");
			System.out.println("verifyToken() : 사용자 세션에 userId가 존재하지 않습니다.");
			return null;
		}
		if ( session.getUserType() == null || session.getUserType().trim().equals("") ) {
			//throw new Exception("사용자 세션에 userType이 존재하지 않습니다.");
			System.out.println("verifyToken() : 사용자 세션에 userType이 존재하지 않습니다.");
			return null;
		}
		
		
		Long currentTime = System.currentTimeMillis();

		if ( session.getExpireTimeAt().compareTo(currentTime) < 0 ) {
			System.out.println("verifyToken() : 세션 새로 발급");
//			if ( !session.getKeepingLoginType().equals(User.KEEPING_LOGIN_PERM) ) {
//				throw new Exception("세션이 만료되었습니다.");
//			} else {
				issueTokenWithSession(res, session, null);	// 기존 keepingLoginType 그대로 유지
//			}
		}
		
		return session;
	}

	
	public void revokeToken(HttpServletResponse res) throws Exception {
		
	}
	
	
	
	
	protected static String toJsonString(UserSession userSession) throws Exception {
		return JsonUtil.toJson(userSession);
	}
	protected static UserSession fromJsonString(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<UserSession>() {});
	} 
}
