package dentiq2.api.model;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class LocationSummary extends Location {
	@Getter @Setter private Long cnt = (long) 0;
	
	@Getter Map<String, Long> cntByJobAdType = new TreeMap<String, Long>();
	
	@Getter private String adTypeList;
	@Getter private String cntList;
	
	@Getter @Setter private boolean requested = false;
	
	
	
	public LocationSummary() {}
	
	public void setAdTypeList(String adTypeList) throws Exception {
		this.adTypeList = adTypeList;
		setup();
	}
	
	public void setCntList(String cntList) throws Exception {
		this.cntList = cntList;
		setup();
	}
	
	private void setup() throws Exception {
		//System.out.println("SETUP : [" + adTypeList + "] [" + cntList + "]");
		if ( adTypeList==null || cntList==null ) return;
		
		String[] adTypeToken = adTypeList.split(",");
		String[] cntToken	 = cntList.split(",");
		if ( adTypeToken.length!=cntToken.length ) throw new Exception();		
		
		for ( int i=0; i<adTypeToken.length; i++ ) {
			cntByJobAdType.put(adTypeToken[i], Long.parseLong(cntToken[i]));
			cnt += Long.parseLong(cntToken[i]);
		}
	}
	
	
	
	
	
	
}
