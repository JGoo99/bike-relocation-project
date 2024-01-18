# 🚲 따릉이 재배치 어플리케이션 [진행중]

따릉이가 밀집된 대여소를 기준으로 거치율이 낮은 대여소를 추천하는 서비스입니다.  
(JSON 형태의 빅데이터를 다룬 백엔드 RESTful API 개발)

[//]: # ([플레이 시연 영상 보러가기👉🏻👉🏻]&#40;&#41;)

<br/><br/>

## 목차
- [전체적인 구조](#전체적인-구조-계획)
- [개요](#개요)
- [개발환경](#개발환경)
- [기능 및 설계](#기능-및-설계)
- [ERD](#erd)

<br/>

## 전체적인 구조 계획

따릉이 대여소의 모든 정보를 제공하며, 밀집된 대여소에서 거치율이 낮은 대여소를 거리가 가까운 순으로 정렬하여 제시합니다.

> 유저 : 자전거 재배치 예정목록을 등록할 수 있어요!

<br/>

[//]: # (<img src="" width="60%">)

| 데이터 불러오기 버튼을 눌러 가장 최신 데이터를 가져옵니다.  
| 유저는 따릉이 대여소의 정보를 확인하고 재배치를 원하는 대여소를 선택합니다.  
| 따릉이가 부족한 주변 대여소 리스트를 확인합니다.  
| 밀집 대여소, 부족 대여소, 재배치할 자전거의 개수를 입력하여 재배치 예정 정보를 등록합니다.

<br/>

## 개요

- 개발 인원 : 1인 창작 프로젝트
- 개발 기간 : 2024.01.17 ~ 진행중 (5주간 진행 예정)

<br/>

## 개발환경

<div>
    <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
    <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=java&logoColor=white">
    <img src="https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=java&logoColor=white">
</div>

> Spring Boot 3.1.5 (JDK 17)
>
> Java 17
>
> Gradle - Groovy
>
> JPA
>
> MariaDB
>
> Spring Security
> 
> JWT
>
> Lombok
> 
> Gson
>
> Validation

따릉이 대여소 데이터는 [공공데이터포털](https://data.seoul.go.kr/)을 이용했습니다.

<br/>

## 기능 및 설계

### 회원가입 기능
따릉이 재배치 기사 활동을 원하는 사용자는 회원가입을 할 수 있다. 메인 페이지를 제외하고 모든 페이지는 접근 권한(ROLE_USER)이 필요하다.

```text
- POST “/users/signup”
- Param : @RequestBody AuthDto.JoinRequest [이름, 이메일, 비밀번호, 전화번호]
- 실패 : @Valid 정규식에서 벗어난 경우
- 성공 응답 : ResponseEntity.ok(AuthDto.JoinResponse) [DB 유저정보, 유저 id, jwt 인증키(확인용)]
```

### 로그인 기능
사용자는 로그인을 할 수 있다. 로그인 시 JWT 인증토근이 부여된다.

```text
- POST “/users/signin”
- Param : @RequestBody AuthDto.Login [이메일, 비밀번호]
- 실패 : 회원 이메일이 아닌 경우, 비밀번호가 일치하지 않는 경우, 정규식에서 벗어난 경우
- 성공 응답 : ResponseEntity.ok(Auth.Login)
```

### 대여소 오픈데이터 불러오기(DB저장)
공공데이터포털에서 따릉이 데이터를 가져와 로컬 DB에 저장한다.

```text
- POST “/api/open-api”
- 실패 : 오픈데이터 필드 변경, 서버 오류 등의 경우, DB에 저장되지 않은 경우, 권한이 없는 경우
- 성공 응답 : ResponseEntity.ok(ParsingResultDto) [총 데이터 수 , … ?]
```

### 따릉이 대여소 전체 리스트 보여주기
모든 따릉이 대여소 정보를 페이징처리하여 보여준다. (현재 데이터 총 개수 2723개)

```text
- GET “/api/list.do”
- 실패 : 권한이 없는 경우, DB 데이터가 없는 경우
- 성공 응답 : ResponseEntity.ok(Page<BikeStationInfoDto>) [대여소 ID, 대여소이름, 주소1, 거치율]
```

### 기본 상세정보 보기

해당 대여소에 대한 상세한 정보를 보여준다.

```text
- GET “/api/detail.do”
- Param : @RequestParam(”bikeStationId”) Long
- 실패 : 권한이 없는 경우, 대여소 id가 데이터에 없는 경우
- 성공 응답 : ResponseEntity.ok(BikeStationDetailDto) [대여소 ID, 대여소이름, 주소1, 주소2, 거치대개수, 자전거주차총건수, 거치율]
```

### 해당 대여소에서 가까운 10개의 거치율 부족 대여소 리스트

해당 대여소에서 거치율이 100%보다 낮은 10개의 대여소를 거리순으로 보여준다.

```text
- GET “/api/low-rack-rate-list”
- Param : @RequestParam(”bikeStationId”) Long
- 실패 : 권한이 없는 경우, 대여소 id가 데이터에 없는 경우, 해당 대여소의 주소데이터가 유효하지 않은 경우
```

<br/><br/>

## 추가구현 예정

### 유저(재배치 기사)의 재배치 예약

유저는 밀집된 대여소, 거치율이 낮은 대여소, 재배치할 자전거의 개수를 선택하고  재배치를 예약할 수 있다.

```text
- POST “/api/relocation-reservation
- Param : @RequestBody ReservationDto [밀집대여소 id, 부족대여소 id, 자전거 개수]
- 실패 : 권한이 없는 경우, 각 대여소 id가 없는 경우
- 방지 : 선택한 자전거 개수 < 총 거치된 자전거 개수, 거치율이 낮은 대여소에 가져다놓을 때 거치율 ≤ 100%
```

### 대여소의 상세정보를 보여줄 때 카카오맵 api 를 활용하여 지도띄우기
세부계획 미정

<br/><br/>

## 구현 순서

<img src="https://github.com/JGoo99/bike-relocation-project/assets/126454114/65b99f28-f933-4d04-8103-bb212bea2587" width="100%">

<br/>

## ERD

<img src="https://github.com/JGoo99/bike-relocation-project/assets/126454114/602362a2-9a8c-4eb4-a004-80b9cc804266" width="100%">


