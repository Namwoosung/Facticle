import styled from "styled-components";
import { Link } from "react-router-dom";

export const PageWrapper = styled.div`
    width: 1280px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
`;

export const CategoryWrapper = styled.div`
    display: flex;
    flex-direction: column;
    margin-top: 80px;
`;

export const CategoryTitleWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
`;

export const CategoryTitle = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: start;
`;

export const CategoryText = styled.p`
    font-size: 14px;
    color: #505050;
`;

//더보기 버튼
export const MoreButton = styled(Link)`
    font-size: 20px;
    font-weight: 600;
    color: #080E4B;
    display: flex;
    align-items: center;
    gap: 5px;
    transition: color 0.3s ease, transform 0.3s ease; /* color와 transform에 0.3초 애니메이션 적용 */

    &:hover {
        color: #4F5D74; /* hover 시 글자 색상 변경 */
        transform: translateX(5px); /* hover 시 버튼이 오른쪽으로 살짝 이동 */
    }
`;


export const CategoryNewsWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 20px;
    margin-top: 20px;
`;


export const NewsHighlightWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 40px;
    width: 100%;
    margin-top: 60px;
`;

//인기 뉴스
export const NewsHighlightContainer = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 50%;
`;

//인기 뉴스, 유사도 높은 뉴스 내용
export const NewsHighlightContent = styled.div`
    display: flex;
    gap: 10px;
    width: 100%;
`;

export const MoreNewsWrapper = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 5px;
`;

export const MoreNewsTitle = styled.p`
    font-size: 30px;
    font-weight: 500;
    color: black;
    margin-top: 80px;
`;

export const MoreNewsButton = styled(Link)`
    font-size: 20px;
    font-weight: 600;
    color: white;
    margin-bottom: 80px;
    padding: 10px 20px;
    border-radius: 25px;
    background-color: #080E4B;
    transition: all 0.3s ease-in-out; /* 모든 스타일 변화에 0.3초 동안 부드러운 애니메이션 적용 */

    &:hover {
        background-color: #4F5D74; /* hover 시 배경 색상 변경 */
        transform: translateY(-5px); /* hover 시 살짝 위로 이동 */
    }
`;

