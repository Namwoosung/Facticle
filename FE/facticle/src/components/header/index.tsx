import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import Avatar from "../avatar";
import { HeaderWrapper, ProfileContainer, HomeButton, NavButton, LoginButton, RegisterButton } from "./header.styles";

function Header() {
    const { isAuthenticated, nickname, profileImage, logout } = useAuth();
    const [avatarImage, setAvatarImage] = useState(profileImage);

    const handleLogout = () => {
        logout();
    };

    useEffect(() => {
        setAvatarImage(profileImage);
    }, [profileImage]);

    return (
        <HeaderWrapper>
            <div>
                <HomeButton to="/">FACTICLE</HomeButton>
                <NavButton to="/">홈</NavButton>
                <NavButton to="/news">뉴스 보기</NavButton>
            </div>

            {isAuthenticated ? (
                <div style={{ display: "flex", alignItems: "center" }}>
                    <ProfileContainer>
                        <Avatar src={avatarImage} />
                        <NavButton to="/mypage">{nickname} 님</NavButton>
                    </ProfileContainer>
                    <RegisterButton to="/login" onClick={handleLogout}>로그아웃</RegisterButton>
                </div>
            ) : (
                <div>
                    <LoginButton to="/login">로그인</LoginButton>
                    <RegisterButton to="/register">회원가입</RegisterButton>
                </div>
            )}
        </HeaderWrapper>
    );
}

export default Header;