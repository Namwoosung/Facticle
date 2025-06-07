import { useLocation } from "react-router-dom";
import { CategoryButton, CategoryLine, CategoryTitle, CategoryWrapper, ContentWrapper, ControlsContainer, ControlsWrapper, OptionButton, PageWrapper, SearchButton } from "./newspage.styles";
import { LuFilter } from "react-icons/lu";
import { MdSort } from "react-icons/md";
import { IoSearchOutline } from "react-icons/io5";
import Input from "../../components/input";
import { useEffect, useState, useRef, useCallback } from "react";
import Filter from "./filter";
import News from "../../components/news";
import newsService from "../../services/news/news.service";

//["Politics", "Economy", "Society", "International", "tech", "Culture", "Entertainment", "Sports", "Weather"]
function NewsPage() {
  const [page, setPage] = useState(0);          // 현재 페이지
  const [hasMore, setHasMore] = useState(true); // 더 가져올 뉴스가 있는지
  const [loading, setLoading] = useState(false); // 중복 요청 방지

  const location = useLocation();
  const state = location.state as any; // 카테고리 필터링을 위한 state
  const [keyword, setKeyword] = useState('');
  const [filters, setFilters] = useState({
    category: [],
    factScore: 0,
    keyword: "",
    similarity: 0,
    sort: "",
    star: 0,
    time: 0
  });
  type NewsItem = {
    newsId: string;
    imageUrl: string;
    title: string;
    summary: string;
    headlineScore: number;
    factScore: number;
    rating: number;
    collectedAt: string;
  };
  const [newsList, setNewsList] = useState<NewsItem[]>([]);

  const [openFilter, setOpenFilter] = useState(false);
  const [openSort, setOpenSort] = useState(false);

  const handleFilter = () => {
    setOpenFilter(!openFilter);
    setOpenSort(!openSort);
  }

  const observer = useRef<IntersectionObserver | null>(null); // 마지막 뉴스 아이템을 감지하기 위한 IntersectionObserver
  const lastNewsRef = useCallback((node: HTMLDivElement | null) => { // 마지막 뉴스 아이템을 감지하는 ref
    if (loading) return;
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore) {
        fetchNews(filters, true); // 다음 페이지 불러오기
      }
    });

    if (node) observer.current.observe(node);
  }, [loading, hasMore, filters, keyword]);

  const fetchNews = async (filterValues: any, isNextPage = false) => {
    if (loading) return;
    setLoading(true);

    const currentPage = isNextPage ? page + 1 : 0;
    const updatedFilterValues = { ...filterValues, keyword };

    try {
      const response: any = await newsService.getNewsList(updatedFilterValues, 12, currentPage);
      if (response.data.code === 200) {
        const newList = response.data.newsList;

        if (isNextPage) {
          setNewsList(prev => [...prev, ...newList]);
          setPage(currentPage);
        } else {
          setNewsList(newList);
          setPage(0);
        }

        setHasMore(response.data.totalCount === 12); // 12개 미만이면 마지막 페이지
      }
    } catch (error) {
      // observer 해제
      if (observer.current) observer.current.disconnect();
      setHasMore(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (state) {
      const updatedFilters = { ...filters, ...state };
      setFilters(updatedFilters);
      fetchNews(updatedFilters, false); // 초기화
    } else {
      fetchNews(filters, false);
    }
  }, [state]);

  return (
    <PageWrapper>
      <CategoryWrapper>
        <CategoryTitle>분야별</CategoryTitle>
        <CategoryButton
          to="/news"
          isActive={location.pathname === '/news'}
          state={{ category: [] }}
        >전체
        </CategoryButton>
        <CategoryButton
          to="/news/politics"
          isActive={location.pathname === '/news/politics'}
          state={{ category: ['정치'] }}
        >정치
        </CategoryButton>
        <CategoryButton
          to="/news/economy"
          isActive={location.pathname === '/news/economy'}
          state={{ category: ['경제'] }}
        >경제
        </CategoryButton>
        <CategoryButton
          to="/news/society"
          isActive={location.pathname === '/news/society'}
          state={{ category: ['사회'] }}
        >사회
        </CategoryButton>
        <CategoryButton
          to="/news/international"
          isActive={location.pathname === '/news/international'}
          state={{ category: ['국제'] }}
        >국제
        </CategoryButton>
        <CategoryButton
          to="/news/tech"
          isActive={location.pathname === '/news/tech'}
          state={{ category: ['기술'] }}
        >기술
        </CategoryButton>
        <CategoryButton
          to="/news/culture"
          isActive={location.pathname === '/news/culture'}
          state={{ category: ['문화'] }}
        >문화
        </CategoryButton>
        <CategoryButton
          to="/news/entertainment"
          isActive={location.pathname === '/news/entertainment'}
          state={{ category: ['연예'] }}
        >연예
        </CategoryButton>
        <CategoryButton
          to="/news/sports"
          isActive={location.pathname === '/news/sports'}
          state={{ category: ['스포츠'] }}
        >스포츠
        </CategoryButton>
        <CategoryButton
          to="/news/weather"
          isActive={location.pathname === '/news/weather'}
          state={{ category: ['날씨'] }}
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
          <SearchButton onClick={() => { fetchNews(filters) }}>
            <IoSearchOutline size={'20px'} color="white" />
          </SearchButton>
          <Input
            type="text"
            placeholder="검색어를 입력하세요"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
              if (e.key === 'Enter') {
                fetchNews(filters);
              }
            }}
          />

        </ControlsContainer>

        {openFilter && (
          <Filter onFilterChange={setFilters} />
        )}

      </ControlsWrapper>

      <ContentWrapper>
        {newsList.map((news: any, index: number) => {
          const isLast = index === newsList.length - 1;
          return (
            <div key={news.newsId} ref={isLast ? lastNewsRef : undefined}>
              <News
                src={String(news.newsId)}
                image_url={news.imageUrl}
                title={news.title}
                content={news.summary}
                hs_score={news.headlineScore}
                fs_score={news.factScore}
                rating={news.rating}
                time={news.collectedAt}
              />
            </div>
          );
        })}
      </ContentWrapper>
    </PageWrapper>
  );
}

export default NewsPage;