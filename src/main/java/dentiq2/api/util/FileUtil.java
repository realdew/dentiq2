package dentiq2.api.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
	
		
	public static void saveFile(String dir, String fileName, byte[] contents) throws Exception {
		if ( dir==null || dir.trim().equals("") )	throw new Exception("디렉토리가 올바르지 않습니다. [" + dir + "]");
		dir = dir.trim();
		
		//if ( !dir.startsWith("/") )	throw new Exception("디렉토리는 절대경로여야 합니다. ('/'으로 시작하지 않습니다)  [" + dir + "]");
		
		if ( !dir.endsWith("/") ) dir += "/";
		Path path = Paths.get(dir + fileName);
        Files.write(path, contents);
	}
	
	/**
	 * 디렉토리를 생성한다.
	 * 
	 * @param	dirStr
	 * @return	어찌되었건 디렉토리가 생성되었다면(이미 존재하는 경우도 포함) true, 결과적으로 해당 디렉토리가 존재하지 않게 되었다면 false
	 * @throws	Exception 디렉토리 생성 중 에러
	 */
	public static boolean makeDir(String dirStr) throws Exception {
		File dir = new File(dirStr);
		
		if ( dir.exists() ) return true;
		
		dir.mkdir();
		
		return dir.exists();
	}
	
	public static void makeDir(String parentDirStr, String childDirStr) throws Exception {
		if ( parentDirStr==null || parentDirStr.trim().equals("") )	throw new Exception("부모 경로가 올바르지 않습니다. [" + parentDirStr + "]");
		if ( childDirStr==null || childDirStr.trim().equals("") )	throw new Exception("자식 경로가 올바르지 않습니다. [" + childDirStr + "]");
		
		parentDirStr = parentDirStr.trim();
		childDirStr = childDirStr.trim();
		
		//if ( !parentDirStr.startsWith("/") )	throw new Exception("부모 경로명은 절대경로여야 합니다. ('/'으로 시작하지 않습니다)  [" + parentDirStr + "]");
		if ( parentDirStr.endsWith("/") )		parentDirStr.substring(0, parentDirStr.length() -1 );
		
		if ( childDirStr.startsWith("/") )		childDirStr.substring(1, childDirStr.length());
		if ( childDirStr.endsWith("/") )		childDirStr.substring(0, childDirStr.length() -1 );
		
				
		File dir = new File(parentDirStr + "/" + childDirStr);
		
		if ( dir.exists() ) return;
		
		dir.mkdir();
		
		if ( !dir.exists() ) throw new Exception("디렉토리 생성에 실패했습니다. [" + parentDirStr + "/" + childDirStr + "]");
	}

}
