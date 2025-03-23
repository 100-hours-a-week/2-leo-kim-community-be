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

Update에서 Transactional FetchType, Propagation에 대한 설정에 대한 고민
https://velog.io/@nuh__d/JPA-FetchType.LAZY-%EC%9C%BC%EB%A1%9C-%EC%9D%B8%ED%95%9C-%EB%B0%9C%EC%83%9D%ED%96%88%EB%8D%98-%EB%AC%B8%EC%A0%9C%EC%A0%90

accessToken에서 userId를 가져올 때 Bearer를 빼지 않고 추출하면 Invalid Token을 던지게 된다.

@Transactional을 붙여서 Fetchtype-lazy로 인한 문제를 해결하였다. -> createComment
UserEntity의 getLikedPosts() 메서드는 @OneToMany(fetch = FetchType.LAZY) 설정이 되어 있어 처음에는 프록시 객체로 로드된다.
하지만 toString(), .size() 등의 메서드를 호출하여 실제 데이터를 조회하려고 하면 LazyInitializationException이 발생한다.
이는 해당 엔티티가 트랜잭션 내에서 조회되지 않았거나, 트랜잭션이 종료된 후 데이터를 조회하려고 시도하기 때문이다.
이를 해결하기 위해 @Transactional을 사용하여 트랜잭션(Persistence Context)을 유지하면, EntityManager가 열려 있어 Lazy Loading이 정상적으로 작동한다.

UserEntity와 PostEntity의 상호 @Tostring, log으로 인한 stackoverflow가 생겼다.

Entity에 implements Serializable가 권장된다고 JPA 문서에 명시되어있다.
하지만 최근엔 굳이 사용하지 않아도 된다고한다.

1. DTO, VO의 사용:
   요즘 구현을 할 때 DTO, VO를 사용하지 않고, Entity 자체를 보내는 경우는 거의 없다.
2. 직렬화 대안 기술:
   Spring boot와 MSA 환경에서는 JSON, XML 등의 직렬화 대안 기술을 활용하여 데이터를 주고 받는 것이 일반적이다.
   JSON을 사용하는 경우 Jackson 라이브러리가 자동으로 객체를 JSON으로 변환하고 역직렬화한다. 이 경우 클래스 버전과 환경의 일치성을 걱정할 필요가 없을 수 있다.
3. 클래스 로딩 및 Classpath 관리:
   Spring boot와 MSA 환경은 클래스 로딩과 Classpath 관리를 편리하게 제공한다.
   필요한 클래스들을 각 마이크로서비스의 패키지 구조에 잘 배치하고, 의존성 관리를 해주면, 클래스 버전 및 환경의 불일치 문제를 최소화할 수 있다.
4. 마이크로서비스 아키텍처의 장점:
   MSA 환경에서는 각 서비스가 독립적으로 배포되고 실행되므로, 클래스나 환경의 변경이 각 서비스에 미치는 영향이 제한적일 수 있다.
   이로 인해 클래스나 환경의 변화로 인한 문제가 다른 서비스로 전파되는 것을 최소화할 수 있다.

고민사항 : ~~이미지 저장을 MongoDB를 이용할 것인가,~~ 아니면 그냥 디렉토리에 저장할 것인가.

근데 comment가 달려서 comment만 다시 가져와서 리렌더링하는 형식으로하면, 댓글 달린 사이에 그 post author의 정보가 바뀌면 그건 반영이 안되는데? 결국 새로고침으로 다시 가져와야하나?

또, likeList의 size로 like수를 가져오는게아니라 like flag? 를 entity에 만들어놓고 그걸 반환하자 -> likeList를 SELECT하는 쿼리가 불필요하기때문

토큰이 만료되면 만들어둔 ExceptionHandler를 RestControllerAdvice로 설정하여 exception을 던지지 말고 메시지를 리턴하게끔 해놨으나, jwtFilter에 의해 먼저 걸러져서 ExceptionHandler는 무시되고 예외를 그냥 던지는 문제가 있었다. Exception의 stack trace 도중 jwtFilter가 먼저이기 때문. 이를 해결하는 방법이

1. Filter내부에서 로직처리를 한다.(리턴하게끔 한다.)
2. AuthenticationEntryPoint를 이용한다.
   우선은 Filter내부에서 로직처리를 하고, 프로젝트를 완성하는 방향으로 진행하였다.

TODO : 좋아요 한번 누르는데 쿼리문의 불필요한 호출이 있는거같은데? 다른 API도 마찬가지고? 다 까봐야한다.

TODO : 인증을 받기 위해 getMe 호출을 하는데 getMe는 한번만 호출받고 인증요청만 따로 실행하는게 맞을거같기도하고?

파일 업로드, 변경 구현
변경, 삭제 시에 업로드된 파일은 삭제하여 메모리 관리를 하였습니다.
