import styled from "styled-components";
import { Link } from "react-router-dom";

export const Wrapper = styled.div`
    width: 1280px;
    margin: 0 auto;
    display: flex;
    margin-top: 80px;
    gap: 50px;
`;

export const SideWrapper = styled.div`
    width: 30%;
    position: sticky;
    top: 0;
    max-height: fit-content;
    margin-bottom: 20px;
`;

export const NewsContentWrapper = styled.div`
    width: 70%;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
`;

export const NewsContentHeader = styled.div`
    display: flex;
    flex-direction: column;
    gap: 10px;
`;

export const NewsContentTitle = styled.h1`
    font-size: 30px;
    font-weight: bold;
`;

export const NewsContentInfo = styled.div`
    width: 100%;
    display: flex;
    align-items: center;
    margin-top: 10px;
`;

export const NewsContentDate = styled.span`
    font-size: 14px;
    color: #999999;
`;

export const InfoLine = styled.span`
    width: 1px;
    height: 14px;
    background-color: #999999;
    display: block;
    margin: 0 15px;
`;

export const ScoreWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
`;

export const ScoreContainer = styled.div`
    width: 300px;
    display: flex;
    align-items: center;
    gap: 5px;
`;

export const ScoreText = styled.p`
  width: 100px;
  font-size: 16px;
  font-weight: 600;
  color: black;
`;

export const StarContainer = styled.span`
    width: fit-content;
    padding: 5px 10px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
    background-color: #E2E2E2;
`;

export const StarText = styled.p`
    font-size: 16px;
    font-weight: 600;
    color: black;
    margin-left: 5px;
`;
 
export const OriginalButton = styled(Link)`
    width: fit-content;
    height: fit-content;
    border-radius: 10px;
    border: 1px solid #E2E2E2;
    background-color: #fff;
    font-size: 14px;
    color: black;
    cursor: pointer;
    padding: 5px 10px;
    margin-left: 10px;
`;

export const NewsContentImg = styled.img`
    width: 100%;
    height: auto;
`;

export const NewsContentBody = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 30px;
`;

export const NewsContentBodyTextWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 20px;
`;

export const NewsContentBodySubText = styled.p`
    font-size: 14px;
    color: #999999;
    text-align: left;
`;

export const NewsContentBodyText = styled.div`
    font-size: 16px;
    font-weight: 600;
    background-color: #E2E2E2;
    padding: 30px 20px;
    border-radius: 10px;
    text-align: left;
    line-height: 1.5;
`;

export const ReasonWrapper = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 40px;
    margin-top: 20px;
`;

export const ReasonContainer = styled.div`
    flex: 1;
    width: 100%;
    height: fit-content;
    display: flex;
    flex-direction: column;
    gap: 15px;
    border-radius: 10px;
    border: 1px solid #E2E2E2;
    box-sizing: border-box;
    box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.1);
    padding: 25px 0px;
`;

export const ReasonTitle = styled.h3`
    font-size: 20px;
    font-weight: bold;
    color: black;
    margin-left: 20px;
`;

export const ReasonText = styled.p`
    font-size: 16px;
    font-weight: 500;
    color: black;
    margin: 0 20px;
    padding: 10px 20px;
    background-color: #F5F5F5;
    border-radius: 10px;
    text-align: left;
    line-height: 2;
`;

export const NewsContentLine = styled.div`
    width: 100%;
    height: 0.5px;
    background-color: #D9D9D9;
    margin: 20px 0;
`;

export const ReviewWrapper = styled.div`
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    gap: 20px;
`;

export const ReviewTitle = styled.h3`
    margin: 0;
    display: flex;
    align-items: center;
`;

export const DeleteRatingButton = styled.button<{ $loading?: boolean }>`
    margin-left: 15px;
    padding: 4px 8px;
    background-color: transparent;
    color: #dc3545;
    border: 1px solid #dc3545;
    border-radius: 4px;
    cursor: ${props => props.$loading ? 'not-allowed' : 'pointer'};
    opacity: ${props => props.$loading ? 0.5 : 1};
    font-size: 12px;
    font-weight: normal;
    transition: all 0.2s ease;

    &:hover {
        background-color: ${props => props.$loading ? 'transparent' : '#dc3545'};
        color: ${props => props.$loading ? '#dc3545' : 'white'};
    }

    &:disabled {
        cursor: not-allowed;
        opacity: 0.5;
    }
`;

export const StarWrapper = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 30px;
`;

export const StarClickArea = styled.div<{ $isAuthenticated: boolean }>`
    position: relative;
    width: 60px;
    height: 60px;
`;

export const StarHalfClickArea = styled.div<{ $isAuthenticated: boolean; $isLeft?: boolean }>`
    position: absolute;
    width: 50%;
    height: 100%;
    ${props => props.$isLeft ? 'left: 0;' : 'right: 0;'}
    top: 0;
    z-index: 1;
    cursor: ${props => props.$isAuthenticated ? 'pointer' : 'not-allowed'};
`;

export const RatingButtonWrapper = styled.div`
    display: flex;
    justify-content: center;
    margin-top: 20px;
`;

export const ConfiremButton = styled.button<{ $disabled?: boolean }>`
    width: 50%;
    height: 50px;
    border-radius: 10px;
    border: 1px solid #E2E2E2;
    background-color: #fff;
    font-size: 16px;
    font-weight: 600;
    color: black;
    cursor: ${props => props.$disabled ? 'not-allowed' : 'pointer'};
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    opacity: ${props => props.$disabled ? 0.5 : 1};

    &:disabled {
        cursor: not-allowed;
        opacity: 0.5;
    }
`;

export const LoginWarningText = styled.p`
    color: #666;
    font-size: 14px;
    margin-top: 15px;
    text-align: center;
`;


