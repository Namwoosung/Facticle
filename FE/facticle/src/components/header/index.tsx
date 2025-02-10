import { HomeButton, NavButton, LoginButton, RegisterButton, HeaderWrapper } from "./header.styles";

function Header() {
    return (
        <HeaderWrapper>
            <div>
                <HomeButton to="/">FACTICLE</HomeButton>
                <NavButton to="/">홈</NavButton>
                <NavButton to="/about"> 소개</NavButton>
                <NavButton to="/news">뉴스 보기</NavButton>
                <NavButton to="/search">검색</NavButton>
            </div>

            <div>
                <LoginButton to="/login">로그인</LoginButton>
                <RegisterButton to="/register">회원가입</RegisterButton>
            </div>     
        </HeaderWrapper>
    );
}

export default Header;