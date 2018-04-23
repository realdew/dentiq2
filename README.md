
 
dentiq2 API Server
==================

Project dentiq - second revision.



# 서버 기동 설정
1. 서버 기동을 위해서는 서버 설정 properties 파일이 필요함

서버 기동 방법
	<pre><code>
	java -Ddentiq.configFileName=/etc/dentalplus/api_server2.properties -jar ./dentiq2.jar
	</code></pre>


# 주요 변경 사항 (first revision 대비)

* Controller --> Service --> Mapper 구조에서 Controller --> Mapper 구조로 단순화함.
	개발 속도 향상을 위해서
	
* MyBatis Mapper의 단순화
	- 기존에 테이블 단위 및 서비스 단위로 복잡하게 만들어진 여러 Mapper들을 CommonMapper 1개로 통일
	- CommonMapper.java에서 단순한 Query들을 직접 어노테이션으로 직접 처리하고,
	구조적인 객체 생성이 필요한 경우(대부분 select)나 속성(직업/위치) 검색 등이 필요한 부분만 외부 xml로 정리

* 지역코드(LocationCode)를 서버 기동시에 미리 로딩하여, 캐쉬된 내용을 사용함.
	DB에서 Select시 복잡한 join을 사용하지 않도록 함
	
* 좌표 체계 다중화
	- 좌표 기반 서비스를 편리하게 사용하기 위해서 GRS80 좌표계와 WGS84좌표계를 모두 저장함

* 인증 토큰에 서명을 추가함.
	보안성 향상
