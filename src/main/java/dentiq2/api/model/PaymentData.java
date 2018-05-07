package dentiq2.api.model;



import lombok.Getter;
import lombok.Setter;

public class PaymentData extends dentiq2.iamport.response.Payment {
	
	@Getter @Setter String	pg				= "html5_inicis";
	
	@Getter @Setter Long hospitalId;
	
	@Getter @Setter Long jobAdId;
	@Getter @Setter String paymentFor;
	@Getter @Setter String trxStatus;
	
	
}
