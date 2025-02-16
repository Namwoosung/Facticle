import styled from 'styled-components'
import Home from './pages/home';
import Login from './pages/auth/login';
import Register from './pages/auth/register';
import { Routes, Route } from 'react-router-dom';
import Redirection from './pages/auth/redirection';
import RegisterOauth from './pages/auth/register-oauth';

const RootElement = styled.div`
  @font-face {
    font-family: 'Nanum';
    src: url('/assets/fonts/NanumBarunGothic.ttf');
  }

  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0 ;
  text-align: center;
  font-family: 'Nanum';

  button {
    cursor: pointer;

    &:hover {
      outline: none;
    }
    &:focus {
      outline: none;
    }
    &:active {
      outline: none;
    }
  }

  a {
    text-decoration: none;
  }
`;

function App() {
  return (
    <RootElement>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/register-oauth" element={<RegisterOauth />} />
        <Route path='/oauth/callback/*' element={<Redirection />} />
      </Routes>
    </RootElement>
  )
}

export default App
