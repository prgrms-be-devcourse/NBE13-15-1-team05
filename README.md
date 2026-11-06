# README 필수 내용
### 1. 팀원별 담당 작업, 공동 구현 시 각자의 작업 내용
- 염대원 : 환경설정, 메뉴 CRUD + 심화 redis, 날짜별 통계
- 성은재 : 카테고리 CRUD 및 메뉴 조회 API 작성, 입력값·비즈니스 규칙 검증
- 우승명 : 
### 2. 완료·미완료 기능과 미해결 오류 유무(없으면 없다고 표시)
없음
### 3. 실제 Java·Spring Boot·MySQL 버전, DB 설정 위치/환경변수 이름과 예시 값
- 자바 : 21
- 스프링부트 : 4.1.2
- MYSQL : 8.0
### 4. 새 DB 준비·초기 데이터 등록 절차, 백엔드·web 실행 폴더·명령·주소
- ddl-auto : UPDATE
```mysql
#초기 카데고리
insert into category(name)
values ('싱글오리진'), ('블렌드');

#초기 메뉴
insert into menu( available, description, name, price, category_id)
values
(true, '콜롬비아 나리뇨 원두를 담은 커피빈 패키지입니다.' , 'Columbia Nariñó' ,5000,1),
(true, '브라질 세하 두 카파라오 원두를 담은 커피빈 패키지입니다.' , 'Brazil Serra Do Caparaó' ,7000,1),
(true, '콜롬비아 킨디오 원두를 담은 커피빈 패키지입니다.' , 'Columbia Quindío (White Wine Extended Fermentation)' ,9000,1),
(true, '에티오피아 시다모 원두를 담은 커피빈 패키지입니다.' , 'Ethiopia Sidamo' ,11000,1);
- ```


### 5 .테스트 실행 명령, .http 경로·변수·실행 순서, 시각 경계값 확인 방법
- 테스트 ./gradlew test
- request/api-check.http

### 6. 평가 대상 브랜치·태그 또는 커밋, 선택 심화 코드·검증 방법
- https://github.com/prgrms-be-devcourse/NBE13-15-1-team05
- 브랜치 : master 

### 7. 메뉴 레디스 
- docker run -d --name redis -p 6379:6379 redis:7-alpine
- A06  name=menus, key=all
- A19  name=menus, key=availableList
- TTL 600s 

- 8 9 10 21 사용시 
- CacheEvict  name=menus key allEntries
