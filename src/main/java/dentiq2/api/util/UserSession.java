package dentiq2.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonInclude(Include.NON_NULL)
public class UserSession {
	
	public static final String TOKEN_NAME = "X-ENQUAL-DENTALPLUST-TOKEN";
	
	
	@Getter @Setter private		Long	userId;			
	@Getter @Setter private		String	userType;			// user type: '1':구직회원, '2':병원회원
	@Getter @Setter protected	String	email;				// 로그인 이메일
	@Getter @Setter protected	Long	hospitalId;			// 병원 ID. userType==2인 경우에만
	@Getter @Setter protected	String	keepingLoginType;	// 로그인상태유지(0:로그인 유지안함, 1:로그인유지)
	
		
	@Getter @Setter private		Long	issuedTimeAt;		// 토큰 발급 시각
	@Getter @Setter private		Long	expireTimeAt;		// 토큰 종료 시각
	
	@JsonIgnore public boolean isHospitalUser() {
		if ( this.userType!=null && this.userType.equals(User.USER_TYPE_HOSPITAL) ) return true;
		return false;
	}
	@JsonIgnore public boolean isJobSeekerUser() {
		if ( this.userType!=null && this.userType.equals(User.USER_TYPE_JOB_SEEKER) ) return true;
		return false;
	}
	
}
