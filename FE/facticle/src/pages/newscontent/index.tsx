import { useEffect, useState } from "react";
import { 
  InfoLine, NewsContentBody, NewsContentBodySubText, NewsContentBodyText, 
  NewsContentBodyTextWrapper, ConfiremButton, NewsContentDate, NewsContentHeader, 
  NewsContentImg, NewsContentInfo, NewsContentLine, ReviewWrapper, StarWrapper, 
  NewsContentTitle, NewsContentWrapper, ScoreContainer, ScoreText, ScoreWrapper, 
  SideWrapper, StarContainer, StarText, Wrapper, OriginalButton, ReasonContainer, 
  ReasonWrapper, ReasonTitle, ReasonText, ReviewTitle, DeleteRatingButton,
  StarClickArea, StarHalfClickArea, RatingButtonWrapper, LoginWarningText
} from "./newscontent.styles";
import Progress from "../../components/progress";
import PopularNews from "./popularnews";
import { FaStar, FaRegStarHalfStroke, FaRegStar } from "react-icons/fa6";
import { GiCheckMark } from "react-icons/gi";
import Ranking from "./ranking";
import { useParams, useNavigate } from "react-router-dom";
import newsService from "../../services/news/news.service";
import { useAuth } from "../../hooks/useAuth";
import NewsComment from "./comment";
import Dialog from "../../components/dialog";

interface NewsContentType {
  category: string;
  collectedAt: string;
  commentCount: number;
  factScore: number;
  factScoreReason: string;
  hateCount: number;
  headlineScore: number;
  headlineScoreReason: string;
  imageUrl: string;
  likeCount: number;
  mediaName: string;
  naverUrl: string;
  newsId: number;
  rating: number;
  ratingCount: number;
  summary: string;
  title: string;
  url: string;
  viewCount: number;
}

interface NewsInteraction {
  newsInteractionId: number;
  userId: number;
  newsId: number;
  reaction: string | null;
  rating: number | null;
  reactionAt: string | null;
  ratedAt: string | null;
  viewedAt: string;
}

interface NewsApiResponse {
  success: boolean;
  data: {
    news: NewsContentType;
    code: number;
    isUser: boolean;
    newsInteraction: NewsInteraction | null;
  };
  message: string;
}

function NewsContent() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [content, setContent] = useState<NewsContentType | null>(null);
  const [userRating, setUserRating] = useState(0);
  const [hasUserRated, setHasUserRated] = useState(false);
  const [showLoginDialog, setShowLoginDialog] = useState(false);
  const [showRatingSuccessDialog, setShowRatingSuccessDialog] = useState(false);
  const [showDeleteRatingDialog, setShowDeleteRatingDialog] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleStarClick = (value: number) => {
    if (!isAuthenticated) {
      setShowLoginDialog(true);
      return;
    }
    setUserRating(value);
  };

  const renderStar = (index: number) => {
    if (userRating >= index) return <FaStar size={60} color="#524DD6" />;
    if (userRating >= index - 0.5) return <FaRegStarHalfStroke size={60} color="#524DD6" />;
    return <FaRegStar size={60} color="#ccc" />;
  };

  const formatDate = (isoString: string) => {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
  }

  const fetchNewsContent = async (newsId: string) => {
    try {
      const response = await newsService.getNews(newsId) as NewsApiResponse;
      if (response.data.code === 200) {
        setContent(response.data.news);
        
        // newsInteraction에서 사용자 평점 정보 추출
        if (response.data.newsInteraction && response.data.newsInteraction.rating) {
          setUserRating(response.data.newsInteraction.rating);
          setHasUserRated(true);
        } else {
          // 평점이 없는 경우 초기화
          setUserRating(0);
          setHasUserRated(false);
        }
      }
    } catch (error) {
      // 에러 처리
      setContent(null);
      setUserRating(0);
      setHasUserRated(false);
    }
  }

  // 평점 제출/수정
  const handleSubmitRating = async () => {
    if (!isAuthenticated) {
      setShowLoginDialog(true);
      return;
    }

    if (userRating === 0) {
      alert('별점을 선택해주세요.');
      return;
    }

    // 만약 수정일때 평점이 이전과 같다면 아무것도 하지 않음
    if (hasUserRated && userRating === content?.rating) {
      return;
    }

    if (!id) return;

    try {
      setLoading(true);
      await newsService.postAndUpdateNewsRating(id, userRating);
      setHasUserRated(true);
      setShowRatingSuccessDialog(true);
      // 현재 뉴스 정보 다시 가져와서 평균 평점 업데이트
      await fetchNewsContent(id);
    } catch (error) {
      alert('평점 등록에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setLoading(false);
    }
  };

  // 평점 삭제 다이얼로그 열기
  const openDeleteRatingDialog = () => {
    setShowDeleteRatingDialog(true);
  };

  // 평점 삭제 다이얼로그 닫기
  const closeDeleteRatingDialog = () => {
    setShowDeleteRatingDialog(false);
  };

  // 평점 삭제 실행
  const confirmDeleteRating = async () => {
    if (!id) return;

    try {
      setLoading(true);
      await newsService.deleteNewsRating(id);
      setUserRating(0);
      setHasUserRated(false);
      closeDeleteRatingDialog();
      // 현재 뉴스 정보 다시 가져와서 평균 평점 업데이트
      await fetchNewsContent(id);
    } catch (error) {
      alert('평점 삭제에 실패했습니다. 다시 시도해주세요.');
      closeDeleteRatingDialog();
    } finally {
      setLoading(false);
    }
  };

  const handleLoginConfirm = () => {
    setShowLoginDialog(false);
    navigate('/login');
  };

  const handleLoginDialogClose = () => {
    setShowLoginDialog(false);
  };

  const handleRatingSuccessClose = () => {
    setShowRatingSuccessDialog(false);
  };

  // 뉴스 ID가 변경될 때마다 해당 뉴스의 정보와 평점을 가져옴
  useEffect(() => {
    if (id) {
      // 이전 뉴스의 평점 상태 초기화
      setUserRating(0);
      setHasUserRated(false);
      setShowLoginDialog(false);
      setShowRatingSuccessDialog(false);
      setShowDeleteRatingDialog(false);
      
      // 새로운 뉴스 정보 가져오기
      fetchNewsContent(id);
    }
  }, [id]); // id가 변경될 때마다 실행

  return (
    <Wrapper>
      <NewsContentWrapper>
        <NewsContentHeader>
          <NewsContentTitle>{content?.title}</NewsContentTitle>
          <NewsContentInfo>
            <NewsContentDate>{formatDate(content?.collectedAt ?? '')}</NewsContentDate>

            <InfoLine />

            <ScoreWrapper>
              <ScoreContainer>
                <ScoreText>유사도</ScoreText>
                <Progress progress={Math.round(content?.headlineScore ?? 0) ?? 0} color="black" />
                <ScoreText>{Math.round(content?.headlineScore ?? 0)}%</ScoreText>
              </ScoreContainer>
              <InfoLine />
              <ScoreContainer>
                <ScoreText>신뢰도</ScoreText>
                <Progress progress={Math.round(content?.factScore ?? 0) ?? 0} color="black" />
                <ScoreText>{Math.round(content?.factScore ?? 0)}%</ScoreText>
              </ScoreContainer>
            </ScoreWrapper>
          </NewsContentInfo>
          <NewsContentInfo>
            <StarContainer>
              <FaStar style={{ color: "#524DD6" }} />
              <StarText>
                {content?.rating ? content.rating.toFixed(1) : '0.0'} 
                ({content?.ratingCount || 0}명 평가)
              </StarText>
            </StarContainer>
            <OriginalButton
              to={content?.url ?? ''}
              target="_blank">
              원문 보기
            </OriginalButton>
          </NewsContentInfo>
        </NewsContentHeader>

        <NewsContentBody>
          <NewsContentImg src={content?.imageUrl} alt="news" />
          <NewsContentBodyTextWrapper>
            <h2>🤖 핵심 요약</h2>
            <NewsContentBodySubText>(AI가 기사 본문을 분석해 핵심 내용을 추출하여 요약해드립니다.)</NewsContentBodySubText>
          </NewsContentBodyTextWrapper>
          <NewsContentBodyText>{content?.summary}</NewsContentBodyText>

          <ReasonWrapper>
            <ReasonContainer>
              <ReasonTitle>📝 유사도 분석 근거 ({Math.round(content?.headlineScore ?? 0)}%)</ReasonTitle>
              <ReasonText>{content?.headlineScoreReason}</ReasonText>
            </ReasonContainer>
            <ReasonContainer>
              <ReasonTitle>📝 신뢰도 분석 근거 ({Math.round(content?.factScore ?? 0)}%)</ReasonTitle>
              <ReasonText>{content?.factScoreReason}</ReasonText>
            </ReasonContainer>
          </ReasonWrapper>

          <NewsContentLine />

          <ReviewWrapper>
            <ReviewTitle>
              {hasUserRated 
                ? `내 평점: ${userRating}점` 
                : '이 기사의 별점을 매기세요!'
              }
              {hasUserRated && isAuthenticated && (
                <DeleteRatingButton
                  onClick={openDeleteRatingDialog}
                  disabled={loading}
                  $loading={loading}
                >
                  ✕ 평점 삭제
                </DeleteRatingButton>
              )}
            </ReviewTitle>
            
            <StarWrapper>
              {[1, 2, 3, 4, 5].map((star) => (
                <StarClickArea key={star} $isAuthenticated={isAuthenticated}>
                  {/* 왼쪽 반 클릭: 0.5점 */}
                  <StarHalfClickArea 
                    $isAuthenticated={isAuthenticated} 
                    $isLeft={true}
                    onClick={() => handleStarClick(star - 0.5)}
                  />
                  {/* 오른쪽 반 클릭: 1점 */}
                  <StarHalfClickArea 
                    $isAuthenticated={isAuthenticated} 
                    $isLeft={false}
                    onClick={() => handleStarClick(star)}
                  />
                  {renderStar(star)}
                </StarClickArea>
              ))}
            </StarWrapper>
            
            <RatingButtonWrapper>
              <ConfiremButton 
                onClick={handleSubmitRating}
                disabled={loading || userRating === 0 || !isAuthenticated}
                $disabled={loading || userRating === 0 || !isAuthenticated}
              >
                {loading ? '처리중...' : hasUserRated ? '평점 수정' : '평점 등록'} 
                <GiCheckMark style={{ color: '#009045' }} size={16} />
              </ConfiremButton>
            </RatingButtonWrapper>
            
            {!isAuthenticated && (
              <LoginWarningText>
                ⚠️ 평점을 매기려면 로그인이 필요합니다.
              </LoginWarningText>
            )}
          </ReviewWrapper>

          <NewsContentLine />
        </NewsContentBody>
        
        <NewsComment 
          newsId={id || ''}
        />
      </NewsContentWrapper>

      <SideWrapper>
        <PopularNews />
        <Ranking />
      </SideWrapper>

      {/* 로그인 필요 다이얼로그 */}
      <Dialog
        open={showLoginDialog}
        title="로그인 필요"
        content={
          <div>
            <p>평점을 매기려면 로그인이 필요합니다.</p>
            <p>로그인 페이지로 이동하시겠습니까?</p>
          </div>
        }
        onClose={handleLoginDialogClose}
        onConfirm={handleLoginConfirm}
        confirmText="로그인하러 가기"
        cancelText="취소"
      />

      {/* 평점 등록 성공 다이얼로그 */}
      <Dialog
        open={showRatingSuccessDialog}
        title="평점 등록 완료"
        content={
          <div>
            <p>평점이 성공적으로 등록되었습니다!</p>
            <p>소중한 의견 감사합니다. 🌟</p>
          </div>
        }
        onClose={handleRatingSuccessClose}
        confirmText="확인"
      />

      {/* 평점 삭제 확인 다이얼로그 */}
      <Dialog
        open={showDeleteRatingDialog}
        title="평점 삭제"
        content={
          <div>
            <p>정말로 평점을 삭제하시겠습니까?</p>
            <p style={{ color: '#666', fontSize: '14px' }}>삭제된 평점은 복구할 수 없습니다.</p>
          </div>
        }
        onClose={closeDeleteRatingDialog}
        onConfirm={confirmDeleteRating}
        confirmText="삭제"
        cancelText="취소"
      />
    </Wrapper>
  )
}

export default NewsContent;