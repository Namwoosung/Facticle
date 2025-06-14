import Home from './pages/home';
import Login from './pages/auth/login';
import Register from './pages/auth/register';
import { Routes, Route } from 'react-router-dom';
import Redirection from './pages/auth/redirection';
import RegisterOauth from './pages/auth/register-oauth';
import MainLayout from './layouts/mainLayout';
import AuthLayout from './layouts/authLayout';
import Mypage from './pages/mypage';
import NewsPage from './pages/newspage';
import NewsContent from './pages/newscontent';

function App() {
  return (
    <>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/mypage" element={<Mypage />} />
          <Route path="/news" element={<NewsPage />} />
          <Route path="/news/:category" element={<NewsPage />} />
          <Route path="/news/content/:id" element={<NewsContent />} />
        </Route>

        <Route element={<AuthLayout />}>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/register-oauth" element={<RegisterOauth />} />
          <Route path='/oauth/callback/*' element={<Redirection />} />
        </Route>
      </Routes>
    </>
  )
}

export default App
