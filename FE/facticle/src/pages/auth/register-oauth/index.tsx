import { useState } from "react";
import { HomeButton, RegisterWrapper, RegisterButton } from "./register.styles";
import Input from "../../../components/input";
import authService from "../../../services/auth/auth.service";
import { showSnackbar } from "../../../components/snackbar/util";
import { FaCheckCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import userService from "../../../services/user/user.service";


function RegisterOauth() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nickname: "",
    email: "",
  });

  const [errors, setErrors] = useState({
    nickname: { error: false, message: "" },
    email: { error: false, message: "" },
  });

  // 입력 값 변경 함수
  const handleChange = (key: string, value: string) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  // Blur 이벤트 핸들러
  const handleBlur = async (key: string) => {
    switch (key) {
      case "nickname":
        if (!formData.nickname) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임을 입력해주세요." } }));
        } else if (!/^[a-zA-Z0-9가-힣-_]{2,20}$/.test(formData.nickname)) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임은 2~20자 사이, 한글, 영어, 숫자, _,-만 사용할 수 있습니다." } }));
        } else {
          try {
            const response: any = await authService.nicknamecheck({ nickname: formData.nickname });
            if (response?.data?.code === 200 && response.data.is_available) {
              setErrors((prev) => ({ ...prev, nickname: { error: false, message: "" } }));
            } else {
              setErrors((prev) => ({ ...prev, nickname: { error: true, message: "이미 사용중인 닉네임입니다." } }));
            }
          } catch (error) {
            setErrors((prev) => ({ ...prev, nickname: { error: true, message: "서버 오류가 발생했습니다." } }));
          }
        }
        break;
      case "email":
        if (formData.email && !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.email)) {
          setErrors((prev) => ({ ...prev, email: { error: true, message: "이메일 형식이 올바르지 않습니다." } }));
        } else {
          setErrors((prev) => ({ ...prev, email: { error: false, message: "" } }));
        }
        break;
      default:
        break;
    }
  };

  // 회원가입 함수
  const handleRegister = async () => {
    if (Object.values(errors).some((field) => field.error)) return;

    // 에러가 없을 경우 회원가입 요청
    const registerData = {
      nickname: formData.nickname,
      email: formData.email.trim() === "" ? null : formData.email,
    };

    try {
      const response: any = await userService.updateUserProfile(registerData);
      if (response?.data?.code === 201) {
        showSnackbar("회원가입이 완료되었습니다.", <FaCheckCircle size={20} color="green" />);
        navigate("/login");
      }

    } catch (error: any) {
    }
  };

  return (
    <RegisterWrapper>
      <HomeButton to="/">FACTICLE</HomeButton>

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
        onBlur={() => handleBlur("nickname")}
      />
      <Input
        type="email"
        value={formData.email}
        placeholder="이메일 (선택사항)"
        error={errors.email.error}
        errorMessage={errors.email.message}
        tabIndex={6}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          handleChange("email", event.target.value);
        }}
        onBlur={() => handleBlur("email")}
      />

      <RegisterButton
        onClick={handleRegister}
        tabIndex={7}>
        회원가입
      </RegisterButton>
    </RegisterWrapper>
  );
}

export default RegisterOauth;