package dentiq2.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@JsonInclude(Include.NON_NULL)
public class PaymentArgument {
	
	@Getter @Setter String	pg				= "html5_inicis";
	
	@JsonProperty("pay_method")
	@Getter @Setter String	payMethod		= "card";
	
	@JsonProperty("merchant_uid")
	@Getter @Setter String	merchantUid;
	
	@Getter @Setter String	name;
	@Getter @Setter Long	amount;
	
	// 전용
	@Getter @Setter Long	hospitalId;
	
	@JsonProperty("buyer_email")
	@Getter @Setter String	buyerEmail;
	
	@JsonProperty("buyer_name")
	@Getter @Setter String	buyerName;
//	@Getter @Setter String	buyer_tel		= "01072093356";
//	@Getter @Setter String	buyer_addr		= "서울시 강남구 역삼동 725-40";
//	@Getter @Setter String	buyer_postcode	= "12345";
	
	@JsonProperty("m_redirect_url")
	@Getter @Setter String	mRedirectUrl;
	
	
	
	// 응답값
	
	@Getter @Setter String	resJson;
	
	@JsonProperty("paid_amount")
	@Getter @Setter Long	paidAmount;
	
	@JsonProperty("error_msg")
	@Getter @Setter String	errorMsg;
	
	@JsonProperty("apply_num")
	@Getter @Setter String	applyNum;
	
	@JsonProperty("imp_uid")
	@Getter @Setter String	impUid;
	
	@JsonProperty("pg_tid")
	@Getter @Setter String	pgTid;
	
	
	
	
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
