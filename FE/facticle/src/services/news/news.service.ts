import htttpService from "../htttp.service";

class NewsService {
  getNewsList = async (filterValues: any, limit: number, page?: number) => {
    let filteredNewsEndpoint = '/news/search';

    // 키워드 설정(띄어쓰기로 되어있는거를 쉼표로 구분)
    if (filterValues.keyword.length > 0) {
      const keyword = filterValues.keyword.split(' ').join(',');
      filteredNewsEndpoint += `&titleOrContentKeyword=${keyword}`;
    }

    // 카테고리 설정(배열로 되어있는 것을 쉼표로 구분)
    if (filterValues.category.length > 0) {
      const categoryMap = {
        정치: 'POLITICS',
        경제: 'ECONOMY',
        사회: 'SOCIETY',
        국제: 'INTERNATIONAL',
        기술: 'TECH',
        문화: 'CULTURE',
        연예: 'ENTERTAINMENT',
        스포츠: 'SPORTS',
        날씨: 'WEATHER',
      } as const;
      const translated = filterValues.category
        .map((cat: keyof typeof categoryMap) => categoryMap[cat])
        .join(',');
      filteredNewsEndpoint += `&categories=${translated}`;
    }

    // 시간 설정 - 범위로 변경 (min, max 값 사용)
    if (filterValues.time && (filterValues.time.min !== -6 || filterValues.time.max !== 23)) {
      const now = new Date();
      let startDate: Date;
      let endDate: Date;

      // 시작 시간 계산 (min 값)
      if (filterValues.time.min < 0) {
        // 날짜 기준: -6 ~ -1 (ex: -2 → 이틀 전)
        startDate = new Date(now);
        startDate.setDate(now.getDate() + filterValues.time.min);
      } else {
        // 시간 기준: 0 ~ 23 (ex: 0 → 24시간 전, 23 → 1시간 전)
        startDate = new Date(now);
        startDate.setHours(now.getHours() - (24 - filterValues.time.min));
      }

      // 종료 시간 계산 (max 값)
      if (filterValues.time.max < 0) {
        // 날짜 기준
        endDate = new Date(now);
        endDate.setDate(now.getDate() + filterValues.time.max);
      } else if (filterValues.time.max >= 23) {
        // 현재까지
        endDate = new Date(now);
      } else {
        // 시간 기준
        endDate = new Date(now);
        endDate.setHours(now.getHours() - (24 - filterValues.time.max));
      }

      // KST 기준으로 YYYY-MM-DDTHH:MM:SS 포맷
      const pad = (n: number) => n.toString().padStart(2, '0');
      const formatDate = (date: Date) => {
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
      };
      
      filteredNewsEndpoint += `&startDate=${encodeURIComponent(formatDate(startDate))}&endDate=${encodeURIComponent(formatDate(endDate))}`;
    }

    // 유사도 점수 설정 - 범위로 변경 (min, max 값 사용)
    if (filterValues.similarity && (filterValues.similarity.min !== 0 || filterValues.similarity.max !== 100)) {
      filteredNewsEndpoint += `&minHsScore=${filterValues.similarity.min}&maxHsScore=${filterValues.similarity.max}`;
    }

    // 팩트 점수 설정 - 범위로 변경 (min, max 값 사용)
    if (filterValues.factScore && (filterValues.factScore.min !== 0 || filterValues.factScore.max !== 100)) {
      filteredNewsEndpoint += `&minFsScore=${filterValues.factScore.min}&maxFsScore=${filterValues.factScore.max}`;
    }

    // 별점 설정 - 범위로 변경 (min, max 값 사용)
    if (filterValues.star && (filterValues.star.min !== 0 || filterValues.star.max !== 5)) {
      filteredNewsEndpoint += `&minRating=${filterValues.star.min}&maxRating=${filterValues.star.max}`;
    }

    if (filterValues.sort .length > 0) {
      const sortMap = {
        최신순: 'COLLECTED_AT',
        유사도점수순: 'HEADLINE_SCORE',
        팩트점수순: 'FACT_SCORE',
        조회수순: 'VIEW_COUNT',
        별점순: 'RATING',
      } as const;
      // sort string을 가져와서 해당 맵에 매핑하는 string으로 변환
      const translated = sortMap[filterValues.sort as keyof typeof sortMap];
      filteredNewsEndpoint += `&sortBy=${translated}`;
    }

    if (limit > 0) {
      filteredNewsEndpoint += `&size=${limit}`;
    }

    if (page) {
      filteredNewsEndpoint += `&page=${page}`;
    }

    //첫 번째 &를 ?로 바꿔줌
    if (filteredNewsEndpoint.includes('&')) {
      filteredNewsEndpoint = filteredNewsEndpoint.replace('&', '?');
    }
    return await htttpService.get(filteredNewsEndpoint);
  }

  getNews = async (newsId: string) => {
    const newsEndpoint = `/news/${newsId}`;
    return await htttpService.get(newsEndpoint);
  }

  postAndUpdateNewsRating = async (newsId: string, rating: number) => {
    const ratingEndpoint = `/news/${newsId}/rating`;
    return await htttpService.post(ratingEndpoint, { rating });
  } 
  
  deleteNewsRating = async (newsId: string) => {
    const ratingEndpoint = `/news/${newsId}/rating`;
    return await htttpService.delete(ratingEndpoint);
  }
}

export default new NewsService();