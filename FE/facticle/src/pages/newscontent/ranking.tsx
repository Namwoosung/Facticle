import { useEffect, useState } from "react";
import { RankingBodyWrapper, RankingButton, RankingButtonWrapper, RankingNewsContainer, RankingSubTitle, RankingTitle, RankingTitleWrapper, RankingWrapper } from "./ranking.styles";
import News from "../../components/news";
import newsService from "../../services/news/news.service";

function Ranking() {
  const [content, setContent] = useState<any[]>([]);
  const [similarityList, setSimilarityList] = useState<any[]>([]);
  const [factList, setFactList] = useState<any[]>([]);

  const [openSimialr, setOpenSimilar] = useState(true);
  const [openFact, setOpenFact] = useState(false);

  const handleButtonClick = () => {
    setOpenSimilar(!openSimialr);
    setOpenFact(!openFact);
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
    }, 5, 0);
    if (response.data.code === 200) {
      setSimilarityList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  const fetchFactNews = async () => {
    const response: any = await newsService.getNewsList({
      category: [],
      factScore: 0,
      keyword: "",
      similarity: 0,
      sort: "팩트점수순",
      star: 0,
      time: 0
    }, 5, 0);
    if (response.data.code === 200) {
      setFactList(response.data.newsList);
    } else {
      // Handle error
    }
  }

  useEffect(() => {
    fetchSimilarNews();
    fetchFactNews();
  }, []);

  useEffect(() => {
    if (openSimialr) {
      setContent(similarityList);
    }
    if (openFact) {
      setContent(factList);
    }
  }, [openSimialr, openFact, similarityList, factList]);



  return (
    <RankingWrapper>
      <RankingTitleWrapper>
        <RankingTitle>랭킹 뉴스</RankingTitle>
        <RankingSubTitle>랭킹 뉴스는 1시간마다 업데이트 됩니다.</RankingSubTitle>
      </RankingTitleWrapper>

      <RankingBodyWrapper>
        <RankingButtonWrapper>
          <RankingButton open={openSimialr} onClick={handleButtonClick}>유사도 순</RankingButton>
          <RankingButton open={openFact} onClick={handleButtonClick}>신뢰도 순</RankingButton>
        </RankingButtonWrapper>
        {content.map((news, index) => (
          <RankingNewsContainer key={index}>
            <News
              src={news.newsId}
              image_url={news.imageUrl}
              title={news.title}
              content={news.summary}
              hs_score={news.headlineScore}
              fs_score={news.factScore}
              rating={news.rating}
              axis="row"
              titleSize={16}
              time={news.collectedAt}
            />
          </RankingNewsContainer>
        ))}
      </RankingBodyWrapper>
    </RankingWrapper>
  );
}

export default Ranking;