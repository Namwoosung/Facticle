# Facticle

<p align="center">
  <img src="./FE/facticle/public/facticle.png" width="160" alt="Facticle logo" />
</p>

### AI 기반 신뢰도·유사도 분석을 제공하는 뉴스 큐레이션 플랫폼

Facticle은 생성형 AI를 활용해 뉴스의 요약, 제목-본문 정합성, 사실 기반 신뢰도 분석을 제공하는 뉴스 큐레이션 플랫폼입니다. 사용자는 기사 원문을 읽기 전에 AI 요약과 분석 점수, 점수 산출 근거를 함께 확인하며 뉴스를 탐색할 수 있습니다.

- **개발 기간**: 2024.04 - 2024.12
- **플랫폼**: Web
- **개발 인원**: 2명

---

## 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [실행 방법](#실행-방법)

---

## 주요 기능

- **AI 뉴스 요약**: 기사 본문을 기반으로 핵심 내용을 요약합니다.
- **유사도 분석**: 기사 제목과 본문 간 정합성을 `headlineScore`로 제공합니다.
- **신뢰도 분석**: 기사 내용의 사실 기반 신뢰도를 `factScore`로 제공합니다.
- **분석 근거 제공**: `headlineScoreReason`, `factScoreReason`을 통해 점수 산출 근거를 함께 보여줍니다.
- **뉴스 수집 자동화**: 네이버 뉴스, 엔터 뉴스, 스포츠 뉴스를 주기적으로 수집합니다.
- **사용자 인증**: 자체 로그인/회원가입과 카카오·네이버·구글 OAuth 로그인을 지원합니다.

---

## 기술 스택

### Frontend

- React 18
- TypeScript
- Vite
- React Router
- Styled Components
- Axios

### Backend

- Java 17
- Spring Boot 3.4.2
- Spring Security
- Spring Data JPA
- Querydsl
- MySQL
- Elasticsearch
- JWT
- Azure Blob Storage

### Crawler / AI

- Python
- APScheduler
- BeautifulSoup4
- newspaper4k
- OpenAI API
- SQLAlchemy
- PyMySQL
- Elasticsearch Python Client

### Infra

- Docker
- Docker Compose

---

## 프로젝트 구조

```text
Facticle
├── BE/facticle/                 # Spring Boot 백엔드
│   ├── src/main/java/com/example/facticle/
│   │   ├── common/              # 공통 설정, 인증, 예외, DTO
│   │   ├── news/                # 뉴스 API, 도메인, Repository, Service
│   │   └── user/                # 사용자, 인증, OAuth
│   ├── Dockerfile
│   └── build.gradle
├── FE/facticle/                 # React 프론트엔드
│   ├── src/components/          # 공통 및 기능별 UI 컴포넌트
│   ├── src/pages/               # 홈, 뉴스, 인증, 마이페이지
│   ├── src/services/            # API 통신 모듈
│   ├── Dockerfile
│   └── package.json
├── Crawler/facticle-crawler/    # 뉴스 크롤러 및 AI 후처리
│   ├── app.py                   # 크롤러 실행 엔트리
│   ├── news_crawler.py
│   ├── enter_crawler.py
│   ├── sports_crawler.py
│   ├── postprocess.py
│   └── requirements.txt
└── docker-compose.yml           # MySQL, Backend, Frontend 통합 실행
```

---

## 실행 방법

### Docker Compose

```bash
docker compose up -d --build
```

`docker-compose.yml`은 MySQL, Backend, Frontend 컨테이너를 함께 실행합니다. Backend와 Frontend는 각각 `.env` 파일을 참조합니다.

### Backend

```bash
cd BE/facticle
./gradlew bootRun
```

### Frontend

```bash
cd FE/facticle
npm install
npm run dev
```

### Crawler

```bash
cd Crawler/facticle-crawler
pip install -r requirements.txt
python app.py
```
