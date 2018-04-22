package enqual.common.juso;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

/**
 * 좌표계 변환 유틸리티
 * 
 * 현재는 UTM-K 좌표(GRS80 기반)를 경위도 좌표(WGS84)로 변환하는 기능만 제공한다.
 * 
 * 내부에서 proj4의 java 버전인 proj4j를 사용한다. 이를 위하여 maven 설정에 의존성을 추가하여야 한다.
 * 
 * proj4j의 좌표변환 기능 사용 방법은 다음을 참고함.
 * 		https://github.com/jdeolive/proj4j/blob/master/src/test/java/org/osgeo/proj4j/ExampleTest.java
 * 
 * proj4에서 좌표계 변환 방법은 다음을 참고함.
 * 		https://mrchypark.wordpress.com/2014/10/23/좌표계-변환-proj4-라이브러리/
 * 		http://windingroad.tistory.com/27
 * 
 * @author		jhlee
 * @startedAt	2018.03.30
 * @lastUpdated	2018.03.30 by jhlee
 */
public class CoordUtil {
	
	//private static CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	//private static CRSFactory csFactory = new CRSFactory();
	
	
	private static final String GRS80_PARAM = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";	// UTM-K 보정
	private static final String WGS84_PARAM = "+proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees +no_defs";
	
	public String[] transGRS80toWGS84(String srcX, String srcY) throws Exception {
		double x;
		double y;
		try {
			x = Double.parseDouble(srcX);
			y = Double.parseDouble(srcY);
		} catch(Exception ex) {
			throw new Exception("소스 좌표가 올바르지 않습니다. x:" + srcX + ", y:" + srcY);
		}
		
		double[] retVal = transGRS80toWGS84(x, y);
		
		return new String[] {retVal[0]+"", retVal[1]+""};
		
	}
	public double[] transGRS80toWGS84(double srcX, double srcY) throws Exception {
		CRSFactory coordinateReferenceSystemFactory = new CRSFactory();
		CoordinateReferenceSystem GRS80		= coordinateReferenceSystemFactory.createFromParameters("GRS80", GRS80_PARAM);
		CoordinateReferenceSystem WGS84		= coordinateReferenceSystemFactory.createFromParameters("WGS84", WGS84_PARAM);
		
		CoordinateTransformFactory coordinateTransformFactory	= new CoordinateTransformFactory();
		CoordinateTransform coordinateTransform					= coordinateTransformFactory.createTransform(GRS80, WGS84);
		
		ProjCoordinate srcCoord = new ProjCoordinate();
		srcCoord.setValue(srcX, srcY);
		ProjCoordinate destCoord = new ProjCoordinate();
		coordinateTransform.transform(srcCoord, destCoord);
		
		double[] retVal = new double[] {destCoord.x, destCoord.y};
		
		return retVal;
	}

	
	public static void main(String[] args) throws Exception {
		
		double[] srcCoord = new double[] {(double) 959392.6806814834, (double) 1944538.8317065234};
		System.out.println("SRC : " + srcCoord[0] + "  " + srcCoord[1]);
		
		CoordUtil coordUtil = new CoordUtil();
		double[] dstCoord = coordUtil.transGRS80toWGS84(srcCoord[0], srcCoord[1]);
		System.out.println("DST : " + dstCoord[0] + "  " + dstCoord[1]);
		
		/*
		CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	  	CRSFactory csFactory = new CRSFactory();
	  	
	  	//CoordinateReferenceSystem crs = csFactory.createFromName("GRS80");
	  	CoordinateReferenceSystem crs = csFactory.createFromParameters("GRS80", "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs");
	  	
	  	
	  	final String WGS84_PARAM = "+proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees +no_defs";
	    CoordinateReferenceSystem WGS84 = csFactory.createFromParameters("WGS84",WGS84_PARAM);
	    
	    CoordinateTransform trans = ctFactory.createTransform(crs, WGS84);
	    
	    ProjCoordinate p = new ProjCoordinate();
	 // entX=959392.6806814834, entY=1944538.8317065234, bdNm=해오름주택
	    p.x = (double) 959392.6806814834;	// double
	    p.y = (double) 1944538.8317065234;
	    
	    ProjCoordinate p2 = new ProjCoordinate();
	    
	    trans.transform(p, p2);
	    
	    System.out.println("FROM : " + p.x + " " + p.y);
	    System.out.println("TO   : " + p2.x + " " + p2.y);
	    */
	    
	}
}

/*
[[ 참고 ]]


** ( https://mrchypark.wordpress.com/2014/10/23/좌표계-변환-proj4-라이브러리/ )

	tm-k좌표계를 위경도 좌표계로 변경
		cs2cs +proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs +to +proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs 파일명 > 저장하고자 하는 파일명
	
	위경도 좌표계 ==> tm-k좌표계 로 변경
		cs2cs +proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs +to +proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs 파일명 > 저장하고자 하는 파일명




** proj4js 활용한 경우  ( http://windingroad.tistory.com/27 )

	var firstProjection = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs"; // from
    var secondProjection = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"; // to
   
    // #1. 변환한 위도 경도 값 저장
    var lonAndLat1 = proj4(firstProjection, secondProjection, point1);// from 경위도
    var lonAndLat2 = proj4(firstProjection, secondProjection, point2); // to 경위도


*/

//
//[[ 참고 코드 ]]  ( https://github.com/jdeolive/proj4j/blob/master/src/test/java/org/osgeo/proj4j/ExampleTest.java )
//
//	private boolean checkTransform(String csName, double lon, double lat, double expectedX, double expectedY, double tolerance)
//	  {
//	  	CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
//	  	CRSFactory csFactory = new CRSFactory();
//	  	/*
//	  	 * Create {@link CoordinateReferenceSystem} & CoordinateTransformation.
//	  	 * Normally this would be carried out once and reused for all transformations
//	  	 */ 
//	    CoordinateReferenceSystem crs = csFactory.createFromName(csName);
//	    
//	    final String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
//	    CoordinateReferenceSystem WGS84 = csFactory.createFromParameters("WGS84",WGS84_PARAM);
//
//	    CoordinateTransform trans = ctFactory.createTransform(WGS84, crs);
//	    
//	    /*
//	     * Create input and output points.
//	     * These can be constructed once per thread and reused.
//	     */ 
//	    ProjCoordinate p = new ProjCoordinate();
//	    ProjCoordinate p2 = new ProjCoordinate();
//	    p.x = lon;
//	    p.y = lat;
//	    
//	    /*
//	     * Transform point
//	     */
//	    trans.transform(p, p2);
//	    
//	   
//	    return isInTolerance(p2, expectedX, expectedY, tolerance);
//	  }
//	
//	boolean isInTolerance(ProjCoordinate p, double x, double y, double tolerance)
//	  {
//	    /*
//	     * Compare result to expected, for test purposes
//	     */ 
//	    double dx = Math.abs(p.x - x);
//	    double dy = Math.abs(p.y - y);
//	    boolean isInTol =  dx <= tolerance && dy <= tolerance;
//	    return isInTol;
//	  }
