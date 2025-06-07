import { PopularNewsWrapper, PopularNewsTitleWrapper, MoreButton, NewsContentWrapper, PopularNewsTitle } from "./popularnews.styles";
import { FaChevronRight } from "react-icons/fa";
import News from "../../components/news";
import newsService from "../../services/news/news.service";
import { useEffect, useState } from "react";


function PopularNews() {
  const [popularNewsList, setPopularNewsList] = useState<any[]>([]);
  const fetchPopularNews = async () => {
    const response: any = await newsService.getNewsList({
      category: [],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "조회수순",
      star: 0,
      time: 0
    }, 3, 0);
    if (response.data.code === 200) {
      setPopularNewsList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  useEffect(() => {
    fetchPopularNews();
  }, []);

  return (
    <PopularNewsWrapper>
      <PopularNewsTitleWrapper>
        <PopularNewsTitle>인기 뉴스</PopularNewsTitle>
        <MoreButton to="/news">
          더보기
          <FaChevronRight />
        </MoreButton>
      </PopularNewsTitleWrapper>

      <NewsContentWrapper>
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
              imageHeight={200}
              titleSize={16}
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
              titleSize={16}
            />
          )
        ))}
      </NewsContentWrapper>
    </PopularNewsWrapper>
  );
}

export default PopularNews;