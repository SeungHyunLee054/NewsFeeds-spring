# News_Feeds

뉴스 피드 프로젝트

## 기능

### 1. 회원 가입

- 유저 정보를 입력 받은 후 닉네임과 이메일 중복검증 후 저장 진행

### 2. 로그인

- 이메일과 비밀번호를 입력 받아 해당 유저가 탈퇴했는지 검증 후 비밀번호 검증 후 access token과 refresh token 발급

### 3. 로그아웃

- 로그인한 access token을 black list로 지정 해당 토근 사용 불가 처리, 컨트롤러에서 해당 토큰의 principal 제거

### 4. 회원 탈퇴

- 유저의 탈퇴 상태를 delete = true로 설정, 닉네임과 민감 정보를 임의의 값으로 변경

### 5. 토큰 갱신

- 최초 로그인 시 발급했던 refresh token으로 access token 재발급

### 6. 유저 프로필 조회

- 조회하려는 유저가 본인이면 모든 정보 반환, 아닐 시 민감한 정보 제외 반환

### 7. 유저 프로필 수정

- 닉네임과 비밀번호 수정, 수정 값이 둘 다 없는지 검증, 비밀번호 값이 기존과 동일한지 검증, 유저의 비밀번호가 맞는지 검증

## [ERD](https://seunghyun937.notion.site/ERD-1cec72e464458045a47ffdbbd8ad9603?pvs=4)

## [Wire Frame](https://seunghyun937.notion.site/1cec72e464458071ba0ccfd3c489bf41?pvs=4)

## [API 명세서](https://seunghyun937.notion.site/API-1cec72e4644580a488a7c17ca3b64202?pvs=4)

## [트러블 슈팅](https://seunghyun937.notion.site/1d2c72e464458037b9a6fb5157fb8fe9?pvs=4)

## 기술 스택

### Language

- Java17

### Framework

- SpringBoot 3.4.4

### Build Tool

- Gradle

### Database

- MySQL, Redis

### SVN

- Git, GitHub

## 프로젝트 폴더 구조

```` text
├───main
│   ├───java
│   │   └───com
│   │       └───nbc
│   │           └───newsfeeds
│   │               ├───common
│   │               │   ├───audit
│   │               │   ├───config
│   │               │   ├───exception
│   │               │   │   ├───dto
│   │               │   │   └───handler
│   │               │   ├───filter
│   │               │   │   ├───constant
│   │               │   │   └───exception
│   │               │   ├───jwt
│   │               │   │   ├───constant
│   │               │   │   ├───core
│   │               │   │   ├───dto
│   │               │   │   └───exception
│   │               │   ├───redis
│   │               │   │   ├───dto
│   │               │   │   ├───exception
│   │               │   │   ├───repository
│   │               │   │   ├───service
│   │               │   │   └───vo
│   │               │   ├───request
│   │               │   ├───response
│   │               │   └───util
│   │               └───domain
│   │                   ├───comment
│   │                   │   ├───code
│   │                   │   ├───controller
│   │                   │   ├───dto
│   │                   │   │   ├───request
│   │                   │   │   └───response
│   │                   │   ├───entity
│   │                   │   ├───exception
│   │                   │   ├───repository
│   │                   │   └───service
│   │                   ├───feed
│   │                   │   ├───code
│   │                   │   ├───controller
│   │                   │   ├───dto
│   │                   │   ├───entity
│   │                   │   ├───exception
│   │                   │   ├───repository
│   │                   │   └───service
│   │                   ├───friend
│   │                   │   ├───controller
│   │                   │   ├───entity
│   │                   │   ├───exception
│   │                   │   ├───model
│   │                   │   │   ├───request
│   │                   │   │   └───response
│   │                   │   ├───repository
│   │                   │   └───service
│   │                   ├───heart
│   │                   │   ├───controller
│   │                   │   ├───dto
│   │                   │   ├───entity
│   │                   │   ├───exception
│   │                   │   ├───repository
│   │                   │   └───service
│   │                   └───member
│   │                       ├───auth
│   │                       ├───constant
│   │                       ├───controller
│   │                       ├───dto
│   │                       │   ├───request
│   │                       │   └───response
│   │                       ├───entity
│   │                       ├───exception
│   │                       ├───repository
│   │                       └───service
│   └───resources
│       ├───static
│       └───templates
└───test
    └───java
        └───com
            └───nbc
                └───newsfeeds
                    └───domain
                        ├───comment
                        │   └───service
                        ├───feed
                        │   ├───controller
                        │   ├───entity
                        │   └───service
                        ├───friend
                        │   ├───controller
                        │   ├───entity
                        │   └───service
                        ├───heart
                        │   └───service
                        ├───member
                        │   ├───controller
                        │   └───service
                        └───support
                            ├───fixture
                            └───security
````
