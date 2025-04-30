import { useLocation } from "react-router-dom";
import { CategoryButton, CategoryLine, CategoryTitle, CategoryWrapper, ContentWrapper, ControlsContainer, ControlsWrapper, NewsContainer, OptionButton, PageWrapper, SearchButton } from "./newspage.styles";
import { LuFilter } from "react-icons/lu";
import { MdSort } from "react-icons/md";
import { IoSearchOutline } from "react-icons/io5";
import Input from "../../components/input";
import { useState } from "react";
import Filter from "./filter";
import News from "../../components/news";

//["Politics", "Economy", "Society", "International", "tech", "Culture", "Entertainment", "Sports", "Weather"]
function NewsPage() {
  const location = useLocation();
  const [openFilter, setOpenFilter] = useState(false);
  const [openSort, setOpenSort] = useState(false);

  const handleFilter = () => {
    setOpenFilter(!openFilter);
    setOpenSort(!openSort);
  }

  return (
    <PageWrapper>
      <CategoryWrapper>
        <CategoryTitle>분야별</CategoryTitle>
        <CategoryButton
          to="/news"
          isActive={location.pathname === '/news'}
        >전체
        </CategoryButton>
        <CategoryButton
          to="/news/politics"
          isActive={location.pathname === '/news/politics'}
        >정치
        </CategoryButton>
        <CategoryButton
          to="/news/economy"
          isActive={location.pathname === '/news/economy'}
        >경제
        </CategoryButton>
        <CategoryButton
          to="/news/society"
          isActive={location.pathname === '/news/society'}
        >사회
        </CategoryButton>
        <CategoryButton
          to="/news/international"
          isActive={location.pathname === '/news/international'}
        >국제
        </CategoryButton>
        <CategoryButton
          to="/news/tech"
          isActive={location.pathname === '/news/tech'}
        >기술
        </CategoryButton>
        <CategoryButton
          to="/news/culture"
          isActive={location.pathname === '/news/culture'}
        >문화
        </CategoryButton>
        <CategoryButton
          to="/news/entertainment"
          isActive={location.pathname === '/news/entertainment'}
        >엔터
        </CategoryButton>
        <CategoryButton
          to="/news/sports"
          isActive={location.pathname === '/news/sports'}
        >스포츠
        </CategoryButton>
        <CategoryButton
          to="/news/weather"
          isActive={location.pathname === '/news/weather'}
        >날씨
        </CategoryButton>
      </CategoryWrapper>

      <CategoryLine />

      <ControlsWrapper>
        <ControlsContainer>
          <OptionButton
            onClick={handleFilter}
            isActive={openFilter}
          >필터
            <LuFilter size={'16px'} />
          </OptionButton>
          <OptionButton
            onClick={handleFilter}
            isActive={openSort}
          >정렬
            <MdSort size={'16px'} />
          </OptionButton>
          <SearchButton> {/* 해당 버튼으로 검색 api 요청*/}
            <IoSearchOutline size={'20px'} color="white"/>
          </SearchButton>
          <Input
            type="text"
            placeholder="검색어를 입력하세요"
            value=""
            onChange={() => { }}
          />
          
        </ControlsContainer>

        {openFilter && (
          <Filter></Filter>
        )}

      </ControlsWrapper>

      <ContentWrapper>
        <NewsContainer>
          <News
            src="1"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={80}
            fs_score={90}
            rating={4.7}
          />
          <News
            src="2"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={20}
            fs_score={30}
            rating={4.0}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={70}
            fs_score={80}
            rating={4.5}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={100}
            fs_score={100}
            rating={5.0}
          />
        </NewsContainer>
        <NewsContainer>
          <News
            src="1"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={80}
            fs_score={90}
            rating={4.7}
          />
          <News
            src="2"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={20}
            fs_score={30}
            rating={4.0}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={70}
            fs_score={80}
            rating={4.5}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={100}
            fs_score={100}
            rating={5.0}
          />
        </NewsContainer>
        <NewsContainer>
          <News
            src="1"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={80}
            fs_score={90}
            rating={4.7}
          />
          <News
            src="2"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={20}
            fs_score={30}
            rating={4.0}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
            hs_score={70}
            fs_score={80}
            rating={4.5}
          />
          <News
            src="3"
            image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
            title="코로나19 신규 확진 1,500명대…사망자 40명"
            content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
            hs_score={100}
            fs_score={100}
            rating={5.0}
          />
        </NewsContainer>
      </ContentWrapper>
    </PageWrapper>
  );
}

export default NewsPage;