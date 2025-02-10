import styled from "styled-components";

export const InputWrapper = styled.div`
  position: relative;
  width: 100%;
  margin-bottom: 30px;
`;

export const InputField = styled.input<{ error?: boolean }>`
  width: 100%;
  height: 50px;
  padding: 10px;
  padding-left: 15px;
  padding-right: 40px; /* 지우기 버튼을 위해 여유 공간을 추가 */
  border: 1px solid ${(props) => (props.error ? "#e74c3c" : "#d5d5d5")};
  border-radius: 10px;
  font-size: 16px;
  background-color: #fff;
  color: #333;
  box-sizing: border-box;

  &:focus {
    border-color: ${(props) => (props.error ? "#e74c3c" : "#080E4B")};
    border-width: 2px;
    background-color: #fff;
    outline: none;
  }
  &:disabled {
    background-color: #d5d5d5;
  }
`;

export const ErrorMessage = styled.span`
  color: #e74c3c;
  font-size: 12px;
  position: absolute;
  bottom: -18px;
  left: 10px;
`;

export const ClearButton = styled.button`
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #909090;
  cursor: pointer;
  font-size: 18px;
  
  &:hover {
    color: #333;
  }

  &:focus {
    outline: none;
  }
  
`;

export const ShowPasswordButton = styled.button`
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #909090;
  font-size: 18px;
  
  &:hover {
    color: #333;
  }

  &:focus {
    outline: none;
  }
`;