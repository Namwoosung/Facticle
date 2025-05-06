import { useEffect, useState } from "react";
import { InfoLine, NewsContentBody, NewsContentBodySubText, NewsContentBodyText, NewsContentBodyTextWrapper, Comment, CommentsWrapper, CommentTextWrapper, CommentTItleNumber, CommentTitleWrapper, CommentWrapper, ConfiremButton, NewsContentDate, NewsContentHeader, NewsContentImg, NewsContentInfo, NewsContentLine, ReviewWrapper, StarWrapper, NewsContentTitle, UserCommentWrapper, NewsContentWrapper, ScoreContainer, ScoreText, ScoreWrapper, SideWrapper, StarContainer, StarText, Wrapper, CommentTextTitleWrapper, CommentTextTitle, CommentTextDate, CommentThumbsDownButton, CommentThumbsUpButton, CommentText, CommentReplyButton, CommentContainer, ReplyWrapper, ReplyContainer, ReplyInputWrapper } from "./newscontent.styles";
import Progress from "../../components/progress";
import PopularNews from "./popularnews";
import { FaStar, FaRegStarHalfStroke, FaRegStar } from "react-icons/fa6";
import { GiCheckMark } from "react-icons/gi";
import { IoMdSend } from "react-icons/io";
import Input from "../../components/input";
import Avatar from "../../components/avatar";
import Ranking from "./ranking";

function NewsContent() {
  const [rating, setRating] = useState(0);

  const handleClick = (value: number) => {
    setRating(value);
  };

  const renderStar = (index: number) => {
    if (rating >= index) return <FaStar size={60} color="#524DD6" />;
    if (rating >= index - 0.5) return <FaRegStarHalfStroke size={60} color="#524DD6" />;
    return <FaRegStar size={60} color="#ccc" />;
  };

  const handleCommentSubmit = () => {
    // 댓글 제출 로직
  };




  useEffect(() => { // 들어오자마자 요청 후 페이지 내용 렌더링
  }, [])


  return (
    <Wrapper>
      <NewsContentWrapper>
        <NewsContentHeader>
          <NewsContentTitle>테슬라, 中 전기차들 제기한 'EU 관세 소송' 동참</NewsContentTitle>
          <NewsContentInfo>
            <NewsContentDate>2025-01-25 16:50</NewsContentDate>

            <InfoLine />

            <ScoreWrapper>
              <ScoreContainer>
                <ScoreText>유사도</ScoreText>
                <Progress progress={80} color="black" />
                <ScoreText>80%</ScoreText>
              </ScoreContainer>
              <InfoLine />
              <ScoreContainer>
                <ScoreText>신뢰도</ScoreText>
                <Progress progress={90} color="black" />
                <ScoreText>90%</ScoreText>
              </ScoreContainer>
            </ScoreWrapper>
          </NewsContentInfo>
          <StarContainer>
            <FaStar style={{ color: "#524DD6" }} />
            <StarText>4.5</StarText>
          </StarContainer>
        </NewsContentHeader>

        <NewsContentBody>
          <NewsContentImg src="https://img.etnews.com/news/article/2024/07/19/news-p.v1.20240719.a82bdabb58a54758bed0997cd30cb2d3_P1.jpg" alt="news" />
          <NewsContentBodyTextWrapper>
            <h2>🤖 핵심 요약</h2>
            <NewsContentBodySubText>(AI가 기사 본문을 분석해 핵심 내용을 추출하여 요약해드립니다.)</NewsContentBodySubText>
          </NewsContentBodyTextWrapper>
          <NewsContentBodyText> 이에 지난해 10월부터 중국산 전기차 관세율이 최소 17.8%에서 최고 45.3%까지 증가했다. 중국 상하이공장에서 제조하는 테슬라가
            가장 낮은 17.8％를 부과받았고 BYD와 지리는 각각 27.0％, 28.8％, SAIC은 가장 높은 45.3％ 관세를 각각 책정받았다.</NewsContentBodyText>

          <NewsContentLine />

          <ReviewWrapper>
            <h3>이 기사의 별점을 매기세요!</h3>
            <StarWrapper>
              {[1, 2, 3, 4, 5].map((star) => (
                <div key={star} style={{ position: "relative", width: 60, height: 60 }}>
                  {/* 왼쪽 반 클릭: 0.5점 */}
                  <div
                    style={{
                      position: "absolute",
                      width: "50%",
                      height: "100%",
                      left: 0,
                      top: 0,
                      zIndex: 1,
                      cursor: "pointer",
                    }}
                    onClick={() => handleClick(star - 0.5)}
                  />
                  {/* 오른쪽 반 클릭: 1점 */}
                  <div
                    style={{
                      position: "absolute",
                      width: "50%",
                      height: "100%",
                      right: 0,
                      top: 0,
                      zIndex: 1,
                      cursor: "pointer",
                    }}
                    onClick={() => handleClick(star)}
                  />
                  {renderStar(star)}
                </div>
              ))}

            </StarWrapper>
            <ConfiremButton>확인 <GiCheckMark style={{ color: '#009045' }} size={16} /></ConfiremButton>
          </ReviewWrapper>

          <NewsContentLine />

          <CommentWrapper>
            <CommentTitleWrapper>
              <h3>댓글</h3>
              <CommentTItleNumber>{3}</CommentTItleNumber>
            </CommentTitleWrapper>
            {/* <Input placeholder="댓글을 입력하세요" /> */}
          </CommentWrapper>
        </NewsContentBody>
        <UserCommentWrapper>
          <Input
            type="text"
            value=""
            onChange={() => { }}
            placeholder="댓글을 입력하세요"
            icon={<IoMdSend size={30} color="#ccc" onClick={handleCommentSubmit} />}
          />
        </UserCommentWrapper>
        <CommentsWrapper>
          <Comment>
            <Avatar size={40} src="https://avatars.githubusercontent.com/u/12345678?v=4" alt="User Avatar" />
            <CommentContainer>
              <CommentTextWrapper>
                <CommentTextTitleWrapper>
                  <CommentTextTitle>양신희</CommentTextTitle>
                  <CommentTextDate>01/25 16:50</CommentTextDate>
                  <CommentThumbsUpButton size={20} />
                  <CommentTextDate>0</CommentTextDate>
                  <CommentThumbsDownButton size={20} />
                  <CommentTextDate>0</CommentTextDate>
                </CommentTextTitleWrapper>
                <CommentText>이 기사는 정말 흥미롭네요! 테슬라의 전략이 궁금합니다.</CommentText>
              </CommentTextWrapper>
              <CommentReplyButton>답글 쓰기</CommentReplyButton>

              <ReplyWrapper>
                <Avatar size={32} src="https://avatars.githubusercontent.com/u/98765432?v=4" alt="User Avatar" />
                <ReplyContainer>
                  <CommentTextTitleWrapper>
                    <CommentTextTitle>홍길동</CommentTextTitle>
                    <CommentTextDate>01/25 17:05</CommentTextDate>
                  </CommentTextTitleWrapper>
                  <CommentText>정말 흥미롭네요. BYD도 대단하죠.</CommentText>
                </ReplyContainer>
              </ReplyWrapper>
              <ReplyInputWrapper>
                <Input
                  type="text"
                  value=""
                  onChange={() => { }}
                  placeholder="답글을 입력하세요"
                  icon={<IoMdSend size={24} color="#ccc" onClick={() => { /* 답글 전송 로직 */ }} />}
                />
              </ReplyInputWrapper>
            </CommentContainer>
          </Comment>
        </CommentsWrapper>
      </NewsContentWrapper>

      <SideWrapper>
        <PopularNews />
        <Ranking />
      </SideWrapper>
    </Wrapper >
  )
}


export default NewsContent;