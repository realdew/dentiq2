package dentiq2.api.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtil {
	public static <E> E toGenericObject(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<E>() {});
	}
	
	
	public static String toJson(Object obj) throws Exception {
		String retVal = null;
		
		ByteArrayOutputStream output = null;
		Writer writer = null;
		
		
		try {
			output = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(output, "utf-8");
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(writer, obj);
			
			retVal = output.toString("utf-8");
			
			writer.close();
			output.close();
		} catch(Exception ex) {
			if ( writer != null ) try { writer.close(); } catch(Exception ignore) {}
			if ( output != null ) try { output.close(); } catch(Exception ignore) {}
			throw ex;
		}
		
		return retVal;
	}
}
