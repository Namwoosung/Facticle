import { useState } from "react";
import { ControlsContainer } from "./newspage.styles";
import { DialogText, FilterAllWrapper, FilterButton, FilterDialogButton, FilterLine, FilterResetButton, FilterWrapper, StarContainer } from "./filter.styles";
import FilterDialog from "../../components/filterdialog";
import { MdChevronRight } from "react-icons/md";
import { GrPowerReset } from "react-icons/gr";
import { FaCheck } from "react-icons/fa6";
import { FaStar } from "react-icons/fa";
import Slider from "../../components/slider";

function Filter() {
  interface Filters {
    category: boolean;
    time: boolean;
    similarity: boolean;
    factScore: boolean;
    star: boolean;
    sort: boolean;
  }

  const [filters, setFilters] = useState<Filters>({
    category: false,
    time: false,
    similarity: false,
    factScore: false,
    star: false,
    sort: false,
  });
  const [filtersDialog, setFiltersDialog] = useState<Filters>({
    category: false,
    time: false,
    similarity: false,
    factScore: false,
    star: false,
    sort: false,
  });
  const [categoryFilters, setCategoryFilters] = useState<string[]>([]); // 선택된 카테고리 필터
  const [timeFilters, setTimeFilters] = useState<number>(0); // 선택된 시간 필터 (기본값은 0)
  const [similarityFilters, setSimilarityFilters] = useState<number>(0); // 선택된 유사도 필터 (기본값은 0)
  const [factScoreFilters, setFactScoreFilters] = useState<number>(0); // 선택된 팩트 점수 필터 (기본값은 0)
  const [starFilters, setStarFilters] = useState<number>(0); // 선택된 별점 필터 (기본값은 0)
  const [sortFilters, setSortFilters] = useState<string>(""); // 선택된 정렬 필터 (기본값은 "" 빈 문자열)

  const handleFilters = (filter: keyof Filters) => {
    setFilters((prevFilters: Filters) => {
      // 모든 필터를 false로 초기화하고 클릭한 필터만 true로 설정
      const newFilters: Filters = Object.keys(prevFilters).reduce((acc, key) => {
        if (categoryFilters.length > 0 && key === "category") { // 카테고리 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (timeFilters !== 0 && key === "time") { // 시간 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (similarityFilters !== 0 && key === "similarity") { // 유사도 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (factScoreFilters !== 0 && key === "factScore") { // 팩트 점수 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (starFilters !== 0 && key === "star") { // 별점 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (sortFilters !== "" && key === "sort") { // 정렬 필터가 선택된 경우
          acc[key as keyof Filters] = true;
        }
        else if (key === filter) {
          acc[key as keyof Filters] = !prevFilters[filter];
        }
        else {
          acc[key as keyof Filters] = key === filter ? !prevFilters[filter] : false;
        }
        return acc;
      }, {} as Filters);
      return newFilters;
    });

    setFiltersDialog((prevFilters: Filters) => {
      // 모든 필터 다이얼로그를 false로 초기화하고 클릭한 필터만 true로 설정
      const newFilters: Filters = Object.keys(prevFilters).reduce((acc, key) => {
        acc[key as keyof Filters] = key === filter ? !prevFilters[filter] : false;
        return acc;
      }, {} as Filters);
      return newFilters;
    });
  };

  const timeFormatter = (time: number) => {
    if (time === 0) {
      return "1일 전";
    } else if (time < 0) {
      return `${Math.abs(time) + 1}일 전`;
    } else if (time === 24) {
      return "현재";
    } else {
      return `${24 - time}시간 전`;
    }
  }

  const handleCategoryChange = (category: string) => {
    const ALL_CATEGORIES = ["정치", "경제", "사회", "국제", "기술", "문화", "연예", "스포츠", "날씨"];

    setCategoryFilters((prev) => {
      if (category === "전체") {
        return [];  // 전체 클릭 시 모두 해제
      }

      const newFilters = prev.includes(category)
        ? prev.filter((c) => c !== category)  // 이미 선택된 항목은 제거
        : [...prev, category];  // 새 항목 추가

      // 모든 항목이 선택됐으면 전체로 간주해 전부 해제
      const allSelected = ALL_CATEGORIES.every(c => newFilters.includes(c));
      return allSelected ? [] : newFilters;
    });
  };

  const handleTimeChange = (newValue: string) => {
    if (newValue === '현재') {
      setTimeFilters(24);  // '현재'일 때는 24로 설정
    } else if (newValue.includes('일 전')) {
      const days = parseInt(newValue.split('일')[0]);
      setTimeFilters(-(days - 1));  // '1일 전'은 -2로 설정, '0일 전'은 -1로 설정
    } else if (newValue.includes('시간 전')) {
      const hours = parseInt(newValue.split('시간')[0]);
      setTimeFilters(24 - hours);  // '0시간 전'은 24로 설정, '1시간 전'은 23으로 설정
    } else {
      setTimeFilters(0);  // 기본값 설정
    }
  };

  const handleSimilarityChange = (newValue: number) => {
    // 문자빼고 숫자만
    const score = parseInt(newValue.toString().replace(/[^0-9]/g, ''));
    setSimilarityFilters(score);  // 유사도 점수 설정
  }

  const handleFactScoreChange = (newValue: string) => {
    // 문자빼고 숫자만
    const score = parseInt(newValue.toString().replace(/[^0-9]/g, ''));
    setFactScoreFilters(score);  // 팩트 점수 설정
  }

  const handleStarChange = (newValue: string) => {
    // 문자빼고 숫자만
    const score = parseInt(newValue.toString().replace(/[^0-9]/g, ''));
    setStarFilters(score);  // 별점 설정
  }

  const handleSortChange = (newValue: string) => {
    setSortFilters((prev) => (prev === newValue ? '' : newValue));
  }

  const handleResetFilters = () => {
    setCategoryFilters([]);  // 카테고리 필터 초기화
    setTimeFilters(0);  // 시간 필터 초기화
    setSimilarityFilters(0);  // 유사도 점수 필터 초기화
    setFactScoreFilters(0);  // 팩트 점수 필터 초기화
    setStarFilters(0);  // 별점 필터 초기화

    setFilters({
      category: false,
      time: false,
      similarity: false,
      factScore: false,
      star: false,
      sort: false,
    });
    setFiltersDialog({
      category: false,
      time: false,
      similarity: false,
      factScore: false,
      star: false,
      sort: false,
    });
  };


  return (
    <FilterAllWrapper>
      <FilterWrapper>
        <FilterButton open={filters.category} onClick={() => handleFilters("category")}>
          분야
          {categoryFilters.length > 0 && `(${categoryFilters.length})`}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.category} onClose={() => handleFilters("category")}>
          {["전체", "정치", "경제", "사회", "국제", "기술", "문화", "연예", "스포츠", "날씨"].map((item) => (
            <FilterDialogButton
              key={item}
              selected={categoryFilters.includes(item)}
              onClick={() => handleCategoryChange(item)}
            >
              {categoryFilters.includes(item) ? <FaCheck /> : <span style={{ width: "16px" }}></span>}
              {item}
            </FilterDialogButton>
          ))}
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.time} onClick={() => handleFilters("time")}>
          시간
          {timeFilters !== 0 && `(${timeFormatter(timeFilters)})`}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.time} onClose={() => handleFilters("time")}>
          <div>시간 설정</div>
          <Slider type='time' defaultValue={timeFilters} onChange={handleTimeChange} />
          <DialogText>(기본값: 1일 전)</DialogText>
        </FilterDialog>

      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.similarity} onClick={() => handleFilters("similarity")}>
          유사도 점수
          {similarityFilters !== 0 && `(${similarityFilters}점)`}
          <MdChevronRight />

        </FilterButton>
        <FilterDialog open={filtersDialog.similarity} onClose={() => handleFilters("similarity")}>
          <div>유사도 점수 설정</div>
          <Slider type='score' defaultValue={similarityFilters} onChange={handleSimilarityChange} />
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.factScore} onClick={() => handleFilters("factScore")}>
          팩트 점수
          {factScoreFilters !== 0 && `(${factScoreFilters}점)`}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.factScore} onClose={() => handleFilters("factScore")}>
          <div>팩트 점수 설정</div>
          <Slider type='score' defaultValue={factScoreFilters} onChange={handleFactScoreChange} />
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.star} onClick={() => handleFilters("star")}>
          별점
          {starFilters !== 0 && `(${starFilters}점)`}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.star} onClose={() => handleFilters("star")}>
          <div>별점 설정</div>
          <StarContainer>
            {/* starFilters에 따라서 별점 개수를 채움*/}
            {[...Array(5)].map((_, index) => (
              <FaStar key={index} color={index < starFilters ? "#524DD6" : "#D3D3D3"} size={20} />
            ))}
          </StarContainer>
          <Slider type='star' defaultValue={starFilters} onChange={handleStarChange} />
        </FilterDialog>
      </FilterWrapper>

      <FilterLine />

      <FilterWrapper>
        <FilterButton open={filters.sort} onClick={() => handleFilters("sort")}>
          정렬
          {sortFilters !== "" && `(${sortFilters})`}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.sort} onClose={() => handleFilters("sort")}>
          {["최신순", "인기순", "유사도 점수순", "팩트 점수순", "조회 수순", "별점순"].map((item) => (
            <FilterDialogButton
              key={item}
              selected={sortFilters === item}
              onClick={() => handleSortChange(item)}
            >
              {sortFilters === item ? <FaCheck /> : <span style={{ width: "16px" }}></span>}
              {item}
            </FilterDialogButton>
          ))}
        </FilterDialog>
      </FilterWrapper>

      <FilterResetButton onClick={handleResetFilters}>
        {/* 필터 초기화 버튼 클릭 시 모든 필터 초기화 */}
        필터&정렬 초기화 <GrPowerReset />
      </FilterResetButton>
    </FilterAllWrapper>
  )
}

export default Filter;