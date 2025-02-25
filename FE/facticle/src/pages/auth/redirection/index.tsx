import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../../hooks/useAuth";
import authService from "../../../services/auth/auth.service";
import userService from "../../../services/user/user.service";
import { showSnackbar } from "../../../components/snackbar/util";

function Redirection() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login, getUserProfile } = useAuth();

    useEffect(() => {
        // 현재 URL에서 쿼리 스트링 읽기
        const params = new URLSearchParams(location.search);
        const pathSegments = location.pathname.split('/'); // 경로를 '/'로 분리
        const platform = pathSegments[pathSegments.length - 1];
        const code = params.get("code");

        if (!code) return;

        authService.loginSocial({ provider: platform, code })
            .then((response: any) => {
                if (response?.data?.code === 200) {
                    login(response.data.access_token);
                    if (response?.data?.is_new) {
                        navigate("/register-oauth", { replace: true });
                    } else {
                        navigate("/", { replace: true });
                        userService.getUserProfile()
                          .then((res: any) => {
                              getUserProfile(res.data.User.nickname, res.data.User.profileImage);
                              showSnackbar("로그인이 완료되었습니다.");
                              navigate("/");
                          });
                    }
                }
            })
            .catch((error: any) => {
                // 에러 처리
                navigate("/login", { replace: true });
            });
    
        navigate("/", { replace: true });
    }, [location, navigate]);

    return <></>;
}

export default Redirection;
