import { useState } from "react";
import { HomeButton, RegisterWrapper, RegisterButton, InputWrapper, CheckButton } from "./register.styles";
import Avatar from "../../../components/avatar";
import Input from "../../../components/input";
import authService from "../../../services/auth/auth.service";
import { showSnackbar } from "../../../components/snackbar/util";
import { FaCheckCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";


function RegisterOauth() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        nickname: "",
    });

    const [errors, setErrors] = useState({
        nickname: { error: false, message: "" },
    });

    const [checkFlags, setCheckFlags] = useState({
        nickname: false,
    });

    // 입력 값 변경 함수
    const handleChange = (key: string, value: string) => {
        setFormData((prev) => ({ ...prev, [key]: value }));
    };

    // Blur 이벤트 핸들러
    const handleBlur = () => {
        if (!formData.nickname) {
            setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임을 입력해주세요." } }));
        } else if (!checkFlags.nickname) {
            setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임 중복 확인을 해주세요." } }));
        } else {
            setErrors((prev) => ({ ...prev, nickname: { error: false, message: "" } }));
        }
    };

    // ID 중복 확인 함수
    const handleCheck = async (key: string) => {
        try {
            const response: any = await authService.nicknamecheck({ nickname: formData.nickname });
            if (response?.data?.code === 200) {
                if (response.data.is_available) {
                    setErrors((prev) => ({ ...prev, [key]: { error: false, message: "" } }));
                    setCheckFlags((prev) => ({ ...prev, [key]: true }));
                } else {
                    setErrors((prev) => ({ ...prev, [key]: { error: true, message: "이미 사용중인 닉네임입니다." } }));
                }
            }
        } catch (error: any) {
            if (error.response?.status === 400) {
                setErrors((prev) => ({ ...prev, [key]: { error: true, message: "2글자 이상 20글자 이하로 입력해주세요." } }));
            } else {
                setErrors((prev) => ({ ...prev, [key]: { error: true, message: "서버 오류가 발생했습니다." } }));
            }
        }
    };

    // 회원가입 함수
    const handleRegister = async () => {
        const newErrors = { ...errors };

        // 닉네임 유효성 검사
        if (!formData.nickname) {
            newErrors.nickname = { error: true, message: "닉네임을 입력해주세요." };
        } else if (!checkFlags.nickname) {
            newErrors.nickname = { error: true, message: "닉네임 중복 확인을 해주세요." };
        } else {
            newErrors.nickname = { error: false, message: "" };
        }

        setErrors(newErrors);

        // 에러가 없을 경우 회원가입 요청
        const registerData = {
            nickname: formData.nickname,
        };

        try {
            const response: any = await authService.registerSocial(registerData);
            if (response?.data?.code === 201) {
                showSnackbar("회원가입이 완료되었습니다.", <FaCheckCircle size={20} color="green"/>);
                navigate("/login");
            }

        } catch (error: any) {
        }
    };

    return (
        <RegisterWrapper>
            <HomeButton to="/">FACTICLE</HomeButton>
            <Avatar size={150} />

            <InputWrapper>
                <Input
                    type="text"
                    value={formData.nickname}
                    placeholder="닉네임"
                    error={errors.nickname.error}
                    errorMessage={errors.nickname.message}
                    tabIndex={5}
                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                        handleChange("nickname", event.target.value);
                    }}
                    onBlur={() => handleBlur()}
                    disabled={checkFlags.nickname}
                />
                <CheckButton
                    onClick={() => handleCheck("nickname")}
                    tabIndex={6}
                    disabled={checkFlags.nickname}>
                    중복 확인
                </CheckButton>
            </InputWrapper>

            <RegisterButton
                onClick={handleRegister}
                tabIndex={7}>
                회원가입
            </RegisterButton>
        </RegisterWrapper>
    );
}

export default RegisterOauth;