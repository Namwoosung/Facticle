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

-- 뉴스 테이블 생성
CREATE TABLE news (
    news_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    image_url VARCHAR(255),
    media_name VARCHAR(255),
    naver_url VARCHAR(255),
    category ENUM('POLITICS', 'ECONOMY', 'SOCIETY', 'INTERNATIONAL', 'TECH', 'CULTURE', 'ENTERTAINMENT', 'SPORTS', 'WEATHER') NOT NULL,
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

--뉴스 상호작용 테이블 생성  --시간은 모두 마지막에 인터랙션한 시간, 현재 프로젝트에서는 과거의 유저 인터랙션까지 전부 기록하지는 않고, 가장 최근 인터랙션만 기록
-- 만약 추후 과거 행적까지 기록해야 하면, one-to-one이 아니라 many-to-one으로 바꾸고, 하나의 유저가 하나의 뉴스에 대해 여러 인터랙션일 할 수 있게 관리
CREATE TABLE news_interactions (
    news_interaction_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    reaction ENUM('LIKE', 'HATE') DEFAULT NULL,
    rating DECIMAL(2,1) NOT NULL DEFAULT 0.0,
    reaction_at TIMESTAMP,
    rated_at TIMESTAMP,
    viewed_at TIMESTAMP,
    CONSTRAINT FK_news_interactions_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_news_interactions_news FOREIGN KEY (news_id) REFERENCES news(news_id) ON DELETE CASCADE,
    CONSTRAINT unique_user_news UNIQUE (news_id, user_id)
) ENGINE=InnoDB;

-- 댓글 테이블 생성
CREATE TABLE comments (
    comment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    news_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    hate_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id BIGINT,
    CONSTRAINT FK_comments_news FOREIGN KEY (news_id) REFERENCES news(news_id) ON DELETE CASCADE,
    CONSTRAINT FK_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 댓글 상호작용 테이블 생성
CREATE TABLE comment_interactions (
    comment_interaction_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    reaction ENUM('LIKE', 'HATE') DEFAULT NULL,
    reaction_at TIMESTAMP,
    CONSTRAINT FK_comment_interactions_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_comment_interactions_comment FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
    CONSTRAINT unique_user_comment UNIQUE (comment_id, user_id)
) ENGINE=InnoDB;



-- 인덱스 추가
CREATE INDEX idx_category ON news (category);
CREATE INDEX idx_headline_score ON news (headline_score);
CREATE INDEX idx_fact_score ON news (fact_score);
CREATE INDEX idx_collected_at ON news (collected_at);
CREATE INDEX idx_media_name ON news (media_name);
CREATE INDEX idx_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_nickname ON users (nickname);
CREATE INDEX idx_username ON users (username);
CREATE INDEX idx_social_provider_id ON users (social_provider, social_id);

CREATE INDEX idx_news_interactions_user_id ON news_interactions (user_id);
CREATE INDEX idx_news_interactions_news_id ON news_interactions (news_id);
CREATE INDEX idx_comments_news_id ON comments (news_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comment_interactions_user_id ON comment_interactions (user_id);
CREATE INDEX idx_comment_interactions_comment_id ON comment_interactions (comment_id);


-- DB 테이블의 데이터 사제
SET FOREIGN_KEY_CHECKS = 0;  -- 외래 키 제약 조건 해제
TRUNCATE TABLE news;
TRUNCATE TABLE news_content;
SET FOREIGN_KEY_CHECKS = 1;  -- 외래 키 제약 조건 설정
