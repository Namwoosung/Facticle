import { SNSContainer, SNSButton } from "./login.styles";
import { FcGoogle } from "react-icons/fc";
import { SiNaver } from "react-icons/si";
import { RiKakaoTalkFill } from 'react-icons/ri';

function SNSLogin() {
    const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI;
    const KAKAO_REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY;
    const KAKAO_LOGIN_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_REST_API_KEY}&redirect_uri=${REDIRECT_URI + '/kakao'}&response_type=code`;
    const NAVER_CLIENT_ID = import.meta.env.VITE_NAVER_CLIENT_ID; //
    const NAVER_CLIENT_SECRET = import.meta.env.VITE_NAVER_CLIENT_SECRET; //
    const NAVER_LOGIN_URL = `https://nid.naver.com/oauth2.0/authorize?client_id=${NAVER_CLIENT_ID}&response_type=code&redirect_uri=${REDIRECT_URI + '/naver'}&state=${NAVER_CLIENT_SECRET}`;
    const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID; //
    const GOOGLE_LOGIN_URL = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${GOOGLE_CLIENT_ID}&redirect_uri=${REDIRECT_URI + '/google'}&response_type=code&scope=email%20profile`;

    const handleKakaoLogin = () => {
        window.location.href = `${KAKAO_LOGIN_URL}&platform=kakao`;
    };
    
    const handleNaverLogin = () => {
        window.location.href = `${NAVER_LOGIN_URL}&platform=naver`;
    };
    
    const handleGoogleLogin = () => {
        window.location.href = `${GOOGLE_LOGIN_URL}&platform=google`;
    };

    return (
        <SNSContainer>
            <SNSButton bgColor="#FEE500" tabIndex={5} onClick={handleKakaoLogin}>
                <RiKakaoTalkFill color="#3E2723" size={30} />
            </SNSButton>
            <SNSButton bgColor="#03C75A" tabIndex={6} onClick={handleNaverLogin}>
                <SiNaver size={20} />
            </SNSButton>
            <SNSButton bgColor="#d9d9d9" tabIndex={7} onClick={handleGoogleLogin}>
                <FcGoogle size={30} />
            </SNSButton>
        </SNSContainer>
    );
}

export default SNSLogin;