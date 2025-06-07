import { useEffect, useState } from "react";
import News from "../../components/news";
import newsService from "../../services/news/news.service";
import { CategoryNewsWrapper, CategoryText, CategoryTitle, CategoryTitleWrapper, CategoryWrapper, MoreButton, MoreNewsButton, MoreNewsTitle, MoreNewsWrapper, NewsHighlightContainer, NewsHighlightContent, NewsHighlightWrapper, PageWrapper } from "./home.styles";
import { FaChevronRight } from "react-icons/fa";

function Home() {
  const [recentNewsList, setRecentNewsList] = useState<any[]>([]);
  const [popularNewsList, setPopularNewsList] = useState<any[]>([]);
  const [similarNewsList, setSimilarNewsList] = useState<any[]>([]);
  const [entertainmentNewsList, setEntertainmentNewsList] = useState<any[]>([]);
  const [sportsNewsList, setSportsNewsList] = useState<any[]>([]);

  const fetchRecentNews = async () => {
    const response: any = await newsService.getNewsList({
      category: [],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "",
      star: 0,
      time: 0
    }, 4, 0);
    if (response.data.code === 200) {
      setRecentNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  const fetchPopularNews = async () => {
    const response: any = await newsService.getNewsList({
      category: [],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "조회수순",
      star: 0,
      time: 0
    }, 4, 0);
    if (response.data.code === 200) {
      setPopularNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  const fetchSimilarNews = async () => {
    const response: any = await newsService.getNewsList({
      category: [],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "유사도점수순",
      star: 0,
      time: 0
    }, 4, 0);
    if (response.data.code === 200) {
      setSimilarNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  const fetchEntertainmentNews = async () => {
    const response: any = await newsService.getNewsList({
      category: ["연예"],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "",
      star: 0,
      time: 0
    }, 4, 0);
    if (response.data.code === 200) {
      setEntertainmentNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  const fetchSportsNews = async () => {
    const response: any = await newsService.getNewsList({
      category: ["스포츠"],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "",
      star: 0,
      time: 0
    }, 4, 0);
    if (response.data.code === 200) {
      setSportsNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  useEffect(() => {
    fetchRecentNews();
    fetchPopularNews();
    fetchSimilarNews();
    fetchEntertainmentNews();
    fetchSportsNews();
  }, []);

  return (
    <PageWrapper>
      <CategoryWrapper>
        <CategoryTitleWrapper>
          <CategoryTitle>
            <h2>최신 뉴스 보기</h2>
            <CategoryText>뉴스 제목과 내용의 유사도를 분석해 신뢰할 수 있는 뉴스를 제공합니다.</CategoryText>
          </CategoryTitle>
          <MoreButton to="/news">
            더보기
            <FaChevronRight />
          </MoreButton>
        </CategoryTitleWrapper>

        <CategoryNewsWrapper>
          {recentNewsList.map((news, index) => (
            <News
              key={index}
              src={String(news.newsId)}
              image_url={news.imageUrl}
              title={news.title}
              content={news.summary}
              hs_score={news.headlineScore}
              fs_score={news.factScore}
              rating={news.rating}
              time={news.collectedAt}
            />
          ))}
        </CategoryNewsWrapper>
      </CategoryWrapper>

      <NewsHighlightWrapper>
        <NewsHighlightContainer>
          <CategoryTitleWrapper>
            <CategoryTitle>
              <h2>인기 뉴스</h2>
            </CategoryTitle>
            <MoreButton to="/news" state={{ sort: "조회수순" }}>
              더보기
              <FaChevronRight />
            </MoreButton>
          </CategoryTitleWrapper>

          <NewsHighlightContent>
            <div style={{ display: "flex", flexDirection: "column", gap: "20px", width: "100%", height: "100%" }}>
              {popularNewsList.map((news, index) => (
                index === 0 ? (
                  <News
                    key={index}
                    src={String(news.newsId)}
                    image_url={news.imageUrl}
                    title={news.title}
                    content={news.summary}
                    hs_score={news.headlineScore}
                    fs_score={news.factScore}
                    rating={news.rating}
                    time={news.collectedAt}
                  />
                ) : (
                  <News
                    key={index}
                    src={String(news.newsId)}
                    image_url={news.imageUrl}
                    title={news.title}
                    content={news.summary}
                    hs_score={news.headlineScore}
                    fs_score={news.factScore}
                    axis='row'
                    rating={news.rating}
                    time={news.collectedAt}
                  />
                )
              ))}
            </div>
          </NewsHighlightContent>
        </NewsHighlightContainer>

        <NewsHighlightContainer>
          <CategoryTitleWrapper>
            <CategoryTitle>
              <h2>유사도 높은 뉴스</h2>
            </CategoryTitle>
            <MoreButton to="/news" state={{ sort: "유사도점수순" }}>
              더보기
              <FaChevronRight />
            </MoreButton>
          </CategoryTitleWrapper>

          <NewsHighlightContent>
            <div style={{ display: "flex", flexDirection: "column", gap: "20px", width: "100%", height: "100%" }}>
              {similarNewsList.map((news, index) => (
                index === 0 ? (
                  <News
                    key={index}
                    src={String(news.newsId)}
                    image_url={news.imageUrl}
                    title={news.title}
                    content={news.summary}
                    hs_score={news.headlineScore}
                    fs_score={news.factScore}
                    rating={news.rating}
                    time={news.collectedAt}
                  />
                ) : (
                  <News
                    key={index}
                    src={String(news.newsId)}
                    image_url={news.imageUrl}
                    title={news.title}
                    content={news.summary}
                    hs_score={news.headlineScore}
                    fs_score={news.factScore}
                    axis='row'
                    rating={news.rating}
                    time={news.collectedAt}
                  />
                )
              ))}
            </div>
          </NewsHighlightContent>
        </NewsHighlightContainer>
      </NewsHighlightWrapper>

      <CategoryWrapper>
        <CategoryTitleWrapper>
          <CategoryTitle>
            <h2>연예</h2>
          </CategoryTitle>
          <MoreButton to="/news/entertainment" state={{ category: ['연예'] }}>
            더보기
            <FaChevronRight />
          </MoreButton>
        </CategoryTitleWrapper>

        <CategoryNewsWrapper>
          {entertainmentNewsList.map((news, index) => (
              <News
                key={index}
                src={String(news.newsId)}
                image_url={news.imageUrl}
                title={news.title}
                content={news.summary}
                hs_score={news.headlineScore}
                fs_score={news.factScore}
                rating={news.rating}
                time={news.collectedAt}
              />
            ))
          }
        </CategoryNewsWrapper>
      </CategoryWrapper>

      <CategoryWrapper>
        <CategoryTitleWrapper>
          <CategoryTitle>
            <h2>스포츠</h2>
          </CategoryTitle>
          <MoreButton to="/news/sports" state={{ category: ['스포츠'] }}>
            더보기
            <FaChevronRight />
          </MoreButton>
        </CategoryTitleWrapper>

        <CategoryNewsWrapper>
          {sportsNewsList.map((news, index) => (
              <News
                key={index}
                src={String(news.newsId)}
                image_url={news.imageUrl}
                title={news.title}
                content={news.summary}
                hs_score={news.headlineScore}
                fs_score={news.factScore}
                rating={news.rating}
                time={news.collectedAt}
              />
            ))
          }
        </CategoryNewsWrapper>
      </CategoryWrapper>

      <MoreNewsWrapper>
        <MoreNewsTitle>더 많은 뉴스 보러가기</MoreNewsTitle>
        <MoreNewsButton to="/news">더보기</MoreNewsButton>
      </MoreNewsWrapper>
    </PageWrapper>
  );
}

export default Home;