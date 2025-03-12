import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom'
import App from './App.tsx'
import { AuthProvider } from './context/index.tsx'
import { createGlobalStyle } from 'styled-components'
import NanumGothic from './assets/fonts/NanumBarunGothic.ttf'

const GlobalStyles = createGlobalStyle`
  @font-face {
      font-family: 'Nanum';
      src: url(${NanumGothic}) format('truetype');
  }

  body {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0 ;
    text-align: center;
    font-family: 'Nanum';
  }

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

  a:visted {
      color: black;
  }

  h1, h2, h3, h4, h5, h6 {
    margin: 0;
    padding: 0;
    text-align: left;
  }

`;

createRoot(document.getElementById('root')!).render(
  //<StrictMode>
  <Router>
    <AuthProvider>
      <GlobalStyles />
      <App />
    </AuthProvider>
  </Router>
  //</StrictMode>,
)
