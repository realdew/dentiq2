package dentiq2.iamport.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AuthData {
	@JsonProperty(value="imp_key")
	@Getter @Setter private String apiKey;
	
	@JsonProperty(value="imp_secret")
	@Getter @Setter private String apiSecret;
	
	public AuthData(String api_key, String api_secret) {
		this.apiKey = api_key;
		this.apiSecret = api_secret;
	}
}
