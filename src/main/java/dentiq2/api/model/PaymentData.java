package dentiq2.api.model;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class PaymentData extends dentiq2.iamport.response.Payment {
	
	public static final String PAYMENT_FOR_JOB_AD = "J";
	public static final String PAYMENT_FOR_MEMBERSHIP = "M";
	
	public static final String CURRENT_PG = "html5_inicis";
	
	@Getter @Setter String	pg				= CURRENT_PG;
	
	@Getter @Setter Long hospitalId;
	
	@Getter @Setter Long jobAdId;
	@Getter @Setter String paymentFor;
	@Getter @Setter String trxStatus;
	
	@Getter @Setter String startDate;
	@Getter @Setter String endDate;
	@Getter @Setter Integer period;
	
	
}
