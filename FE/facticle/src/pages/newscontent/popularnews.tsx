import { PopularNewsWrapper, PopularNewsTitleWrapper, MoreButton, NewsContentWrapper, PopularNewsTitle } from "./popularnews.styles";
import { FaChevronRight } from "react-icons/fa";
import News from "../../components/news";


function PopularNews() {
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
        <News
          src="1"
          image_url="https://img.etnews.com/news/article/2025/04/21/news-p.v1.20250421.30de8a78fb4445738deff382a12f93d9_P1.png"
          title="글로벌 전기차 격전지 中 '상하이모터쇼'…신차 100대 공개"
          hs_score={80}
          fs_score={90}
          rating={4.5}
          imageHeight={200}
          titleSize={16}
        />

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

        <News
          src="1"
          image_url="https://img.etnews.com/news/article/2025/05/01/news-p.v1.20250501.3a696612b66f4aa8ab3c98096f9dbb6b_P1.png"
          title="국내 AI 시장 6兆 돌파…성장 궤도 안착"
          hs_score={90}
          fs_score={89}
          rating={4.8}
          axis="row"
          titleSize={16}
        />
      </NewsContentWrapper>
    </PopularNewsWrapper>
  );
}

export default PopularNews;