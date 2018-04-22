package dentiq2.api.model;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class JobAdSummary {
	
	@Getter private List<LocationSummary>	locationList;
	
	@Getter private Map<String, Long>		adTypeList	= new Hashtable<String, Long>();
	@Getter private Long					totalCnt	= 0L;		// 현재 검색 조건에서 검색된 공고의 총 개수	
	
	
	@Getter @Setter private SummaryCondition condition;
	
//	public void setSidoCode(String sidoCode) {
//		if ( condition == null ) condition = new SummaryCondition();
//		condition.setSidoCode(sidoCode);
//	}
//	public void setSiguCodeList(List<String> siguCodeList) {
//		if ( condition == null ) condition = new SummaryCondition();
//		condition.setSiguCodeList(siguCodeList);
//	}
//	public void setAttr(List<String> attr) {
//		if ( condition == null ) condition = new SummaryCondition();
//		condition.setAttr(attr);
//	}
		
	
	@JsonInclude(Include.NON_NULL)
	class SummaryCondition {
		@Getter @Setter private String sidoCode;
		
		@Getter @Setter private List<String> siguCode;
		
		@Getter @Setter private List<String> attr;
		
		@Getter @Setter private String baseSidoCode;
	}
	
	
	
	
	
	public JobAdSummary(String sidoCode, List<String> siguCodeList, List<String> attrList, List<LocationSummary> locSummaryList) throws Exception {
		this.condition = new SummaryCondition();
		
		
		// 시구코드들이 요청된 경우에는, a:시도(라이브보드에서 시도가 선택된 경우) 또는 b:관심지역, c:우리동네 이므로 sidoCode를 클라이언트로 보내서는 안된다. (sidoCode를 보내게 되면 listJobAd에서 sidoCode가 포함되어 조회되는 문제발생)
		// 대신에 요청된 sidoCode를 baseSidoCode로 바꾸어서 보낸다.
		if ( siguCodeList!=null && siguCodeList.size()>0 ) {
			this.condition.setBaseSidoCode(sidoCode);		
			
		// 시구코드들이 요청되지 않는 경우에는, 전국(시도단위들) 이므로 sidoCode를 그대로 sidoCode로 클라이언트에 보내준다.	
		} else {												
			this.condition.setSidoCode(sidoCode);
		}

		
		this.condition.setSiguCode(siguCodeList);
		this.condition.setAttr(attrList);
		
		
		if ( locSummaryList==null || locSummaryList.size()<1 ) return;
		
		
		
		this.locationList = locSummaryList;
		
		if ( this.condition.siguCode!=null && this.condition.siguCode.size()>0 ) {		// requested 마킹 + requested 개수
			for ( LocationSummary locationSummary : this.locationList ) {
				if ( !this.condition.siguCode.contains(locationSummary.getSiguCode()) ) continue;
				accumulate(locationSummary);
				locationSummary.setRequested(true);
			}
			
		} else {													// 전체 개수 (이 경우, requested된 것이 없으므로, 전체 개수 == requested 개수)
			for ( LocationSummary locationSummary : this.locationList ) {
				accumulate(locationSummary);
			}
		}
	}
	
	private void accumulate(LocationSummary locationSummary) {
		Map<String, Long> map = locationSummary.getCntByJobAdType();			
		Iterator<String> keyIter = map.keySet().iterator();
		while ( keyIter.hasNext() ) {
			String key = keyIter.next();
			Long cnt = map.get(key);
			if ( cnt==null ) continue;
			
			Long accumulatedCnt = this.adTypeList.get(key);
			if ( accumulatedCnt == null ) {
				accumulatedCnt = cnt;
			} else {
				accumulatedCnt += cnt;
			}
			this.adTypeList.put(key, accumulatedCnt);
			this.totalCnt += cnt;
		}
	}
	
	
//	public void setLocationList(List<LocationSummary> locCounterList) {
//		
//		if ( locCounterList==null || locCounterList.size()<1 ) return;
//		
//		this.locationList = locCounterList;		
//		
//		for ( int i=0; i<this.locationList.size(); i++ ) {
//			LocationSummary counter = this.locationList.get(i);
//			Map<String, Long> map = counter.getCntByJobAdType();
//			
//			
//			Iterator<String> keyIter = map.keySet().iterator();
//			while ( keyIter.hasNext() ) {
//				String key = keyIter.next();
//				Long cnt = map.get(key);
//				if ( cnt==null ) continue;
//				
//				Long accumulatedCnt = adTypeList.get(key);
//				if ( accumulatedCnt == null ) {
//					accumulatedCnt = cnt;
//				} else {
//					accumulatedCnt += cnt;
//				}
//				adTypeList.put(key, accumulatedCnt);
//				this.totalCnt += cnt;
//				
//			}
//		}
//		
//		//System.out.println("전체 누적 : " + cntByJobAdTypeMap);
//		
//		
//	}
}



