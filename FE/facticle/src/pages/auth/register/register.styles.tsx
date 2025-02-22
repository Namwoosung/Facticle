import styled from "styled-components";
import { Link } from "react-router-dom";

export const RegisterWrapper = styled.div`
    width: 400px;
    margin: 0 auto;
    display: flex;
    height: 100%;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 40px;
    margin-bottom: 40px
`;

export const HomeButton = styled(Link)`
    font-size: 50px;
    font-weight: 900;
    padding: 0.3em 1.2em;
    color: #080E4B;
    border-style: none;
    outline: none;
`;

//중복 확인 버튼
export const CheckButton = styled.button`
    width: 100px;
    height: 50px;
    border: 1px solid #d5d5d5;
    border-radius: 10px;
    background-color: #fff;
    color: black;
    font-size: 14px;
    margin-left: 10px;
    cursor: pointer;

    &:hover {
        opacity: 0.8;
    }
    &:focus {
        opacity: 0.8;
    }
`;

export const RegisterButton = styled.button`
    width: 70%;
    height: 50px;
    margin-top: 20px;
    border: none;
    border-radius: 10px;
    background-color: #080E4B;
    color: white;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;

    &:hover {
        opacity: 0.8;
    }
`;

