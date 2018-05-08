package dentiq2.iamport.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class IamportResponse<T> {
	
	@Getter @Setter int code;
	
	@Getter @Setter String message;
	
	@Getter @Setter T response;
	
	//@Getter @Setter String json;

}