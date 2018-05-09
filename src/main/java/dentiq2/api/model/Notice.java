package dentiq2.api.model;

import java.time.LocalDateTime;

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
	
	@Getter @Setter private LocalDateTime createdTs;
	
	@Getter @Setter private LocalDateTime lastUpdatedTs;
	
	@Getter @Setter private String useYn;

}
