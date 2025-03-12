import styled from "styled-components";
import { Link } from "react-router-dom";

export const HeaderWrapper = styled.div`
  width: 1280px;
  margin: 0 auto;
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const ProfileContainer = styled.div`
  display: flex;
  align-items: center;
`;

export const HomeButton = styled(Link)`
  font-size: 28px;
  font-weight: bold;
  padding: 0.6em 1.2em;
  padding-left: 0;
  margin-right: 20px;
  color: #080E4B;
  border-style: none;
  outline: none;
`;

export const NavButton = styled(Link)`
  font-size: 18px;
  font-weight: bold;
  padding: 0.6em 1.2em;
  color: #484848;
  border-style: none;
  outline: none;

  &:hover {
    text-decoration: underline;
  }
`;

export const LoginButton = styled(NavButton)`
  color: #080E4B;
`;

export const RegisterButton = styled(Link)`
  font-size: 18px;
  font-weight: bold;
  color: white;
  background-color: #080E4B;
  border-style: none;
  border-radius: 7px;
  outline: none;
  padding: 0.6em 0.8em;

  &:hover {
    opacity: 0.8;
  }
`;
