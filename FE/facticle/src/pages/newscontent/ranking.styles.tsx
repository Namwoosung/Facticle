import styled from 'styled-components';

export const RankingWrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-top: 60px;
`;

export const RankingTitleWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const RankingTitle = styled.h3`
    font-size: 20px;
    font-weight: bold;
`;

export const RankingSubTitle = styled.p`
    font-size: 16px;
    color: grey;
    text-align: start;
`

export const RankingBodyWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const RankingButtonWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-top: 20px;
`;

export const RankingButton = styled.button.withConfig({ // open을 props로 받음
  shouldForwardProp: (prop) => prop !== 'open',
})<{ open: boolean }>`
  width: 50%;
  height: 40px;
  background-color: ${({ open }) => (open ? '#080E4B' : '#E2E2E2')};
  color: ${({ open }) => (open ? 'white' : '#black')};
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
`;

export const RankingNewsContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 20px;
`;

