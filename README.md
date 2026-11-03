# README 필수 내용
### 1. 팀원별 담당 작업, 공동 구현 시 각자의 작업 내용
### 2. 완료·미완료 기능과 미해결 오류 유무(없으면 없다고 표시)
### 3. 실제 Java·Spring Boot·MySQL 버전, DB 설정 위치/환경변수 이름과 예시 값
### 4. 새 DB 준비·초기 데이터 등록 절차, 백엔드·web 실행 폴더·명령·주소
### 5 .테스트 실행 명령, .http 경로·변수·실행 순서, 시각 경계값 확인 방법
./gradlew test
request/api-check.http

### 6. 평가 대상 브랜치·태그 또는 커밋, 선택 심화 코드·검증 방법


### 7. 메뉴 레디스 
docker run -d --name redis -p 6379:6379 redis:7-alpine
A06  name=menus, key=all
A19  name=menus, key=availableList
TTL 600s 

8 9 10 21 사용시 
CacheEvict  name=menus key allEntries