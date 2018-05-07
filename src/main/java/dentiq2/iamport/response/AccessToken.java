package dentiq2.iamport.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AccessToken {
	@JsonProperty(value="access_token")
	@Getter @Setter String token;
	
	@JsonProperty(value="expired_at")
	@Getter @Setter int expiredAt;
	
	@JsonProperty(value="now")
	@Getter @Setter int now;

}