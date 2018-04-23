package dentiq2.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class User {
	
	public static final String USER_TYPE_JOB_SEEKER		= "1";	// 회원 유형 (1: 구직(개인)회원)
	public static final String USER_TYPE_HOSPITAL		= "2";	// 회원 유형 (2: 구인(병원)회원)
	
	// 로그인상태유지(0:로그인 유지안함, 1:로그인유지)
	public static final String KEPPING_LOGIN_SESSION	= "0";	// 로그인 유지 방식 (0:timeout이 존재하는 일반 세션)
	public static final String KEEPING_LOGIN_PERM		= "1";	// 로그인 유지 방식 (2:timeout 지나면 자동연장)
	
	
	@Getter @Setter protected Long		userId;							// 회원 ID
	@Getter @Setter protected String	userType;						// 회원 유형 : '1':구직회원, '2':병원회원
	@Getter @Setter protected String	email;							// 로그인 이메일
	@Getter @Setter protected String	password;						// 로그인 비밀번호
	
	@Getter @Setter protected Long		hospitalId;
	@Getter @Setter protected String	bizRegNo;
	
	@JsonIgnore @Getter @Setter protected String	passwordEncrypted;
	@JsonIgnore @Getter @Setter protected String	useYn;
	
	
	@Getter @Setter protected String	keepingLoginType;
	
	@Getter @Setter protected String	eulaVer;
	@Getter @Setter protected String	cupiVer;
	@Getter @Setter protected String	agreementNoticeNewsYn;
	@Getter @Setter protected String	agreementHiringNewsYn;
	@Getter @Setter protected String	agreementEventYn;
	@Getter @Setter protected String	agreementAdYn;
	
	@Getter @Setter protected String	token;		// 인증 토큰
	
	@Getter protected Boolean			isForcedChangePassword;
		@JsonIgnore public void setForcedChangePasswordYn(String flag) {		// 비밀번호 반드시 변경해야 하는지 여부, MUST_CHANGE_PASSWORD_YN
			if ( flag!=null && flag.equals("Y") ) isForcedChangePassword = true;
		}
	
	
	// 보안 정보들을 필터링한다.
	public void filter() {
		this.password = null;
		this.passwordEncrypted = null;
	}
	
	

}
