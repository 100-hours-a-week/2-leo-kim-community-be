# 📝 Spring Boot 커뮤니티 프로젝트

## 1. 프로젝트 개요

Spring Boot를 기반으로 구현한 커뮤니티 프로젝트입니다. 회원가입, 로그인, 게시글 작성 및 수정, 댓글, 좋아요, 프로필 이미지 업로드 등의 기능을 제공합니다.

## 2. 개발 환경

-   Java: Homebrew 17.0.14
-   Spring Boot
-   JPA (Hibernate)
-   MySQL
-   Spring Security + JWT
-   HTML/CSS/JavaScript (Vanilla)
-   파일 업로드: 디스크(디렉토리) 저장 방식
-   Token 기반 인증 (AccessToken + RefreshToken)
-   추후 Redis 도입 고려 (RefreshToken 관리용)

## 3. 사용 라이브러리 및 의존성

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.projectlombok:lombok'
```

-   `application.yml`에서 MySQL 연결 설정 추가

---

## 📁 프로젝트 구조

-   `controller`: API 요청 처리
-   `service`: 비즈니스 로직
-   `repository`: 데이터 접근
-   `dto`: 요청/응답용 객체
-   `entity`: DB 테이블 매핑
-   `config`: 보안 및 정적 리소스 설정
-   `common`, `global`: 공통 응답, 예외 처리 등
-   `filter`: JWT Filter 등 필터
-   `util`: JWT 생성, 검증 등 도구

---

## 📌 공통 응답 형식 (ApiResponse)

모든 응답은 아래 형식을 따릅니다:

```json
{
	"statusCode": 200,
	"message": "SUCCESS",
	"data": {}
}
```

`Enum` 클래스를 통해 상황별 메시지와 코드를 관리합니다.

---

## 🔐 인증 및 보안

-   Apache JJWT 라이브러리로 JWT 구현
-   `JwtFilter`를 Spring Security 필터 체인에 드래그
-   AccessToken/RefreshToken 인증
-   추후 Redis를 통한 RefreshToken 관리 가능성 고려

### ⚠️ 예외 처리

-   Filter 단계에서 예외가 발생하면 ExceptionHandler까지 도달하지 않아 Filter 내부에서 로직처리
-   Spring Security의 `AuthenticationEntryPoint`로 대체 가능

---

## 🖼️ 파일 업로드 및 이미지 관리

-   회원 프리플, 게시글 이미지 업로드 기능
-   로컬 디렉토리에 저장 (`/upload/profiles`, `/upload/post`)
-   이미지 변경/삭제 시 기존 파일 삭제 처리 포함

---

## 🚀 고민 태겟 & 테크니티 내용

-   게시글 좋아요 수 등은 `likeList.size()` 대신 `likeCount` 필드를 관리해 쿼리 최적화
-   게시글, 프로필 이미지 변경 시 이전 파일 삭제 처리
-   @Transactional로 LAZY 로딩 문제 해결
-   Entity 간 양방향 참조 시 StackOverflow 방지를 위해 toString 제외 처리

---

## 📈 현재 고민

-   `getMe`는 JWT 확인을 위해 모두의 페이지에서 한 번만 호출하고, 인증만 간단히 호출하는 것이 더 효율적일 것 같다는 생각
-   댓글만 리렌더링할 경우, 그 사이에 작성자의 정보가 바뀌면 반영이 안 되는 문제 (해결 필요)
-   예외 전파를 위한 ExceptionHandler 동작 시점 및 필터 순서 이슈
-   좋아요 등 간단한 상태 변경 시 불필요한 쿼리 호출 최적화 필요
-   AOP(HandlerMethodArgumentResolver)를 활용하여 중복된 횡단 관심사 처리(UserEntity 조회 후 활용 등)
-   CommonFunction클래스를 이용하여 결합도를 낮추려고 노력. -> return해야하는 ApiResponse는 서비스에서 처리, throw해야하는건 common에서 처리
-   ***

✅ TODO

-   좋아요 요청 시, 불필요한 쿼리 호출 여부 확인 및 최적화 필요  
    (예: `likeList` 전체를 조회하지 않고 별도의 `flag` 사용 등)
-   다른 API에도 불필요한 쿼리 호출이 없는지 점검 필요
-   인증을 위해 `getMe` 호출 시, 실제 인증과 데이터 조회를 분리할지에 대한 고민  
    (예: `getMe`는 한 번만 호출하고, 인증 여부만 확인하는 별도 API 고려)
-   댓글 등록 후 리렌더링 방식 개선  
    (현재는 새로고침 후 반영. 댓글만 다시 fetch하여 부분 렌더링하는 방식 고려)
-   전체 API 흐름 점검 및 불필요한 연산/쿼리 최소화
-   ~~`@Transactional`을 필요한 메서드에만 설정하여 최적화~~
-   사진 업데이트가 아니라 단순히 내리는 것에 대한 로직처리
-   JWT accesToken refreshToken 로직 수정

## 12. 회고

프로젝트를 위해 저번주와 이번주 내내 개발을 했던 것 같다. 매일 판교에 9 to 10으로 개발을 하다 보니 개발에 대한 감을 잡는데 도움이 됐던 것 같다. 하지만 개발 속도가 많이 느리다는 것을 느꼈다. API, ERD 설계를 하고 들어갔음에도 실 개발 중 디테일을 챙기다 보니 설계를 자주 바꾸게 되기도 하고, 프론트엔드 코드를 구조화를 안해놓고 복사붙여넣기로 짜 놨던게 이번에 좀 터진 것 같다. 다시 리팩토링하여 BE 연결하는 데 굉장히 오랜시간이 걸렸었다. 다음 프로젝트에는 프로젝트를 제대로 구조화하고, 프로젝트 설계에 시간을 아끼지 말고 써야겠다는 생각을 했다.

P.S.
추가적인 branch로 AOP를 적용해보려 했다. <b>그 이유는 서비스 단의 로직이 점점 비대해졌고, 일부 메서드의 시작 로직이 중복되어 보였기 때문이다.</b> 예를 들어, accessToken으로부터 userId를 추출하는 로직이 반복되어 이를 AOP의 @Before 등으로 분리할 수 있지 않을까 고민했다.

그러나 Spring에서 컨트롤러의 파라미터에 직접 UserEntity나 PostEntity를 주입하기 위해 사용하는 방식은,
AOP가 아닌 Spring MVC의 ArgumentResolver 기반 기능이라는 것을 알게 되었고, 이와 같은 상황엔 AOP보다는 HandlerMethodArgumentResolver가 적합하다는 것을 깨달았다.

이후 공통적으로 사용되는 기능들 — 예: 비밀번호 인코딩, accessToken 파싱, 이미지 경로 처리 등 — 을
공통 유틸성 컴포넌트로 분리한 CommonFunctions 클래스를 만들었고, 그 결과 기존에 서비스 단에서 주입받던 JwtUtil, BCryptPasswordEncoder 등의 의존성을 하나로 묶을 수 있어 코드도 간결해졌다.

이렇게 구현하는 것이 더 나은 구조인지, 아니면 기존처럼 직접 각각의 의존성을 주입받는 것이 맞는 방법인지에 대한 확신은 아직 없다. (아직 User, Post 관련 코드에만 적용됨)
<b>케빈의 조언이 필요합니다</b>

추가 회고 :
Test code를 Given Stubbing When Then의 구조로 짜보았다.
테스트 코드를 통해 코드가 수정됐을 때 본래는 PostMan등으로 일일이 상황에 대한 테스트를 해봐야했던 것을 테스트 자동화를 할 수 있어서 좋은 것같다.
또한, 분기 처리를 위해 ParamiterizedTest와 CsvSource를 활용하였다.
로그인*실패, 로그인*성공 등을 나눌 수도 있지만, 한꺼번에 묶는게 가독성 측면에서 더 좋을 것이라고 생각했기 때문이다.
