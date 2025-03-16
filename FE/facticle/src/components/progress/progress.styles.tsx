import styled from "styled-components";

export const ProgressWrapper = styled.div`
  width: 100%;
  height: 5px;
  background: #e0e0e0;
  border-radius: 5px;
  overflow: hidden;
`;

export const ProgressFill = styled.div`
  height: 100%;
  transition: width 0.3s ease-in-out; /* 애니메이션 효과 */
`;