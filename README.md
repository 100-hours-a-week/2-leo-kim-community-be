1. Spring Boot 프로젝트 생성
   Homebrew 17.0.14 버전의 JDK로 생성하였습니다.
2. 의존성 추가 및 설정
  <ul>
  <li>서버 구동을 위한 org.springframework.boot:spring-boot-starter-web</li>
  <li>JPA 사용을 위한 org.springframework.boot:spring-boot-starter-data-jpa</li>
  <li>유효성 검사를 위한 org.springframework.boot:spring-boot-starter-validation</li>
  <li>어노테이션 활성화를 위한 org.projectlombok:lombok</li>
  </ul>
 그리고 mysql과 연동을 위해 application.yml에 설정을 추가하였습니다.

3. 파일 구조
   기본적인 Controller, Service, Repository, Entity, Dto를 추가하였고, response의 형식이 statuscode, message, data로 동일하다는 점을 이용해 ApiResponse로 통일하였습니다. 또한 Enum class를 이용하여 각 코드와 상황을 열거하였습니다.

고민사항 : 이미지 저장을 MongoDB를 이용할 것인가, 아니면 그냥 디렉토리에 저장할 것인가.
