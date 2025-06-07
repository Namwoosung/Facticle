import { useEffect, useState } from "react";
import { DialogText, FilterAllWrapper, FilterButton, FilterDialogButton, FilterLine, FilterResetButton, FilterWrapper } from "./filter.styles";
import FilterDialog from "../../components/filterdialog";
import { MdChevronRight } from "react-icons/md";
import { GrPowerReset } from "react-icons/gr";
import { FaCheck } from "react-icons/fa6";
import DualSlider from "../../components/slider";

function Filter( {onFilterChange}: { onFilterChange: (filters: any) => void }) {
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
  const [categoryFilters, setCategoryFilters] = useState<string[]>([]);
  
  // 범위 필터들 - min, max 값으로 변경
  const [timeFilters, setTimeFilters] = useState<{min: number, max: number}>({min: -6, max: 23});
  const [similarityFilters, setSimilarityFilters] = useState<{min: number, max: number}>({min: 0, max: 100});
  const [factScoreFilters, setFactScoreFilters] = useState<{min: number, max: number}>({min: 0, max: 100});
  const [starFilters, setStarFilters] = useState<{min: number, max: number}>({min: 0, max: 5});
  
  const [sortFilters, setSortFilters] = useState<string>("");

  // 범위 필터 핸들러들
  const handleTimeChange = (minLabel: string, maxLabel: string, minValue: number, maxValue: number) => {
    setTimeFilters({min: minValue, max: maxValue});
  };

  const handleSimilarityChange = (minLabel: string, maxLabel: string, minValue: number, maxValue: number) => {
    setSimilarityFilters({min: minValue, max: maxValue});
  };

  const handleFactScoreChange = (minLabel: string, maxLabel: string, minValue: number, maxValue: number) => {
    setFactScoreFilters({min: minValue, max: maxValue});
  };

  const handleStarChange = (minLabel: string, maxLabel: string, minValue: number, maxValue: number) => {
    setStarFilters({min: minValue, max: maxValue});
  };

  const handleFilters = (filter: keyof Filters) => {
    setFilters((prevFilters: Filters) => {
      const newFilters: Filters = Object.keys(prevFilters).reduce((acc, key) => {
        if (categoryFilters.length > 0 && key === "category") {
          acc[key as keyof Filters] = true;
        }
        else if ((timeFilters.min !== -6 || timeFilters.max !== 23) && key === "time") {
          acc[key as keyof Filters] = true;
        }
        else if ((similarityFilters.min !== 0 || similarityFilters.max !== 100) && key === "similarity") {
          acc[key as keyof Filters] = true;
        }
        else if ((factScoreFilters.min !== 0 || factScoreFilters.max !== 100) && key === "factScore") {
          acc[key as keyof Filters] = true;
        }
        else if ((starFilters.min !== 0 || starFilters.max !== 5) && key === "star") {
          acc[key as keyof Filters] = true;
        }
        else if (sortFilters !== "" && key === "sort") {
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
  };

  const handleCategoryChange = (category: string) => {
    const ALL_CATEGORIES = ["정치", "경제", "사회", "국제", "기술", "문화", "연예", "스포츠", "날씨"];

    setCategoryFilters((prev) => {
      if (category === "전체") {
        return [];
      }

      const newFilters = prev.includes(category)
        ? prev.filter((c) => c !== category)
        : [...prev, category];

      const allSelected = ALL_CATEGORIES.every(c => newFilters.includes(c));
      return allSelected ? [] : newFilters;
    });
  };

  const handleSortChange = (newValue: string) => {
    setSortFilters((prev) => (prev === newValue ? '' : newValue));
  };

  const handleResetFilters = () => {
    setCategoryFilters([]);
    setTimeFilters({min: -6, max: 23});
    setSimilarityFilters({min: 0, max: 100});
    setFactScoreFilters({min: 0, max: 100});
    setStarFilters({min: 0, max: 5});
    setSortFilters('');

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

  // 필터 변경 시 부모 컴포넌트에 즉시 전달
  useEffect(() => {
    onFilterChange({
      category: categoryFilters,
      time: timeFilters,
      similarity: similarityFilters,
      factScore: factScoreFilters,
      star: starFilters,
      sort: sortFilters,
    });
  }, [categoryFilters, timeFilters, similarityFilters, factScoreFilters, starFilters, sortFilters, onFilterChange]);

  // 범위 표시 헬퍼 함수들
  const getTimeRangeText = () => {
    if (timeFilters.min === -6 && timeFilters.max === 23) return '';
    return `(${timeFormatter(timeFilters.min)} ~ ${timeFormatter(timeFilters.max)})`;
  };

  const getScoreRangeText = (filter: {min: number, max: number}, maxVal: number) => {
    if (filter.min === 0 && filter.max === maxVal) return '';
    return `(${filter.min}점 ~ ${filter.max}점)`;
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
          {getTimeRangeText()}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.time} onClose={() => handleFilters("time")} hasSlider={true}>
          <div style={{fontSize: '16px', fontWeight: '600', marginBottom: '10px'}}>시간 범위 설정</div>
          <DualSlider 
            type='time' 
            defaultMinValue={timeFilters.min} 
            defaultMaxValue={timeFilters.max}
            onChange={handleTimeChange} 
          />
          <DialogText>기간을 설정하세요</DialogText>
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.similarity} onClick={() => handleFilters("similarity")}>
          유사도 점수
          {getScoreRangeText(similarityFilters, 100)}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.similarity} onClose={() => handleFilters("similarity")} hasSlider={true}>
          <div style={{fontSize: '16px', fontWeight: '600', marginBottom: '10px'}}>유사도 점수 범위 설정</div>
          <DualSlider 
            type='score' 
            defaultMinValue={similarityFilters.min} 
            defaultMaxValue={similarityFilters.max}
            onChange={handleSimilarityChange} 
          />
          <DialogText>최소 ~ 최대 점수를 설정하세요</DialogText>
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.factScore} onClick={() => handleFilters("factScore")}>
          팩트 점수
          {getScoreRangeText(factScoreFilters, 100)}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.factScore} onClose={() => handleFilters("factScore")} hasSlider={true}>
          <div style={{fontSize: '16px', fontWeight: '600', marginBottom: '10px'}}>팩트 점수 범위 설정</div>
          <DualSlider 
            type='score' 
            defaultMinValue={factScoreFilters.min} 
            defaultMaxValue={factScoreFilters.max}
            onChange={handleFactScoreChange} 
          />
          <DialogText>최소 ~ 최대 점수를 설정하세요</DialogText>
        </FilterDialog>
      </FilterWrapper>

      <FilterWrapper>
        <FilterButton open={filters.star} onClick={() => handleFilters("star")}>
          별점
          {getScoreRangeText(starFilters, 5)}
          <MdChevronRight />
        </FilterButton>
        <FilterDialog open={filtersDialog.star} onClose={() => handleFilters("star")} hasSlider={true}>
          <div style={{fontSize: '16px', fontWeight: '600', marginBottom: '10px'}}>별점 범위 설정</div>
          
          <DualSlider 
            type='star' 
            defaultMinValue={starFilters.min} 
            defaultMaxValue={starFilters.max}
            onChange={handleStarChange} 
          />
          <DialogText>최소 ~ 최대 별점을 설정하세요</DialogText>
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
          {["최신순", "유사도점수순", "팩트점수순", "조회수순", "별점순"].map((item) => (
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
        필터&정렬 초기화 <GrPowerReset />
      </FilterResetButton>
    </FilterAllWrapper>
  );
}

export default Filter;