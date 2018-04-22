package dentiq2.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * (구인) 병원 회원
 * 
 * @author lee
 *
 */
@JsonInclude(Include.NON_NULL)
public class HospitalUser extends User {
	
	@Getter @Setter private Hospital hospital;
	
	//@Getter @Setter private Long hospitalId;
	
	

}
