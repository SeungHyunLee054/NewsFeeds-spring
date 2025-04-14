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

### 8. 친구 요청

- 자기 자신에게 보낸 요청인지, 존재하지 않는 사용자에게 보낸 요청 인지, 친구 요청이 이미 존재하는지, 이미 친구 상태인지 검증 후 친구 요청을 수행

### 9. 친구 요청 응답

- 본인이 받은 요청인지, 친구 요청 상태가 맞는지 검증 후 수락 혹은 거절을 응답

### 10. 친구 요청 취소

- 존재하는 요청인지, 자신이 보낸 요청인지, 친구 요청 상태가 맞는지 검증 후 친구 요청을 취소

### 11. 친구 삭제

- 본인의 친구가 맞는지, 친구 상태가 맞는지 확인 후 친구를 삭제

### 12. 친구 목록 조회

- 친구 목록을 조회하여 커서 기반 페이징을 수행하여 반환

### 13. 받은 친구 요청 목록 조회

- 받은 친구 요청 목록을 조회하여 커서 기반 페이징을 수행하여 반환

### 14. 보낸 친구 요청 목록 조회

- 보낸 친구 요청 목록을 조회하여 커서 기반 페이징을 수행하여 반환

### 15. 게시글 좋아요 조회

- 게시글의 좋아요 갯수를 조회하여 반환

### 16. 게시글 좋아요 추가

- 게시글의 좋아요를 하였는지 확인하고, 하지 않았다면 추가

### 17. 게시글 좋아요 취소

- 게시글의 좋아요를 하였는지 확인하고, 했다면 취소

### 18. 댓글 좋아요 조회

- 댓글의 좋아요 갯수를 조회하여 반환

### 19. 댓글 좋아요 추가

- 댓글의 좋아요를 하였는지 확인하고, 하지 않았다면 추가

### 20. 댓글 좋아요 취소

- 댓글의 좋아요를 하였는지 확인하고, 했다면 취소

### 21. 게시글 작성

- 게시글을 작성합니다.

### 22. 게시글 수정

- 게시글의 유무를 확인하고, 게시글의 내용을 수정

### 23. 게시글 ID 기반 단건 조회

- 게시글의 ID를 기반으로 단건 조회를 진행

### 24. 게시글 전건 조회

- 삭제된 게시물을 제외하고, 전체 게시글을 조회

### 25. 게시글 조건식 다건 조회

- 주간, 월간, 특정기간 별 조회 및 댓글 수 / 좋아요 수 / 수정 일 기준으로 정렬 조회

### 26. 게시글 삭제

- 작성한 게시글을 소프트 딜리트

### 27. 댓글 생성

- 특정 게시글(feedId)에 대해 댓글 작성
- 로그인된 사용자의 정보를 바탕으로 댓글을 저장 및 게시글의 댓글 수를 증가
- 인증 필요 (Bearer Token)

### 28. 게시글 댓글 목록 조회

- 게시글 ID(feedId)로 해당 게시글에 달린 댓글 목록(좋아요 수 포함)을 페이징하여 조회
- 최신순으로 정렬되며, 기본 페이지 사이즈는 10

### 29. 댓글 단일 조회

- 댓글 ID를 기반으로 특정 댓글의 상세 정보(좋아요 수 포함)를 조회

### 30. 댓글 수정

- 댓글 작성자 본인만 수정 가능
- 댓글 ID를 기반으로 내용(content)을 수정
- 인증 필요 (Bearer Token)

### 31. 댓글 삭제

- 댓글 작성자 본인만 삭제 가능
- 댓글 삭제 시 해당 게시글의 댓글 수를 감소
- 인증 필요 (Bearer Token)

## [ERD](https://seunghyun937.notion.site/ERD-1cec72e464458045a47ffdbbd8ad9603?pvs=4)

## [Wire Frame](https://seunghyun937.notion.site/1cec72e464458071ba0ccfd3c489bf41?pvs=4)

## [API 명세서](https://seunghyun937.notion.site/API-1cec72e4644580a488a7c17ca3b64202?pvs=4)

## [트러블 슈팅](https://seunghyun937.notion.site/1d2c72e464458037b9a6fb5157fb8fe9?pvs=4)

## [컨벤션](https://seunghyun937.notion.site/1d5c72e4644580718451cc6b14a3362e?pvs=4)

💻 Language

• Java 17

🚀 Framework & Platform

• Spring Boot 3.4.4

⚙️ Build Tool

• Gradle

🗄 Database

• MySQL

• Redis

🔧 Version Control

• Git

• GitHub

## 프로젝트 폴더 구조

```` text
├───main
│   ├───java
│   │   └───com
│   │       └───nbc
│   │           └───newsfeeds
│   │               ├───common
│   │               │   ├───aop
│   │               │   ├───audit
│   │               │   ├───config
│   │               │   ├───constant
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
│   │               │   ├───model
│   │               │   │   ├───request
│   │               │   │   └───response
│   │               │   ├───redis
│   │               │   │   ├───config
│   │               │   │   ├───constant
│   │               │   │   ├───dto
│   │               │   │   ├───exception
│   │               │   │   ├───repository
│   │               │   │   ├───service
│   │               │   │   └───vo
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
│   │                   │   ├───service
│   │                   │   └───validator
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
│   │                       ├───event
│   │                       ├───exception
│   │                       ├───listener
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
