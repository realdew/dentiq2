package dentiq2.api.util;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TestScrapping {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	
	public static void test() throws Exception {
		
		System.out.println("시작");

		BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
//            HttpGet httpget = new HttpGet("http://www.onnara.go.kr");
//            CloseableHttpResponse response1 = httpclient.execute(httpget);
//            try {
//                HttpEntity entity = response1.getEntity();
//
//                System.out.println("STEP 1 STATUS : " + response1.getStatusLine());
//                //EntityUtils.consume(entity);
//                System.out.println("==> " + entity);
//                String result1 = entity != null ? EntityUtils.toString(entity) : null;
//                //System.out.println("==> " + result1);
//                
//                
////                System.out.println("Initial set of cookies:");
////                List<Cookie> cookies = cookieStore.getCookies();
////                if (cookies.isEmpty()) {
////                    System.out.println("None");
////                } else {
////                    for (int i = 0; i < cookies.size(); i++) {
////                        System.out.println("- " + cookies.get(i).toString());
////                    }
////                }
//            } finally {
//                response1.close();
//            }

            System.out.println("\n\n\n");
            
            String value = "[{\"url\":\"http://localhost:9090/OnnaraServiceBE/lotdetailinfo/selecthousePcList.do\",\"adm_sect_cd\":\"11680\",\"land_loc_cd\":\"10100\",\"ledg_gbn\":\"1\",\"bobn\":\"0725\",\"bubn\":\"0040\",\"authKey\":\"authNumber1234\"}]";
            //value = URLEncoder.encode(value, "UTF-8");
            System.out.println("PARAM ==> " + value);
            
            HttpUriRequest req2 = RequestBuilder.post()
                    .setUri(new URI("http://www.onnara.go.kr/proxy/proxy.jsp?"))
                    
//                    .setHeader("Pragma", "no-cache")
//                    .setHeader("Origin", "http://www.onnara.go.kr")
//                    .setHeader("Accept-Encoding", "gzip, deflate")
//                    .setHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,pt;q=0.6")
//                    .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
//                    .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                    .setHeader("Accept", "*/*")
//                    .setHeader("Cache-Control", "no-cache")
//                    .setHeader("X-Requested-With", "XMLHttpRequest")
//                    .setHeader("Cookie", "JSESSIONID=mAqwDdTXuRO1RAa8JEGiaD6vQUHwvjo16t93CKBc3hvbQa08MLn3hBDZ1XN19MRW.ONRPWSPM1_servlet_OnnaraServiceRT")
//                    .setHeader("Connection", "keep-alive")
//                    .setHeader("Referer", "http://www.onnara.go.kr/")
                    
                    .setHeader("Host", "www.onnara.go.kr")
                    //.addParameter("paramList", "%5B%7B%22url%22%3A%22http%3A%2F%2Flocalhost%3A9090%2FOnnaraServiceBE%2Flotdetailinfo%2FselecthousePcList.do%22%2C%22adm_sect_cd%22%3A%2211680%22%2C%22land_loc_cd%22%3A%2210100%22%2C%22ledg_gbn%22%3A%221%22%2C%22bobn%22%3A%220725%22%2C%22bubn%22%3A%220040%22%2C%22authKey%22%3A%22authNumber1234%22%7D%5D")
                    
                    .addParameter("paramList", value)
                    
                    .build();
            
            System.out.println("HEADERS \n" + req2.getRequestLine() + "\n\n");
            Header[] headers = req2.getAllHeaders();
            for ( Header header : headers ) {
            	System.out.println("\t" + header);
            }
            
            
            CloseableHttpResponse response2 = httpclient.execute(req2);
            try {
                HttpEntity entity = response2.getEntity();

                System.out.println("STEP 2 STATUS : " + response2.getStatusLine());
                //EntityUtils.consume(entity);
                System.out.println("==> " + entity);
                String result2= entity != null ? EntityUtils.toString(entity) : null;
                System.out.println("==> " + result2);

                System.out.println("STEP 2 cookies:");
//                List<Cookie> cookies = cookieStore.getCookies();
//                if (cookies.isEmpty()) {
//                    System.out.println("None");
//                } else {
//                    for (int i = 0; i < cookies.size(); i++) {
//                        System.out.println("- " + cookies.get(i).toString());
//                    }
//                }
            } finally {
                response2.close();
            }
        } finally {
            httpclient.close();
        }
		
        System.out.println("종료");
	}
}
