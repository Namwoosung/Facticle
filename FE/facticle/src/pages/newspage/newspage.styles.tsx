import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const PageWrapper = styled.div`
    width: 1280px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    margin-top: 80px;
`;

export const CategoryWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 40px;
`;

export const CategoryTitle = styled.p`
    font-size: 20px;
    font-weight: 600;
    color: black;
    margin-right: 70px;
    padding-bottom: 20px;
`;

export const CategoryButton = styled(Link).withConfig({
    shouldForwardProp: (prop) => !['isActive'].includes(prop),
})<{ isActive: boolean }>`
    font-size: 20px;
    font-weight: 600;
    color: black;
    padding-bottom: 20px;
    
     ${({ isActive }) =>
        isActive &&
        `
        color: #080E4B; /* 자기 경로일 때 글자 색상 변경 */
        border-bottom: 4px solid #080E4B; /* 자기 경로일 때 아래 선 추가 */
    `}
`;

export const CategoryLine = styled.div`
    position: relative;
    top: -20px;
    left: -50px;
    z-index: -1;
    width: 1380px;
    height: 1px;
    background-color: #D9D9D9;
`;

export const ControlsWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 10px;
`;

export const ControlsContainer = styled.div`
    display: flex;
    align-items: center;
`;

export const OptionButton = styled.button.withConfig({
    shouldForwardProp: (prop) => !['isActive'].includes(prop),
})<{ isActive: boolean }>`
    font-size: 16px;
    width: 100px;
    height: 50px;
    border: 1px solid black;
    border-radius: 5px;
    background-color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    margin-right: 10px;

    ${({ isActive }) =>
        isActive &&
        `
        cursor: pointer;
        border: 1px solid #524DD6;
        color: #524DD6;
    `}
`;

export const SearchButton = styled.button`
    font-size: 16px;
    width: 60px;
    height: 50px;
    border: none;
    border-radius: 10px;
    background-color: #080E4B;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;

    &:hover {
        cursor: pointer;
        opacity: 0.8;
    }
`;

export const ContentWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 20px;
`;

export const NewsContainer = styled.div`
    display: flex;
    gap: 20px;
    margin-top: 40px;
`;