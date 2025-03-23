# 🌐 Spring Boot 커뮤니티 프로젝트

Spring Boot 기반의 커뮤니티 웹 애플리케이션입니다.\
JWT 인증, 게시글 및 댓글 CRUD, 프리플/게시물 이미지 업로드 및 관리 기능 등을 포함합니다.

---

## 📦 개발 환경 및 주요 스택

-   **Language**: Java 17 (Homebrew 설치 버전)
-   **Framework**: Spring Boot
-   **Database**: MySQL
-   **ORM**: Spring Data JPA (Hibernate)
-   **JWT**: Apache JJWT
-   **Build Tool**: Gradle
-   **기타**: Lombok, File I/O

---

## 📁 주요 의존성

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

-   토큰 만료 시 `ExceptionHandler`가 다른 프리세스 필터로 바뀌기 드릴 수 없음
-   Spring Security의 `AuthenticationEntryPoint`로 대체 가능

---

## 🖼️ 파일 업로드 및 이미지 관리

-   회원 프리플, 게시글 이미지 업로드 기능
-   로컬 디렉토리에 저장 (`/upload/profiles`, `/upload/post`)
-   이미지 변경/삭제 시 기존 파일 삭제 처리 포함

---

## 🚀 고민 태겟 & 테크니티 내용

-   `getMe`는 JWT 확인을 위해 모두의 페이지에서 한 번만 호출하고, 인증만 간단히 호출하는 것이 더 효율적일 것.
-   게시글 좋아요 수 등은 `likeList.size()` 대신 `likeCount` 필드를 관리해 쿼리 최적화
-   게시글, 프로필 이미지 변경 시 이전 파일 삭제 처리
-   @Transactional로 LAZY 로딩 문제 해결
-   Entity 간 양방향 참조 시 StackOverflow 방지를 위해 toString 제외 처리

---

## 📈 현재 고민 기사

-   댓글만 리렌더링할 경우, 그 사이에 작성자의 정보가 바뀌면 반영이 안 되는 문제 (해결 필요)
-   이미지 저장 방식으로 MongoDB가 아닌 디렉토리 저장 방식 선택
-   예외 전파를 위한 ExceptionHandler 동작 시점 및 필터 순서 이슈
-   좋아요 등 간단한 상태 변경 시 불필요한 쿼리 호출 최적화 필요

---

✅ TODO

-   좋아요 요청 시, 불필요한 쿼리 호출 여부 확인 및 최적화 필요
    (예: likeList 전체를 조회하지 않고 별도의 flag 사용 등)
-   다른 API에도 불필요한 쿼리 호출이 없는지 점검 필요
-   인증을 위해 getMe 호출 시, 실제 인증과 데이터 조회를 분리할지에 대한 고민
    (예: getMe는 한 번만 호출하고, 인증 여부만 확인하는 별도 API 고려)
-   댓글 등록 후 리렌더링 방식 개선
    (현재는 새로고침해야 반영됨. 댓글만 다시 fetch하여 부분 렌더링하는 방식 고려)
-   전체 API 흐름 점검 및 불필요한 연산/쿼리 최소화
