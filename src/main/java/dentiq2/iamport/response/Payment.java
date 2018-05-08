package dentiq2.iamport.response;


import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
public class Payment {

	@JsonProperty(value="imp_uid")
	@Getter @Setter public String impUid;
	
	@JsonProperty(value="merchant_uid")
	@Getter @Setter public String merchantUid;
	
	@JsonProperty(value="pay_method")
	@Getter @Setter public String payMethod;
	
	@JsonProperty(value="pg_provider")
	@Getter @Setter public String pgProvider;
	
	@JsonProperty(value="pg_tid")
	@Getter @Setter public String pgTid;
	
//	@JsonProperty(value="escrow")
//	@Getter @Setter public boolean escrow;
	
	@JsonProperty(value="apply_num")
	@Getter @Setter public String applyNum;
	
	@JsonProperty(value="card_name")
	@Getter @Setter public String cardName;
	
	@JsonProperty(value="card_quota")
	@Getter @Setter public int cardQuota;
	
//	@JsonProperty(value="vbank_name")
//	@Getter @Setter public String vbankName;
//	
//	@JsonProperty(value="vbank_num")
//	@Getter @Setter public String vbankNum;
//	
//	@JsonProperty(value="vbank_holder")
//	@Getter @Setter public String vbankHolder;
//	
//	@JsonProperty(value="vbank_date")
//	@Getter @Setter public long vbankDate;
	
	@JsonProperty(value="name")
	@Getter @Setter public String name;
	
	@JsonProperty(value="amount")
	@Getter @Setter public BigDecimal amount;
	
	@JsonProperty(value="cancel_amount")
	@Getter @Setter public BigDecimal cancelAmount;
	
	@JsonProperty(value="buyer_name")
	@Getter @Setter public String buyerName;
	
	@JsonProperty(value="buyer_email")
	@Getter @Setter public String buyerEmail;
	
	@JsonProperty(value="buyer_tel")
	@Getter @Setter public String buyerTel;
	
//	@JsonProperty(value="buyer_addr")
//	@Getter @Setter public String buyerAddr;
//	
//	@JsonProperty(value="buyer_postcode")
//	@Getter @Setter public String buyerPostcode;
	
	@JsonProperty(value="custom_data")
	@Getter @Setter public String customData;
	
	@JsonProperty(value="status")
	@Getter @Setter public String status;
	
	@JsonProperty(value="paid_at")
	@Getter @Setter public long paidAt;
	
	@JsonProperty(value="failed_at")
	@Getter @Setter public long failedAt;
	
	@JsonProperty(value="cancelled_at")
	@Getter @Setter public long cancelledAt;
	
	@JsonProperty(value="fail_reason")
	@Getter @Setter public String failReason;
	
	@JsonProperty(value="cancel_reason")
	@Getter @Setter public String cancelReason;
	
	@JsonProperty(value="receipt_url")
	@Getter @Setter public String receiptUrl;
	
	@Getter @Setter public String resJson;
	
	
	
//	
//	public String getImpUid() {
//		//return imp_uid;
//		return impUid;
//	}
//	
//	public String getMerchantUid() {
//		return merchant_uid;
//	}
//
//	public String getPayMethod() {
//		return pay_method;
//	}
//
//	public String getPgProvider() {
//		return pg_provider;
//	}
//
//	public String getPgTid() {
//		return pg_tid;
//	}
//
//	public boolean isEscrow() {
//		return escrow;
//	}
//
//	public String getApplyNum() {
//		return apply_num;
//	}
//
//	public String getCardName() {
//		return card_name;
//	}
//
//	public int getCardQuota() {
//		return card_quota;
//	}
//
//	public String getVbankName() {
//		return vbank_name;
//	}
//
//	public String getVbankNum() {
//		return vbank_num;
//	}
//
//	public String getVbankHolder() {
//		return vbank_holder;
//	}
//
//	public Date getVbankDate() {
//		return new Date( vbank_date * 1000L );
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public BigDecimal getAmount() {
//		return amount;
//	}
//
//	public BigDecimal getCancelAmount() {
//		return cancel_amount;
//	}
//
//	public String getBuyerName() {
//		return buyer_name;
//	}
//
//	public String getBuyerEmail() {
//		return buyer_email;
//	}
//
//	public String getBuyerTel() {
//		return buyer_tel;
//	}
//
//	public String getBuyerAddr() {
//		return buyer_addr;
//	}
//
//	public String getBuyerPostcode() {
//		return buyer_postcode;
//	}
//
//	public String getCustomData() {
//		return custom_data;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public Date getPaidAt() {
//		return new Date( paid_at * 1000L );
//	}
//
//	public Date getFailedAt() {
//		return new Date( failed_at * 1000L );
//	}
//
//	public Date getCancelledAt() {
//		return new Date( cancelled_at * 1000L );
//	}
//
//	public String getFailReason() {
//		return fail_reason;
//	}
//
//	public String getCancelReason() {
//		return cancel_reason;
//	}
//
//	public String getReceiptUrl() {
//		return receipt_url;
//	}
}