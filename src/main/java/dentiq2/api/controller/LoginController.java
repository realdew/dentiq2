package dentiq2.api.controller;



import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dentiq2.api.ErrorCode;
import dentiq2.api.LogicalException;
import dentiq2.api.controller.JsonResponse;
import dentiq2.api.mapper.CommonMapper;
import dentiq2.api.model.User;
import dentiq2.api.util.UserSession;
import dentiq2.api.util.UserSessionManager;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class LoginController {
	
	@Autowired CommonMapper commonMapper;
	
	// 로그인
	@RequestMapping(value="/login2/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<String>> login2(
	//public ResponseEntity<JsonResponse<User>> login(
			@RequestParam(value="email",			required=true)	String email,
			@RequestParam(value="password",			required=true)	String password,
			@RequestParam(value="keepingLoginType",	required=false)	String keepingLoginType,
			HttpServletResponse httpResponse
			) {		
				
		JsonResponse<String> res = new JsonResponse<String>();
		try {
			User user = commonMapper.getUserMinimalByEmail(email);
			if ( user == null ) {
				throw new LogicalException(ErrorCode.USER_001, email);	// 해당 사용자(%1)는 가입되어 있지 않습니다
			}
			
			String passwordEncrypted = commonMapper.encryptPassword(password);
			if ( !user.getPasswordEncrypted().equals(passwordEncrypted) ) {
				throw new LogicalException(ErrorCode.USER_002);	// 비밀번호가 일치하지 않습니다
			}
			
			//TODO 비밀번호 3번 틀리면 막아야 하나?
			
			
			
			
			if ( user.getIsForcedChangePassword()!=null && user.getIsForcedChangePassword() ) {
				//TODO 비밀번호 반드시 변경해야 함
				System.out.println("임시비밀번호 발급 대상임 : 비밀번호 강제 변경 필요");
				res.setResponse("FORCE_CHANGE_PASSWORD");
				
				// 일단 토큰은 발급해 본다.
				UserSessionManager sesMan = UserSessionManager.create();			
				sesMan.issueToken(httpResponse, user, keepingLoginType);
				
			} else {
				UserSessionManager sesMan = UserSessionManager.create();			
				sesMan.issueToken(httpResponse, user, keepingLoginType);
				res.setResponse(null);
			}
						
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<String>>(res, HttpStatus.OK);	
	}
	
	/*
	// 로그인
	@RequestMapping(value="/login/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<User>> login(
	//public ResponseEntity<JsonResponse<String>> login(
			@RequestParam(value="email",	required=true) String email,
			@RequestParam(value="password",	required=true) String password,
			HttpServletResponse httpResponse
			) {		
				
		JsonResponse<User> res = new JsonResponse<User>();
		try {
			User user = commonMapper.getUserMinimalByEmail(email);
			if ( user == null ) {
				throw new LogicalException(ErrorCode.USER_001, email);	// 해당 사용자(%1)는 가입되어 있지 않습니다
			}
			
			String passwordEncrypted = commonMapper.encryptPassword(password);
			if ( !user.getPasswordEncrypted().equals(passwordEncrypted) ) {
				throw new LogicalException(ErrorCode.USER_002);	// 비밀번호가 일치하지 않습니다
			}
			
			//TODO 비밀번호 3번 틀리면 막아야 하나?
			
			user.filter();
			
			
			UserSession userSession = new UserSession();
			userSession.setUserId(user.getUserId());
			userSession.setUserType(user.getUserType());
			userSession.setHospitalId(user.getHospitalId());
			userSession.setEmail(user.getEmail());
			userSession.setKeepingLoginType(user.getKeepingLoginType());
			
			UserSessionManager sesMan = UserSessionManager.get();
			sesMan.issueToken(httpResponse, userSession);
			
			String token = sesMan.generateToken(userSession);
			user.setToken(token);
			
			res.setResponse(user);
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.OK);	
	}
	*/
	
	
	
	
	// 로그아웃
	@RequestMapping(value="/logout/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<User>> login(
			//@RequestParam(value="userId",	required=true) Long userId,
			HttpServletResponse httpResponse
			) {
		
		JsonResponse<User> res = new JsonResponse<User>();
		try {			
			UserSessionManager sesMan = UserSessionManager.create();
			sesMan.revokeToken(httpResponse);
			
			// 임시.
			System.out.println("로그아웃");
			
			
			res.setResponse(null);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.OK);
	}
	
	
	// 회원가입
	@RequestMapping(value="/user/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<User>> createUser(
										@RequestBody User userReq,
										HttpServletResponse httpResponse) {
		
		JsonResponse<User> res = new JsonResponse<User>();
		try {
			//TODO 사업자번호 중복확인
			
			//TODO EMAIL 중복확인
			
			commonMapper.createUser(userReq);
			
			User user = commonMapper.getUserMinimalById(userReq.getUserId());
			
			user.filter();
			
						
//			UserSession userSession = new UserSession();
//			userSession.setUserId(user.getUserId());
//			userSession.setUserType(user.getUserType());
//			userSession.setHospitalId(user.getHospitalId());
//			userSession.setEmail(user.getEmail());
//			userSession.setKeepingLoginType(user.getKeepingLoginType());
			
			UserSessionManager sesMan = UserSessionManager.create();
			sesMan.issueToken(httpResponse, user, null);
			
			res.setResponse(user);			
			
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.CREATED);
	}
	
	
	/* 비밀번호 변경 */
	@RequestMapping(value="/user/changePassword/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<String>> changePassword(
			@RequestParam(value="email",		required=true)		String email,
			@RequestParam(value="oldPassword",	required=true)		String oldPassword,
			@RequestParam(value="newPassword",	required=true)		String newPassword,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse
			) {		
		JsonResponse<String> res = new JsonResponse<String>();
		try {
			
			// 1. 세션 확인해야 한다.
			dentiq2.api.util.UserSessionManager sesMan = dentiq2.api.util.UserSessionManager.create();			
			UserSession session = null;
			try {
				session = sesMan.verifyToken(httpRequest, httpResponse);
			} catch(Exception ex) {
				throw ex;
			}
			if ( session==null ) throw new LogicalException(ErrorCode.AUTH_901);	// 로그인되어 있지 않습니다.
			
			if ( !session.getEmail().equals(email) ) {				// 세션 email과 입력된 email이 동일한지 확인한다.
				throw new LogicalException(ErrorCode.AUTH_911);		// 요청된 이메일주소가 세션의 정보와 일치하지 않습니다.
			}
			
			
			// 2. 기존에 존재하는 email인지 확인한다.
			int emailCnt = commonMapper.countUsersByEmail(email);
			if ( emailCnt != 1 ) {
				throw new LogicalException(ErrorCode.USER_011);		// 등록된 이메일이 아닙니다.
			}
			
			
			// 3. 해당 ID의 비밀번호 일치 여부 확인한다.
			int checkCnt = commonMapper.countUserWithEmailAndPassword(email, oldPassword);
			if ( checkCnt != 1 ) {
				System.out.println("비밀번호 일치 안함 : " + checkCnt);
				throw new LogicalException(ErrorCode.USER_002);		// 비밀번호가 일치하지 않습니다.
			}
			
			
			// 4. 비밀번호를 수정한다.
			int updatedRows = commonMapper.updatePasswordForChange(email, oldPassword, newPassword);
			if ( updatedRows != 1 ) {
				throw new LogicalException(ErrorCode.USER_012);				// 비밀번호 변경에 실패했습니다.
			}
			
			// 5. 확인 메일을 발송한다.
//			SimpleMailMessage mailMessage = createMailMessgaeForChangePassword(email);
//			mailSender.send(mailMessage);
			
			res.setResponse("OK");
			
		} catch(Exception ex) {
			res.setException(ex);
		}		
		return new ResponseEntity<JsonResponse<String>>(res, HttpStatus.OK);		
	}
	
	
	@Autowired private JavaMailSender mailSender;
	
	/* 비밀번호 발송 */
	@RequestMapping(value="/user/resetPassword/", method=RequestMethod.POST)
	public ResponseEntity< JsonResponse< String > > resetPasswordAndSendEmail(
			@RequestParam(value="email",		required=true)		String email
			) {		
		JsonResponse<String> res = new JsonResponse<String>();
		try {
			// 1. 기존에 존재하는 email인지 확인한다.
			int emailCnt = commonMapper.countUsersByEmail(email);
			if ( emailCnt != 1 ) {
				throw new LogicalException(ErrorCode.USER_011);		// 등록된 이메일이 아닙니다.
			}
			
			// 2. 새로운 비밀번호 난수 생성
			String tempPassword = generateTempPassword();
						
			// 3. DB에 비밀번호 변경. 
			//		만일 메일 송신에 실패했다고 하더라도, 사용자는 이미 비밀번호를 잊은 상태이므로
			//		처음부터 다시하는 것이 문제되지 않을 것임.
			int updatedRows = commonMapper.updatePasswordForReset(email, tempPassword);
			if ( updatedRows != 1 ) {
				throw new LogicalException(ErrorCode.USER_012);				// 비밀번호 변경에 실패했습니다.
			}			
			
			// 4. 메일 송신
			SimpleMailMessage mailMessage = createMailMessgaeForResetPassword(email, tempPassword);
			mailSender.send(mailMessage);
			
			res.setResponse("OK");
			
		} catch(Exception ex) {
			res.setException(ex);
		}		
		return new ResponseEntity<JsonResponse<String>>(res, HttpStatus.OK);		
	}
	
	private SimpleMailMessage createMailMessgaeForResetPassword(String email, String tempPassword) {
		
		String text = "임시 비밀번호가 발급되었습니다. 다음의 임시 비밀번호로 로그인해 주십시오.\n"
					+ tempPassword + "\n\n"
					+ ""	// 또는 다음의 링크를 눌러주셔도 로그인됩니다.
					+ "임시 비밀번호로 로그인된 후에는 비밀번호를 변경해주시기 바랍니다.\n"
					+ "감사합니다.";
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[덴탈플러스] 회원님의 임시비밀번호가 발급되었습니다.");
		message.setText(text);
		
		return message;
	}
	
	// 10자리 임시 비밀번호 생성
	private String generateTempPassword() {
		String pw = "";
		for ( int i=0; i<6; i++ ) {
			int random = (int) ( Math.random()*(charsForPassword.length));
			pw += charsForPassword[random];
		}
		
		int random1 = (int) ( Math.random()*(charsForPasswordUpperCase.length));
		pw += charsForPasswordUpperCase[random1];
		int random2 = (int) ( Math.random()*(charsForPasswordLowerCase.length));
		pw += charsForPasswordLowerCase[random2];
		int random3 = (int) ( Math.random()*(charsForPasswordNumber.length));
		pw += charsForPasswordNumber[random3];
		int random4 = (int) ( Math.random()*(charsForPasswordSymbol.length));
		pw += charsForPasswordSymbol[random4];
		
		return pw;
	}
	
	private static final char charsForPasswordUpperCase[] = new char[] { 
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	private static final char charsForPasswordLowerCase[] = new char[] {            
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	private static final char charsForPasswordNumber[] = new char[] { 
            '1','2','3','4','5','6','7','8','9','0'};
	
	private static final char charsForPasswordSymbol[] = new char[] {
            '!','@','#','$','%','^','&','*','(',')'};
	
	private static final char charsForPassword[] = new char[] { 
            '1','2','3','4','5','6','7','8','9','0', 
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z', 
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z', 
            '!','@','#','$','%','^','&','*','(',')'};
	
	
	/* ID 및 사업자 번호 중복 여부 확인 */
	@RequestMapping(value="/checkAvailable/", method=RequestMethod.GET)
	public ResponseEntity< JsonResponse< Map<String, Boolean> > > checkEmail(
			@RequestParam(value="email",		required=false)		String email,
			@RequestParam(value="bizRegNo",		required=false)		String bizRegNo
			) {		
		JsonResponse<Map<String, Boolean>> res = new JsonResponse<Map<String, Boolean>>();
		try {
			Map<String, Boolean> result = new Hashtable<String, Boolean>();
			
			if ( email!=null && !email.trim().equals("") ) {
				if ( availableEmail(commonMapper, email) )	result.put("email", true);
				else										result.put("email", false);
			}
			
			if ( bizRegNo!=null && !bizRegNo.trim().equals("") ) {
				if ( availableBizRegNo(commonMapper, email) )	result.put("bizRegNo", true);
				else											result.put("bizRegNo", false);
			}
			
			res.setResponse(result);
			
		} catch(Exception ex) {
			res.setException(ex);
		}		
		return new ResponseEntity<JsonResponse<Map<String, Boolean>>>(res, HttpStatus.OK);		
	}
	
	/* ID 중복 확인 */
	private boolean availableEmail(CommonMapper internalMapper, String email) throws Exception {
		int cnt = internalMapper.countUsersByEmail(email);
		if ( cnt == 0 ) return true;
		else return false;
	}
	
	/* 사업자 번호 중복 여부 확인 */
	private boolean availableBizRegNo(CommonMapper internalMapper, String bizRegNo) throws Exception {
		int cnt = internalMapper.countUsersByBizRegNo(bizRegNo);
		if ( cnt == 0 ) return true;
		else return false;
	}
	

}
