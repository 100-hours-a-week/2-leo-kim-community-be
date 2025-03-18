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

4. JJWT 라이브러리를 이용한 Jwt 구현
   JWT를 구현하는데 널리 사용되는게 Apache의 JJWT, MIT의 Java Jwt라고 합니다. 그 중 더 많은 기능을 제공하고, github의 star, fork 수가 더 많은 JJWT 라이브러리르 택하여 Jwt 를 구현하였습니다.
   JwtFilter를 Spring Security에 달아두어 자동으로 accessToken, refreshToken에 대한 검증을 하도록 했습니다.

    추가사항 : refreshToken을 저장하는 Redis의 사용에 대한 고민, refreshToken으로 accesstoken재발급에 대한 고민

Update에서 Transactional FetchTyper, Propagation에 대한 설정에 대한 고민
https://velog.io/@nuh__d/JPA-FetchType.LAZY-%EC%9C%BC%EB%A1%9C-%EC%9D%B8%ED%95%9C-%EB%B0%9C%EC%83%9D%ED%96%88%EB%8D%98-%EB%AC%B8%EC%A0%9C%EC%A0%90

accessToken에서 userId를 가져올 때 Bearer를 빼지 않고 추출하면 Invalid Token을 던지게 된다.

고민사항 : ~~이미지 저장을 MongoDB를 이용할 것인가,~~ 아니면 그냥 디렉토리에 저장할 것인가.
