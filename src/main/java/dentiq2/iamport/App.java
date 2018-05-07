package dentiq2.iamport;

import dentiq2.iamport.response.IamportResponse;
import dentiq2.iamport.response.Payment;

public class App 
{
	IamportClient client;
	
    public static void main( String[] args ) throws Exception {
    	App app = new App();
    	app.test();
    }
    
    public void test() throws Exception {
    	
    	String api_key = "2001016147233984";
    	String api_secret = "ArbVoMpEOqMUCo14ajqwffVa2QWzIW4joffN1ErQ5dKIBMML5dilsevv4r5No96MplzrZ0i5e2NQPACU";
    	client = new IamportClient(api_key, api_secret);
    	
    	String merchantUid = "1525184075033-1-WEB01-API_001";    	
    	
    	
    	IamportResponse<Payment> paymentByMerchantUid = client.paymentByMerchantUid(merchantUid);
    	
    	System.out.println(paymentByMerchantUid.getCode());
    	System.out.println(paymentByMerchantUid.getMessage());
    	System.out.println(paymentByMerchantUid.getResponse());
    }
}