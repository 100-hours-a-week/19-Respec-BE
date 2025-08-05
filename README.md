# 19-Respec-BE
카카오 테크 부트캠프 판교 2기 19조 백엔드 레포입니다

## ERD

[ERDCloud](https://www.erdcloud.com/d/4xxcrLbDnLu4ZyEoJ)

## 관련 블로그 글

1. [기술 스택 선정 과정 - 확장성과 안정성 사이의 균형점 찾기](https://aole.tistory.com/111)

2. [ERD 설계 과정에서 마주한 기술적 트레이드오프들](https://aole.tistory.com/116)

3. [채팅방 삭제 기능 설계 - 미래를 대비한 ERD 설계](https://aole.tistory.com/118)

4. [JPA 양방향 매핑과 CASCADE.ALL 도입 결정 과정](https://aole.tistory.com/132)

5. [분산 환경에서 1:1 채팅 서버 구축하기 - 이벤트 기반 아키텍처 도입기](https://aole.tistory.com/146)

6. [WebSocket + Kafka를 활용한 실시간 채팅 시스템 구현기](https://aole.tistory.com/171)

7. [테크 스펙 작성 - 문서로 하는 코딩](https://aole.tistory.com/174)


## 📋 개요

개발자들이 자신의 스펙(경력, 학력, 자격증 등)을 등록하고 비교할 수 있는 플랫폼의 백엔드 시스템입니다. 사용자 인증, 스펙 관리, 실시간 채팅, 소셜 기능, AI 기반 분석 등 다양한 기능을 제공합니다.

## ✨ 주요 기능

### 🔐 사용자 인증 시스템
- **OAuth2 소셜 로그인**: 카카오 로그인 지원
- **JWT 토큰 인증**: Access Token + Refresh Token 방식
- **보안 필터**: Spring Security 기반 인증/인가
- **자동 토큰 갱신**: 만료된 토큰 자동 갱신 스케줄러

### 📊 스펙 관리 시스템
- **종합 스펙 등록**: 학력, 경력, 자격증, 어학점수 등록
- **스펙 상세 관리**: 교육상세, 경력상세, 활동/네트워킹 등
- **스펙 조회**: 다양한 필터와 정렬 옵션
- **AI 분석**: AI서버로 응답을 활용해 스펙 데이터 기반 인사이트 제공

### 💬 실시간 채팅 시스템
- **WebSocket 통신**: 실시간 메시지 송수신
- **채팅방 관리**: 1:1 채팅방 자동 생성 및 관리
- **Kafka 연동**: 메시지 이벤트 스트리밍
- **Ping-Pong**: 연결 상태 확인 스케줄러

### 🤝 소셜 기능
- **북마크**: 관심 있는 사용자 북마크
- **댓글 시스템**: 스펙에 대한 댓글 및 대댓글
- **알림**: 실시간 알림 시스템

### 📄 이력서 관리
- **파일 업로드**: PDF 이력서 업로드 및 관리
- **S3 저장**: AWS S3 기반 파일 저장소

## 🏗️ 아키텍처

### 도메인 중심 설계
```
src/main/java/kakaotech/bootcamp/respec/specranking/
├── 🎯 domain/                     # 도메인 계층
│   ├── auth/                     # 인증 도메인
│   ├── user/                     # 사용자 도메인
│   ├── spec/                     # 스펙 도메인
│   ├── chat/                     # 채팅 도메인
│   ├── social/                   # 소셜 도메인
│   └── notification/             # 알림 도메인
├── 🌐 global/                     # 글로벌 설정
│   ├── common/                   # 공통 설정
│   ├── infrastructure/           # 인프라 계층
│   └── devsetup/                 # 개발 환경 설정
└── SpecRankingApplication.java   # 메인 애플리케이션
```

### 인프라 구조
```
Frontend (React) 
       ↓
Load Balancer
       ↓
Backend Server (Spring Boot)
       ├─ MySQL (데이터 저장)
       ├─ Redis (캐시 및 세션)
       ├─ AWS S3 (파일 저장)
       ├─ Kafka (메시지 스트리밍)
       └─ AI Server (스펙 분석)
```

## 📄 도메인별 상세 기능

### 🔐 Auth Domain
- OAuth2 소셜 로그인 (카카오)
- JWT 기반 인증/인가
- Refresh Token 관리
- 보안 설정 및 필터

### 👤 User Domain
- 사용자 정보 관리
- 프로필 이미지 업로드
- 닉네임 중복 검사

### 📊 Spec Domain
- 스펙 CRUD 관리
- 학력, 경력, 자격증 등록
- 스펙 상세 정보 관리
- 사용자 스펙 검색

### 💬 Chat Domain
- 실시간 WebSocket 통신
- 채팅방 자동 생성
- Kafka 메시지 프로듀싱

### 🤝 Social Domain
- 북마크 기능
- 댓글 및 대댓글 시스템
- 페이징 처리

### 🔔 Notification Domain
- 실시간 알림 시스템
- 알림 상태 관리


### 배포 환경
- **개발**: `dev` 브랜치 → dev 환경
- **스테이징**: `stage` 브랜치 → stage 환경  
- **운영**: `main` 브랜치 → prod 환경


## 🔧 개발 도구

### 개발 환경 초기화
프로젝트에는 개발용 데이터 초기화 클래스들이 포함되어 있습니다:
- `InitializeUser`: 테스트 사용자 생성
- `InitializeSpec`: 샘플 스펙 데이터 생성
- `InitializeChat`: 테스트 채팅 데이터 생성

### 프로필별 설정
- **local**: 로컬 개발 환경
- **dev**: 개발 서버 환경
- **stage**: 스테이징 환경
- **prod**: 운영 환경
  
## 📚 추가 자료

- **ChatConsumer 서비스**: [19-Respec-BE-Chatconsumer](https://github.com/100-hours-a-week/19-Respec-BE-Chatconsumer) - 채팅 메시지 처리 마이크로서비스
