import { useState } from "react";
import Input from "../../../components/input";
import { showSnackbar } from "../../../components/snackbar/util";
import {
    LoginWrapper,
    HomeButton,
    ErrorText,
    LoginButton,
    RegisterButton,
    EasyLoginContainer,
    EasyLoginLine,
    EasyLoginText,
} from "./login.styles";
import authService from "../../../services/auth/auth.service";
import { useAuth } from "../../../hooks/useAuth";
import { useNavigate } from "react-router-dom";
import SNSLogin from "./snslogin";
import userService from "../../../services/user/user.service";

function Login() {
    const { login, getUserProfile } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: "", 
        password: "",
    });

    const [errorCredentials, setErrorCredentials] = useState("");

    const handleChange = (key: string, value: string) => {
        setFormData((prev) => ({ ...prev, [key]: value }));
    };

    const handleLogin = async () => {
        if (!formData.username || !formData.password) {
            setErrorCredentials("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        try {
            const response: any = await authService.login(formData);
    
            if (response?.data?.code === 200) {
                login(response.data.access_token);
                setErrorCredentials("");
    
                userService.getUserProfile()
                    .then((res: any) => {
                        getUserProfile(res.data.User.nickname, res.data.User.profileImage);
                        showSnackbar("로그인이 완료되었습니다.");
                        navigate("/");
                    });
            }
        } catch (error: any) {
            if (error.response?.status === 401) {
                setErrorCredentials("아이디 또는 비밀번호가 일치하지 않습니다.");
            } else {
                setErrorCredentials("서버 오류가 발생했습니다.");
            }
        }
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === "Enter") {
            handleLogin();
        }
    };

    return (
        <LoginWrapper>
            <HomeButton to="/">FACTICLE</HomeButton>
            <Input
                type="text"
                value={formData.username}
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    handleChange("username", event.target.value);
                }}
                placeholder="아이디"
                tabIndex={1}
            />
            <Input
                type="password"
                value={formData.password}
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    handleChange("password", event.target.value);
                }}
                placeholder="비밀번호"
                onKeyDown={handleKeyDown}
                tabIndex={2}
            />

            {errorCredentials && <ErrorText>{errorCredentials}</ErrorText>}
            <LoginButton
                onClick={handleLogin}
                tabIndex={3}>
                    로그인
            </LoginButton>

            <RegisterButton to="/register" tabIndex={4}>회원가입</RegisterButton>

            <EasyLoginContainer>
                <EasyLoginLine />
                <EasyLoginText>간편로그인</EasyLoginText>
            </EasyLoginContainer>

            <SNSLogin />
        </LoginWrapper>
    );
}

export default Login;