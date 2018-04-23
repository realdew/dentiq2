package dentiq2.api.controller;



import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import dentiq2.api.util.UserSessionManager;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class LoginController {
	
	@Autowired CommonMapper commonMapper;
	
	// 로그인
	@RequestMapping(value="/login2/", method=RequestMethod.POST)
	public ResponseEntity<JsonResponse<User>> login2(
	//public ResponseEntity<JsonResponse<String>> login(
			@RequestParam(value="email",			required=true)	String email,
			@RequestParam(value="password",			required=true)	String password,
			@RequestParam(value="keepingLoginType",	required=false)	String keepingLoginType,
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
			
						
			UserSessionManager sesMan = UserSessionManager.create();
			
			sesMan.issueToken(httpResponse, user, keepingLoginType);
						
			res.setResponse(null);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<User>>(res, HttpStatus.OK);	
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
			ex.printStackTrace();
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
