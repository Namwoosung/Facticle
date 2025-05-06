import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const PopularNewsWrapper = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
`;

export const PopularNewsTitleWrapper = styled.div`
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
`;

export const PopularNewsTitle = styled.h3`
    font-size: 20px;
    font-weight: bold;
`;

export const MoreButton = styled(Link)`
    font-size: 18px;
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

export const NewsContentWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
`;
