import { useState } from "react";
import { RankingBodyWrapper, RankingButton, RankingButtonWrapper, RankingNewsContainer, RankingSubTitle, RankingTitle, RankingTitleWrapper, RankingWrapper } from "./ranking.styles";
import News from "../../components/news";

function Ranking() {
  const [openSimialr, setOpenSimilar] = useState(true);
  const [openFact, setOpenFact] = useState(false);

  const handleButtonClick = () => {
    setOpenSimilar(!openSimialr);
    setOpenFact(!openFact);
  }

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
        <RankingNewsContainer>
          <h4>1</h4>
          <News
            src="1"
            image_url="https://img.etnews.com/news/article/2025/04/16/news-p.v1.20250416.38f8366f95fc4d0284464bc1703123e4_P1.jpg"
            title="현대모비스, 상하이모터쇼에 신기술 10종 공개…“中 수주 2억달러 목표”"
            hs_score={85}
            fs_score={78}
            rating={4.3}
            axis="row"
            titleSize={16}
          />
        </RankingNewsContainer>
        <RankingNewsContainer>
          <h4>2</h4>
          <News
            src="2"
            image_url="https://img.etnews.com/news/article/2025/04/16/news-p.v1.20250416.38f8366f95fc4d0284464bc1703123e4_P1.jpg"
            title="현대모비스, 상하이모터쇼에 신기술 10종 공개…“中 수주 2억달러 목표”"
            hs_score={85}
            fs_score={78}
            rating={4.3}
            axis="row"
            titleSize={16}
          />
        </RankingNewsContainer>
        <RankingNewsContainer>
          <h4>3</h4>
          <News
            src="3"
            image_url="https://img.etnews.com/news/article/2025/04/16/news-p.v1.20250416.38f8366f95fc4d0284464bc1703123e4_P1.jpg"
            title="현대모비스, 상하이모터쇼에 신기술 10종 공개…“中 수주 2억달러 목표”"
            hs_score={85}
            fs_score={78}
            rating={4.3}
            axis="row"
            titleSize={16}
          />
        </RankingNewsContainer>
        <RankingNewsContainer>
          <h4>4</h4>
          <News
            src="3"
            image_url="https://img.etnews.com/news/article/2025/04/16/news-p.v1.20250416.38f8366f95fc4d0284464bc1703123e4_P1.jpg"
            title="현대모비스, 상하이모터쇼에 신기술 10종 공개…“中 수주 2억달러 목표”"
            hs_score={85}
            fs_score={78}
            rating={4.3}
            axis="row"
            titleSize={16}
          />
        </RankingNewsContainer>
        <RankingNewsContainer>
          <h4>5</h4>
          <News
            src="3"
            image_url="https://img.etnews.com/news/article/2025/04/16/news-p.v1.20250416.38f8366f95fc4d0284464bc1703123e4_P1.jpg"
            title="현대모비스, 상하이모터쇼에 신기술 10종 공개…“中 수주 2억달러 목표”"
            hs_score={85}
            fs_score={78}
            rating={4.3}
            axis="row"
            titleSize={16}
          />
        </RankingNewsContainer>
      </RankingBodyWrapper>
    </RankingWrapper>
  );
}

export default Ranking;
