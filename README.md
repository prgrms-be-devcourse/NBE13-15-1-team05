# README 필수 내용
### 1. 팀원별 담당 작업, 공동 구현 시 각자의 작업 내용
- 염대원 : 환경설정, 메뉴 CRUD + 심화 redis, 날짜별 통계
- 성은재 : 카테고리 CRUD 및 메뉴 조회 API 작성, 입력값·비즈니스 규칙 검증
- 우승명 : 주문 접수·조회·재주문 API 작성, 날짜별 배송 묶음 조회
- 이상철 : 주문 취소·메모 수정 API 작성, 날짜별 주문·고객 통계

### 2. 완료·미완료 기능과 미해결 오류 유무(없으면 없다고 표시)
- 완료 : 필수 API A01~A24 (24개)
- 선택 심화 완료 : Redis 메뉴 목록 캐시, 업무 규칙(시각 경계값) 테스트
- 선택 심화 미완료 : 페이징, RestClient, 롤백 테스트
- 미해결 오류 : 없음

### 3. 실제 Java·Spring Boot·MySQL 버전, DB 설정 위치/환경변수 이름과 예시 값
- 자바 : 21
- 스프링부트 : 4.1.2
- MYSQL : 8.0
- Redis : 7 (localhost:6379)
- DB 설정 위치 : `src/main/resources/application.yaml`
  - 프로젝트 루트의 `.env` 파일을 읽음 (`spring.config.import: optional:file:.env[.properties]`)
- 환경변수 (`.env` 예시)
  ```
  MYSQL_PORT=3306
  MYSQL_DATABASE=cafe
  MYSQL_USER=user
  MYSQL_PASSWORD=비밀번호
  ```

### 4. 새 DB 준비·초기 데이터 등록 절차, 백엔드·web 실행 폴더·명령·주소
1. MySQL·Redis 실행
   ```
   docker run -d --name redis -p 6379:6379 redis:7-alpine
   ```
2. 새 DB에 초기 데이터 SQL 실행 (프로젝트 루트 `init.sql`, **새 DB에서 한 번만** 실행)
   - `cafe` DB와 테이블 4개 생성, 카테고리 1·2 / 메뉴 1~4 등록
   - 다시 실행하면 초기 데이터가 중복 등록되므로, 초기화가 필요하면 테이블을 지운 뒤 실행
   ```
   docker exec -i <MySQL 컨테이너 이름> mysql -u<사용자> -p < init.sql
   ```
3. 백엔드 실행 : 프로젝트 루트에서 `./gradlew bootRun` → http://127.0.0.1:8080
4. web 실행 : 제공받은 web 폴더(저장소에 포함하지 않음)에서
   ```
   java -m jdk.httpserver -b 127.0.0.1 -p 8000
   ```
   → http://127.0.0.1:8000/index.html

### 5. 테스트 실행 명령, .http 경로·변수·실행 순서, 시각 경계값 확인 방법
- 테스트 : `./gradlew test` (19개)
  - Controller(MockMvc) 6개 : 카테고리·메뉴·통계의 정상 / 400 / 404 / 409
  - 업무 규칙 9개 : 주문 취소·메모(`OrderChangeServiceTest`) 4개, 배송일(`ShippingDateTest`) 3개, 통계(`StatisticsServiceTest`) 2개
  - 입력 정리(`TextNormalizerTest`) 3개, 애플리케이션 구동 1개
- .http : `requests/api-check.http`
  - 변수 : `@baseUrl`, 고정 변수(`@categoryId`, `@menuId`, `@toggleMenuId`, `@email`, `@keyword`), 직접 입력 변수(`@newCategoryId`, `@newMenuId`, `@orderId`, `@shippingDate`, `@date`)
  - 실행 순서 : `init.sql` 직후의 새 DB에서 위에서부터 순서대로 실행 (카테고리 → 메뉴 → 주문 → 통계 → 오류 요청 6개)
  - 직접 입력 변수는 앞 요청(A03, A08, A11)의 응답 값으로 바꿔 넣음
- 시각 경계값 확인 : PC 시계를 바꾸지 않고 테스트에서 서울 시각을 고정해 확인
  - 서버 시각 : `global/config/TimeConfig.java`의 `Clock` (Asia/Seoul)
  - `ShippingDateTest` : 13:59:59 주문 → 당일 배송, 14:00:00 주문 → 다음 날 배송, 전날 23:59:59와 당일 00:00:00 주문은 같은 배송일
  - `OrderChangeServiceTest` : `Clock.fixed`로 배송일 13:59:59 취소 성공, 14:00:00 취소 409, 재취소 시 최초 취소 시각 유지

### 6. 평가 대상 브랜치·태그 또는 커밋, 선택 심화 코드·검증 방법
- https://github.com/prgrms-be-devcourse/NBE13-15-1-team05
- 브랜치 : master
- 선택 심화 코드
  - Redis 캐시 : `global/redis/CacheConfig.java`, `MenuReadService`(`@Cacheable`), `MenuService`(`@CacheEvict`)
  - 업무 규칙 테스트 : `src/test/java/team05/cafe/order/` (`ShippingDateTest`, `change/OrderChangeServiceTest`)
- 검증 방법 : 7번 참고 / `./gradlew test`

### 7. 메뉴 레디스
- docker run -d --name redis -p 6379:6379 redis:7-alpine
- A06  name=menus, key=all
- A19  name=menus, key=availableList
- TTL 600s
- A08·A09·A10·A21 실행 시 `@CacheEvict(name=menus, allEntries=true)`로 메뉴 캐시 전체 삭제
- 확인 : `docker exec redis redis-cli KEYS "menus::*"` (A06·A19 조회 후 키 생성, 메뉴 변경 후 삭제)
- 비우기 : `docker exec redis redis-cli DEL "menus::all" "menus::availableList"`
