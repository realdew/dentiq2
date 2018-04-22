package dentiq2.api.code;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import dentiq2.api.model.Location;
import dentiq2.api.util.JsonUtil;
import lombok.Getter;


public class LocationCodeManager {
	
	private LocationCodeManager() {
		System.out.println("************************* LocationCode 생성됨 *******************************");
	}
	
	public static LocationCodeManager getInstance() {
		return LocationCodeManagerHolder.instance;
	}
	
	public static class LocationCodeManagerHolder {
		private static final LocationCodeManager instance = new LocationCodeManager();
	}
	
	
	@Getter List<Location> locationList;
	
	@Getter Map<String, Location> locationMapByLocationCode	= new Hashtable<String, Location>();
	@Getter Map<String, Location> locationMapBySiguCode		= new Hashtable<String, Location>();
	@Getter Map<String, LocationSido> locationTree			= new Hashtable<String, LocationSido>();
	
	
	@Getter String LOCATION_TREE_JSON;
	
	public String getSidoName(String sidoCode) {
		Location loc = this.locationTree.get(sidoCode);
		if ( loc != null ) return loc.getSidoName();
		return null;
	}
	
	public String getSiguName(String siguCode) {
		Location loc = this.locationMapBySiguCode.get(siguCode);
		if ( loc != null ) return loc.getSiguName();
		return null;
	}
	
	public Location getLocationByLocationCode(String locationCode) {
		return locationMapByLocationCode.get(locationCode);
	}
	
	public Location getLocationBySiguCode(String siguCode) {
		return locationMapBySiguCode.get(siguCode);
	}
	
	public Location getLocationFromTree(String sidoOrLocationCode) {
		return locationTree.get(sidoOrLocationCode);
	}
	
	
	//TODO 외부(Servlet 등)에서 init 및 reload할 때 사용한다.
	public void setLocationList(List<Location> locationList) throws Exception {
		this.locationList = locationList;
		
		locationMapByLocationCode	= new Hashtable<String, Location>();
		locationMapBySiguCode		= new Hashtable<String, Location>();
		locationTree			= new Hashtable<String, LocationSido>();
		
		LOCATION_TREE_JSON = null;
		
		
		
		
		for ( Location location : locationList ) {
			locationMapByLocationCode.put(location.getLocationCode(), location);		// LOCATION_CODE를 KEY로 모두 저장
			if ( location.getLocationType().equals(Location.LOCATION_TYPE_SIDO) ) {
				//locationMapBySidoCode.put(location.getSidoCode(), location);			// SIDO_CODE를 KEY로, 시도인 것들만 저장
				
				LocationSido sido = new LocationSido(location);
				locationTree.put(sido.getLocationCode(), sido);
				
			} else if ( location.getLocationType().equals(Location.LOCATION_TYPE_SIGU) ) {
				locationMapBySiguCode.put(location.getSiguCode(), location);			// SIGU_CODE를 KEY로, 시구인 것들만 저장
			}
		}
		
		
		for ( Location location : locationList ) {
			if ( location.getLocationType().equals(Location.LOCATION_TYPE_SIGU) ) {
				String sidoCode = location.getSidoCode();
				LocationSido sido = locationTree.get(sidoCode);
				sido.addChild(location);
			}
		}
		
		// JSON 데이터 미리 생성
		this.LOCATION_TREE_JSON = JsonUtil.toJson(locationTree);
		
	}
	
	
	
	
	
	public class LocationSido extends Location {
		@Getter Map<String, Location> children;
		
		public LocationSido(Location loc) {
			super();
			this.locationCode	= loc.getLocationCode();
			this.locationType	= loc.getLocationType();
			this.sidoCode		= loc.getSidoCode();
			this.sidoName		= loc.getSidoName();
			this.siguCode		= loc.getSiguCode();
			this.siguName		= loc.getSiguName();
		}
		
		public void addChild(Location sigu) throws Exception {
			
			if ( sigu==null ) return;
			
			if ( this.children == null ) this.children = new Hashtable<String, Location>();
			
			if ( !sigu.getSidoCode().equals(this.sidoCode) ) throw new Exception("시도코드 불일치");
			
			this.children.put(sigu.getLocationCode(), sigu);
			//System.out.println("추가되었음");
		}
	}
	
	

}
