import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../../hooks/useAuth";
import authService from "../../../services/auth/auth.service";

function Redirection() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();

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
                    login(response.data.accessToken);
                    if (response?.data?.nickname === null) {
                        navigate("/register-oauth", { replace: true });
                    } else {
                        navigate("/", { replace: true });
                    }
                }
            })
            .catch((error: any) => {
                if (error.response?.status === 400) {
                    console.error("Unauthorized");
                } else {
                    console.error("Server Error");
                }
            });
    
        navigate("/", { replace: true });
    }, [location, navigate]);

    return <></>;
}

export default Redirection;
