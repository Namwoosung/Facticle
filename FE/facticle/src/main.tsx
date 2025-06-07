import { createRoot } from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom'
import App from './App.tsx'
import { AuthProvider } from './context/index.tsx'
import { createGlobalStyle } from 'styled-components'
import ScrollToTop from './hooks/scrollToTop.tsx'

const GlobalStyles = createGlobalStyle`
  @font-face {
      font-family: 'NanumGothic', 
      src: url('https://hangeul.pstatic.net/hangeul_static/css/nanum-gothic.css') format('truetype');
  }

  body {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0 ;
    text-align: center;
    font-family: 'NanumGothic';
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

  p {
    margin: 0;
    padding: 0;
  }

`;

createRoot(document.getElementById('root')!).render(
  //<StrictMode>
  <Router>
    <GlobalStyles />
    <ScrollToTop />
    <AuthProvider>
      <App />
    </AuthProvider>
  </Router>
  //</StrictMode>,
)
