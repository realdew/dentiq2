package dentiq2.iamport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import dentiq2.api.util.JsonUtil;
import dentiq2.iamport.request.AuthData;
import dentiq2.iamport.request.CancelData;
import dentiq2.iamport.response.AccessToken;
import dentiq2.iamport.response.IamportResponse;
import dentiq2.iamport.response.Payment;

public class IamportClient {
	
	/*
	public static <E> E toGenericObject(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<E>() {});
	}
	
	
	public static String toJson(Object obj) throws Exception {
	 */
	
	
	private static final String API_URL = "https://api.iamport.kr";
	// private static final String API_URL = "http://localhost:8888";
	private String api_key = null;
	private String api_secret = null;
	private HttpClient client = null;
	//private Gson gson = new Gson();
	
	public IamportClient(String api_key, String api_secret) {
		this.api_key = api_key;
		this.api_secret = api_secret;
		this.client = HttpClientBuilder.create().build();
	}
	
	private IamportResponse<AccessToken> getAuth() throws Exception{
		AuthData authData = new AuthData(api_key, api_secret);
				
		//String authJsonData = gson.toJson(authData);
		String authJsonData = JsonUtil.toJson(authData);
		
		try {
			StringEntity data = new StringEntity(authJsonData);
			
			HttpPost postRequest = new HttpPost(API_URL+"/users/getToken");
			postRequest.setHeader("Accept", "application/json");
			postRequest.setHeader("Connection","keep-alive");
			postRequest.setHeader("Content-Type", "application/json");
			
			postRequest.setEntity(data);
			
			HttpResponse response = client.execute(postRequest);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			
			//IamportResponse<AccessToken> auth0 = JsonUtil.<IamportResponse<AccessToken>>toGenericObject(body);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			IamportResponse<AccessToken> auth0 =  mapper.readValue(body, new TypeReference<IamportResponse<AccessToken>>() {});
			
			System.out.println("JACKSON 변환 : " + auth0);
			
			
			
			
//			Type listType = new TypeToken<IamportResponse<AccessToken>>(){}.getType();
//			IamportResponse<AccessToken> auth = gson.fromJson(body, listType);
			
			return auth0;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	} 

	private String postRequest(String path, String token, StringEntity postData) throws URISyntaxException{
		
		try {
			
			HttpPost postRequest = new HttpPost(API_URL+path);
			postRequest.setHeader("Accept", "application/json");
			postRequest.setHeader("Connection","keep-alive");
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.addHeader("Authorization", token);

			postRequest.setEntity(postData);
					
			HttpResponse response = client.execute(postRequest);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			
			return body;
			
		} catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
				
		return null;
	}
	
	private String getRequest(String path, String token) throws URISyntaxException {
		
		try {

			HttpGet getRequest = new HttpGet(API_URL+path);
			getRequest.addHeader("Accept", "application/json");
			getRequest.addHeader("Authorization", token);

			HttpResponse response = client.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);

			return body;
			
		  } catch (ClientProtocolException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();
		  }
		
		return null;
	}
	
	public String getToken() throws Exception {
		
		IamportResponse<AccessToken> auth = this.getAuth();
		
		if( auth != null) {
			String token = auth.getResponse().getToken();
			return token;
		}
		
		return null;		
	}
	
	public IamportResponse<Payment> paymentByImpUid(String impUid) throws Exception {
		
		String token = this.getToken();
		
		if(token != null) {
			String path = "/payments/"+impUid;
			String response = this.getRequest(path, token);
			
//			Type listType = new TypeToken<IamportResponse<Payment>>(){}.getType();
//			IamportResponse<Payment> payment = gson.fromJson(response, listType);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			IamportResponse<Payment> payment =  mapper.readValue(response, new TypeReference<IamportResponse<Payment>>() {});
			payment.getResponse().setResJson(response);
			
			
			return payment;
		}		
		return null;		
	}	
	
	public IamportResponse<Payment> paymentByMerchantUid(String merchantUid) throws Exception {
		
		String token = this.getToken();
		
		if(token != null) {
			String path = "/payments/find/"+merchantUid;
			String response = this.getRequest(path, token);
			
			System.out.println("수신한 JSON 데이터 : " + response);
			
//			Type listType = new TypeToken<IamportResponse<Payment>>(){}.getType();
//			IamportResponse<Payment> payment = gson.fromJson(response, listType);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			IamportResponse<Payment> payment =  mapper.readValue(response, new TypeReference<IamportResponse<Payment>>() {});
			//payment.setJson(response);
			payment.getResponse().setResJson(response);
			
			return payment;
		}
		
		return null;
	}
	
	public IamportResponse<Payment> cancelPayment(CancelData cancelData) throws Exception {
		
		String token = this.getToken();
		
		if(token != null) {
			//String cancelJsonData = gson.toJson(cancelData);
			String cancelJsonData = JsonUtil.toJson(cancelData);
			StringEntity data = new StringEntity(cancelJsonData);
			
			String response = this.postRequest("/payments/cancel", token, data);
						
//			Type listType = new TypeToken<IamportResponse<Payment>>(){}.getType();
//			IamportResponse<Payment> payment = gson.fromJson(response, listType);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			IamportResponse<Payment> payment =  mapper.readValue(response, new TypeReference<IamportResponse<Payment>>() {});
			payment.getResponse().setResJson(response);
			
			return payment;
		}		
		return null;
	}	
}