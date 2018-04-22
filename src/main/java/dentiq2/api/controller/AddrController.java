package dentiq2.api.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dentiq2.api.model.Location;
import enqual.common.juso.AddrCoordinate;
import enqual.common.juso.AddrJuso;
import enqual.common.juso.AddrJusoResults;
import enqual.common.juso.AddrResultCommon;
import enqual.common.juso.JusoUtil;
import lombok.Getter;
import lombok.Setter;


/**
 * 주소 조회용 API 컨트롤러
 * 
 * www.juso.go.kr의 정보를 사용한다.
 * 따라서 서버와 juso.go.kr과의 통신이 원활하지 못할 경우에, 에러가 발생할 수 있음
 * 
 * @author			leejuhyeon@gmail.com
 * @lastUpdated		2017.12.26
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class AddrController {
	
	private LocationResults convertAddrJusoResultsToLocationList(AddrJusoResults addrJusoResults) throws Exception {
		
		AddrResultCommon jusoCommon = addrJusoResults.getCommon();
		
		Map<String, String> common = new Hashtable<String, String>();
		common.put("countPerPage",	jusoCommon.getCountPerPage());
		common.put("currentPage",	jusoCommon.getCurrentPage());
		common.put("errorCode",		jusoCommon.getErrorCode());
		common.put("errorMessage",	jusoCommon.getErrorMessage());
		common.put("totalCount",	jusoCommon.getTotalCount());
		
		
		AddrJuso[] jusoArray = addrJusoResults.getJuso();
		
		List<Location> locationList = new ArrayList<Location>();		
		if ( jusoArray!=null && jusoArray.length>0 ) {
			for ( int i=0; i<jusoArray.length; i++ ) {
				Location location = new Location(jusoArray[i]);
				locationList.add(location);
			}
		}
				
		
		LocationResults locationResults = new LocationResults();
		locationResults.setCommon(common);
		locationResults.setLocationList(locationList);
		
		return locationResults;
		
	}
	class LocationResults {
		@Getter @Setter Map<String, String> common;
		@Getter @Setter List<Location> locationList;
	}
	
	@RequestMapping(value="/location/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<LocationResults>> searchLocation(
			@RequestParam(value="keyword",		required=true) String keyword,
			@RequestParam(value="currentPage",	required=false) Integer currentPage,
			@RequestParam(value="countPerPage",	required=false) Integer countPerPage
			) {
		
		System.out.println("주소검색 [" + keyword + "] [" + currentPage + "]");
		JsonResponse<LocationResults> res = new JsonResponse<LocationResults>();
		try {
			JusoUtil jusoUtil = new JusoUtil();
			
			if ( countPerPage == null ) countPerPage = 10;
			else if ( countPerPage < 1 ) throw new Exception();
			
			if ( currentPage == null ) currentPage = 1;
			else if ( currentPage < 1 ) throw new Exception();
			
			AddrJusoResults result = jusoUtil.searchAddr(currentPage, countPerPage, null, keyword);
			
			System.out.println("주소검색 결과 " + result);
			
			LocationResults results = convertAddrJusoResultsToLocationList(result);
			
			res.setResponse(results);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<LocationResults>>(res, HttpStatus.OK);	
	}
	
	
	// 주소 조회
//		@RequestMapping(value="/addr/", method=RequestMethod.GET)
//		public ResponseEntity<JsonResponse<AddrJusoResults>> searchAddrJuso(
//				@RequestParam(value="keyword",		required=true) String keyword,
//				@RequestParam(value="currentPage",	required=false) Integer currentPage,
//				@RequestParam(value="countPerPage",	required=false) Integer countPerPage
//				) {
//			
//			System.out.println("주소검색 [" + keyword + "] [" + currentPage + "]");
//			JsonResponse<AddrJusoResults> res = new JsonResponse<AddrJusoResults>();
//			try {
//				JusoUtil jusoUtil = new JusoUtil();
//				
//				if ( countPerPage == null ) countPerPage = 10;
//				else if ( countPerPage < 1 ) throw new Exception();
//				
//				if ( currentPage == null ) currentPage = 1;
//				else if ( currentPage < 1 ) throw new Exception();
//				
//				AddrJusoResults result = jusoUtil.searchAddr(currentPage, countPerPage, null, keyword);
//				
//				System.out.println("주소검색 결과 " + result);
//				res.setResponse(result);
//			} catch(Exception ex) {
//				res.setException(ex);
//			}
//			
//			return new ResponseEntity<JsonResponse<AddrJusoResults>>(res, HttpStatus.OK);	
//		}
	
	// 좌표조회 : juso.go.kr의 정보로 좌표값을 찾는다.
	@RequestMapping(value="/coordinate/", method=RequestMethod.GET)
	public ResponseEntity<JsonResponse<AddrCoordinate[]>> searchAddrCoordinate(
			@RequestParam(value="admCd",		required=true) String admCd,
			@RequestParam(value="rnMgtSn",		required=true) String rnMgtSn,
			@RequestParam(value="udrtYn",		required=true) String udrtYn,
			@RequestParam(value="buldMnnm",		required=true) String buldMnnm,
			@RequestParam(value="buldSlno",		required=true) String buldSlno
			) {
		
		JsonResponse<AddrCoordinate[]> res = new JsonResponse<AddrCoordinate[]>();
		try {
			JusoUtil jusoUtil = new JusoUtil();
			AddrCoordinate[] coordinates = jusoUtil.searchCoordinate(admCd, rnMgtSn, udrtYn, buldMnnm, buldSlno);
			res.setResponse(coordinates);
		} catch(Exception ex) {
			res.setException(ex);
		}
		
		return new ResponseEntity<JsonResponse<AddrCoordinate[]>>(res, HttpStatus.OK);
		
	}
}
