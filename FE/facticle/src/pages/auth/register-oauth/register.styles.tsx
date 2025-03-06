import styled from "styled-components";
import { Link } from "react-router-dom";

export const RegisterWrapper = styled.div`
    width: 400px;
    margin: 0 auto;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`;

export const HomeButton = styled(Link)`
    font-size: 50px;
    font-weight: 900;
    padding: 0.3em 1.2em;
    color: #080E4B;
    border-style: none;
    outline: none;
    margin-bottom: 30px;
`;


export const RegisterButton = styled.button`
    width: 70%;
    height: 60px;
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
    &:focus {
        opacity: 0.8;
    }
`;

