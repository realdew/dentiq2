package dentiq2.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
public class Notice {
	
	@Getter @Setter private Long noticeId;
	
	@Getter @Setter private String noticeType;
	
	@Getter @Setter private String title;
	
	@Getter @Setter private String content;
	
	@Getter @Setter private String webUrl;
	
	@Getter @Setter private Date createdTs;
	
	@Getter @Setter private Date lastUpdatedTs;
	
	@Getter @Setter private String useYn;

}
