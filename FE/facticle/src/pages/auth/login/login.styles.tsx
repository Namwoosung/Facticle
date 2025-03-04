import styled from "styled-components";
import { Link } from "react-router-dom";

export const LoginWrapper = styled.div`
    width: 400px;
    margin: 0 auto;
    margin-top: 100px;
    margin-bottom: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
`;

export const HomeButton = styled(Link)`
    font-size: 50px;
    font-weight: bold;
    padding: 0.6em 1.2em;
    margin-bottom: 20px;
    color: #080E4B;
    border-style: none;
    outline: none;
`;

export const InputContainer = styled.form`
    width: 100%;
`;

export const ErrorText = styled.p`
    color: red;
    font-size: 13px;
    margin-top: 0px;
`;

export const LoginButton = styled.button`
    width: 100%;
    height: 60px;
    margin-top: 10px;
    border: none;
    border-radius: 10px;
    background-color: #080E4B;
    color: #fff;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;

    &:hover {
        opacity: 0.8;
    }
    &:focus {
        opacity: 0.8;
    }
`;

export const RegisterButton = styled(Link)`
    margin-top: 20px;
    text-decoration: none;
    color: black;
    font-size: 16px;
    cursor: pointer;
    border-style: none;
    outline: none;
`;

export const EasyLoginContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 100%;
  height: 60px;
  margin-top: 40px;
`;

export const EasyLoginLine = styled.div`
  position: absolute;
  width: 100%;
  height: 1px;
  background-color: #909090;
`;

export const EasyLoginText = styled.span`
  position: relative;
  padding: 0 16px;
  font-size: 16px;
  background-color: white;
  color: #909090;
`;

export const SNSContainer = styled.div`
  display: flex;
  gap: 40px;
  justify-content: center;
  margin-top: 20px;
`;

export const SNSButton = styled.button.withConfig({
  shouldForwardProp: (prop) => prop !== "bgColor"
})<{ bgColor: string }>`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background-color: ${(props) => props.bgColor};
  color: white;
  font-size: 24px;
  
  &:hover {
    opacity: 0.8;
  }
  &:focus {
    opacity: 0.8;
  }
`;
