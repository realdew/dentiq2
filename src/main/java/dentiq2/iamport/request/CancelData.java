package dentiq2.iamport.request;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
public class CancelData {

	@JsonProperty(value="imp_uid")
	@Getter @Setter private String impUid;
	
	@JsonProperty(value="merchant_uid")
	@Getter @Setter private String merchantUid;
	
	@JsonProperty(value="amount")
	@Getter @Setter private BigDecimal amount;
	
	@JsonProperty(value="reason")
	@Getter @Setter private String reason;
	
	@JsonProperty(value="refund_holder")
	@Getter @Setter private String refundHolder;
	
	@JsonProperty(value="refund_bank")
	@Getter @Setter private String refundBank;
	
	@JsonProperty(value="refund_account")
	@Getter @Setter private String refundAccount;
	
	public CancelData(String uid, boolean imp_uid_or_not) {
		if ( imp_uid_or_not ) {
			this.impUid = uid;
		} else {
			this.merchantUid = uid;
		}
	}
	
	public CancelData(String uid, boolean imp_uid_or_not, BigDecimal amount) {
		this(uid, imp_uid_or_not);
		this.amount = amount;
	}

	
}