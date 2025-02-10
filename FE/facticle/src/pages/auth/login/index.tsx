import React, { useState } from "react";
import Input from "../../../components/input";
import { FcGoogle } from "react-icons/fc";
import { SiNaver } from "react-icons/si";
import { RiKakaoTalkFill } from "react-icons/ri";
import {
    LoginWrapper,
    HomeButton,
    ErrorText,
    LoginButton,
    RegisterButton,
    EasyLoginContainer,
    EasyLoginLine,
    EasyLoginText,
    SNSContainer,
    SNSButton,
} from "./login.styles";
import authService from "../../../services/auth/auth.service";

function Login() {
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

            <SNSContainer>
                <SNSButton bgColor="#FEE500" tabIndex={5}>
                    <RiKakaoTalkFill color="#3E2723" size={30} />
                </SNSButton>
                <SNSButton bgColor="#03C75A" tabIndex={6}>
                    <SiNaver  size={20}/>
                </SNSButton>
                <SNSButton bgColor="#d9d9d9" tabIndex={7}>
                    <FcGoogle size={30}/>
                </SNSButton>
            </SNSContainer>
        </LoginWrapper>
    );
}

export default Login;