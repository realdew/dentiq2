package dentiq2.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;


@JsonInclude(Include.NON_NULL)
public class PaymentArgument {
	
	@Getter @Setter String	pg				= "html5_inicis";
	@Getter @Setter String	pay_method		= "card";
	
	@Getter @Setter String	merchant_uid;
	
	@Getter @Setter String	name;
	@Getter @Setter Long	amount;
	
	// 전용
	@Getter @Setter Long	hospitalId;
	
	
	@Getter @Setter String	buyer_email;
	@Getter @Setter String	buyer_name;
//	@Getter @Setter String	buyer_tel		= "01072093356";
//	@Getter @Setter String	buyer_addr		= "서울시 강남구 역삼동 725-40";
//	@Getter @Setter String	buyer_postcode	= "12345";
	
	@Getter @Setter String	m_redirect_url;
	
	
	
	// 응답값
	
	@Getter @Setter String	resJson;
	
	@Getter @Setter Long	paid_amount;
	@Getter @Setter String	error_msg;
	@Getter @Setter String	apply_num;
	@Getter @Setter String	imp_uid;
	@Getter @Setter String	pg_tid;
	
	
	
	
	// 전용
//	@JsonIgnore @Getter @Setter Date	trxStartTs;
//	@Getter @Setter Date	trxEndTs;
	@Getter @Setter String	trxStatus;
	
//	@Getter @Setter Date	cancelStartTs;
//	@Getter @Setter Date	cancelEndTs;
//	@Getter @Setter Date	cancelStatus;
	
	@Getter @Setter String	paymentType;
	@Getter @Setter Long	jobAdId;

}
