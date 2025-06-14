import styled from 'styled-components';
import { Link } from 'react-router-dom';

// NewsWrapper에서 정의할 수 있도록 위에 선언 (hover 시 ReviewWrapper의 opacity를 1로 변경)
export const ReviewWrapper = styled.div.withConfig({
  shouldForwardProp: (prop) => prop !== 'direction' // 'direction' prop을 DOM에 전달되지 않도록 필터링
})<{ direction: 'row' | 'column' }>`
  position: absolute;
  top: 10px;
  left: 10px;
  width: fit-content;
  height: ${({ direction }) => (direction === 'row' ? '15px' : '30px')};
  padding: 5px 10px;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 5px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  opacity: 0; /* 초기에는 별점 표시 안함 */
  transition: opacity 0.2s ease-in-out;
`;

export const ReviewText = styled.span.withConfig({
  shouldForwardProp: (prop) => prop !== 'direction' // 'direction' prop을 DOM에 전달되지 않도록 필터링
})<{ direction: 'row' | 'column' }>`
  font-size: ${({ direction }) => (direction === 'row' ? '12px' : '16px')};
  font-weight: 600;
  color: black;
`;

export const NewsWrapper = styled(Link).withConfig({
  shouldForwardProp: (prop) => prop !== 'direction' // 'direction' prop을 DOM에 전달되지 않도록 필터링
})<{ direction: 'row' | 'column' }>`
  position: relative;
  display: flex;
  flex-direction: ${({ direction }) => (direction === 'row' ? 'row' : 'column')};
  align-items: ${({ direction }) => (direction === 'row' ? 'center' : 'none')};
  gap: 10px;
  width: 100%;
  height: 100%;
  border-radius: 10px;
  text-decoration: none;
  transition: transform 0.2s ease-in-out;

  &:hover {
    transform: translateY(-5px);
  }

  &:hover ${ReviewWrapper} {
    opacity: 1; /* Hover 시 별점 표시 */
  }
`;

export const NewsImage = styled.img.withConfig({
  shouldForwardProp: (prop) => prop !== 'direction' && prop !== '$customheight'
})<{ direction: 'row' | 'column'; $customheight?: number }>`
  width: ${({ direction }) => (direction === 'row' ? '100px' : '100%')};
  height: ${({ direction, $customheight }) =>
    $customheight ? `${$customheight}px` : direction === 'row' ? '100px' : '300px'};
  min-width: ${({ direction }) => (direction === 'row' ? '100px' : 'auto')};
  min-height: ${({ direction }) => (direction === 'row' ? '100px' : 'auto')};
  border-radius: 10px;
  object-fit: cover; /* 이미지 비율 유지하면서 채우기 */
  overflow: hidden;
  background: white;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.2);
`;

export const NewsContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
`;

export const NewsTitle = styled.h3.withConfig({
  shouldForwardProp: (prop) => prop !== 'size' && prop !== 'direction' // 'size'와 'direction' prop을 DOM에 전달되지 않도록 필터링
})<{ size?: number; direction: 'row' | 'column' }>`
  //height는 고정
  height: ${({ direction }) => (direction === 'row' ? 'fit-content' : '45px')};
  font-size: ${({ size }) => (size ? `${size}px` : '20px')};
  font-weight: 600;
  text-align: left;
  color: black;
  text-overflow: ellipsis;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: ${({ direction }) => (direction === 'row' ? 1 : 2)}; /* 최대 2줄 */
  -webkit-box-orient: vertical;
`;

export const NewsContent = styled.p`
  font-size: 16px;
  color: #505050;
  text-align: left;
  text-overflow: ellipsis;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 최대 3줄 */
  -webkit-box-orient: vertical;
  margin: 5px 0;
`;

export const NewsInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 5px;
  margin-top: 5px;
`;

export const ScoreWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 5px;
`;

export const ScoreText = styled.p`
  font-size: 14px;
  font-weight: 600;
  color: black;
`;

export const ScoreNumber = styled.span`
  font-size: 14px;
  font-weight: 600;
  background-color: #D9D9D9;
  border-radius: 20px;
  padding: 3px 6px;
  color: black;
`;

export const TimeText = styled.p`
  font-size: 14px;
  font-weight: 600;
  color: #A4A4A4;
  white-space: nowrap;
`;