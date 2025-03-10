-- 데이터베이스 생성 (초기 DB 생성)
CREATE DATABASE IF NOT EXISTS facticle;
USE facticle;

--사용자 테이블 생성
CREATE TABLE users (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(255) UNIQUE,
    nickname VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(100),
    profile_image VARCHAR(255),
    social_provider VARCHAR(30),
    social_id VARCHAR(255),
    role ENUM('ADMIN', 'USER') NOT NULL,
    signup_type ENUM('LOCAL', 'SOCIAL') NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB;


-- 리프레시 토큰 테이블 생성
CREATE TABLE refresh_tokens (
    token_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hashed_refresh_token VARCHAR(255) NOT NULL,
    is_revoked BIT NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT FK_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 유저 활동 테이블 생성
CREATE TABLE user_activities (
    activity_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NULL,
    activity_type ENUM('COMMENT', 'HATE', 'LIKE', 'RATE', 'VIEW') NOT NULL,
    CONSTRAINT FK_user_activities_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 뉴스 테이블 생성
CREATE TABLE news (
    news_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    image_url VARCHAR(255),
    media_name VARCHAR(255),
    naver_url VARCHAR(255),
    category ENUM('IT_과학', '경제', '국제', '날씨', '문화', '사회', '스포츠', '연예', '정치') NOT NULL,
    headline_score DECIMAL(5,2) NOT NULL,
    fact_score DECIMAL(5,2) NOT NULL,
    headline_score_reason TEXT NOT NULL,
    fact_score_reason TEXT NOT NULL,
    collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    like_count INT NOT NULL DEFAULT 0,
    hate_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    rating_count INT NOT NULL DEFAULT 0,
    rating DECIMAL(2,1) NOT NULL DEFAULT 0.0
) ENGINE=InnoDB;

-- 뉴스 컨텐츠 테이블 생성
CREATE TABLE news_content (
    news_id BIGINT NOT NULL PRIMARY KEY,
    content TEXT NOT NULL,
    CONSTRAINT FK_news_content FOREIGN KEY (news_id) REFERENCES news(news_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 인덱스 추가
CREATE INDEX idx_category ON news (category);
CREATE INDEX idx_headline_score ON news (headline_score);
CREATE INDEX idx_fact_score ON news (fact_score);
CREATE INDEX idx_collected_at ON news (collected_at);
CREATE INDEX idx_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_nickname ON users (nickname);
CREATE INDEX idx_username ON users (username);
CREATE INDEX idx_social_provider_id ON users (social_provider, social_id);