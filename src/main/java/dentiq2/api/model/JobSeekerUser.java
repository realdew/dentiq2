package dentiq2.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import dentiq2.api.code.SystemConstants;
import dentiq2.api.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 구직 회원
 * 
 * @author lee
 *
 */
@JsonInclude(Include.NON_NULL)
public class JobSeekerUser extends User {
	
	@Getter @Setter private Location location;
	
	@Getter @Setter private String name;
	@Getter @Setter private String telNo;
	@Getter @Setter private String birthday;
	@Getter @Setter private String gender;
		
	@Getter private Boolean hasProfileImage;
		@JsonIgnore public void setProfileImageYn(String flag) {
			if ( flag !=null && flag.equals("Y") ) hasProfileImage = true;
			else hasProfileImage = null;
		}
	
	public String getProfileImageUrl() {
		if ( hasProfileImage != null && hasProfileImage == true ) {
			SystemConstants systemConstants = SystemConstants.getInstance();
			return systemConstants.getJOB_SEEKER_RESOURCE_URL_FULL() + "/" + this.userId + "/" + systemConstants.getJOB_SEEKER_RESOURCE_FILE_NAME_PROFILE_SMALL();
		} else {
			return null;
		}
	}
		
		
	
	public Integer getAge() {
		if ( this.birthday == null || this.birthday.trim().length() != 8 ) {
			return null;
		}
		
		Integer age = null;
		try {
			age = DateUtil.calAge(this.birthday);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return age;
	}
	
}
