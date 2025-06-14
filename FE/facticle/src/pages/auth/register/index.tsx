import { useState } from "react";
import { HomeButton, RegisterWrapper, RegisterButton, InputContainer } from "./register.styles";
import Input from "../../../components/input";
import authService from "../../../services/auth/auth.service";
import { showSnackbar } from "../../../components/snackbar/util";
import { FaCheckCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    passwordCheck: "",
    nickname: "",
    email: "",
  });

  const [errors, setErrors] = useState({
    username: { error: false, message: "" },
    password: { error: false, message: "" },
    passwordCheck: { error: false, message: "" },
    nickname: { error: false, message: "" },
    email: { error: false, message: "" },
  });

  const handleChange = (key: string, value: string) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault(); // 기본 동작(페이지 새로고침) 방지
    await handleRegister();
  };

  // Blur 이벤트 핸들러
  const handleBlur = async (key: string): Promise<boolean> => {
    switch (key) {
      case "username":
        if (!formData.username) {
          setErrors((prev) => ({ ...prev, username: { error: true, message: "아이디를 입력해주세요." } }));
          return false;
        }
  
        if (!/^[a-zA-Z0-9_]{4,50}$/.test(formData.username)) {
          setErrors((prev) => ({ ...prev, username: { error: true, message: "아이디는 4~50자 사이, 영어, 숫자, _만 사용할 수 있습니다." } }));
          return false;
        }
  
        try {
          const response: any = await authService.idcheck({ username: formData.username });
          const isAvailable = response?.data?.code === 200 && response.data.is_available;
  
          setErrors((prev) => ({
            ...prev,
            username: { error: !isAvailable, message: isAvailable ? "" : "이미 사용중인 아이디입니다." }
          }));
  
          return isAvailable;
        } catch (error) {
          setErrors((prev) => ({ ...prev, username: { error: true, message: "서버 오류가 발생했습니다." } }));
          return false;
        }
  
      case "password":
        if (!formData.password) {
          setErrors((prev) => ({ ...prev, password: { error: true, message: "비밀번호를 입력해주세요." } }));
          return false;
        }
  
        if (!/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%?&])[A-Za-z\d@$!%*?&]{8,16}$/.test(formData.password)) {
          setErrors((prev) => ({ ...prev, password: { error: true, message: "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자를 사용해야 합니다." } }));
          return false;
        }
  
        setErrors((prev) => ({ ...prev, password: { error: false, message: "" } }));
        return true;
  
      case "passwordCheck":
        if (!formData.passwordCheck) {
          setErrors((prev) => ({ ...prev, passwordCheck: { error: true, message: "비밀번호 확인을 입력해주세요." } }));
          return false;
        }
  
        if (formData.password !== formData.passwordCheck) {
          setErrors((prev) => ({ ...prev, passwordCheck: { error: true, message: "비밀번호가 일치하지 않습니다." } }));
          return false;
        }
  
        setErrors((prev) => ({ ...prev, passwordCheck: { error: false, message: "" } }));
        return true;
  
      case "nickname":
        if (!formData.nickname) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임을 입력해주세요." } }));
          return false;
        }
  
        if (!/^[a-zA-Z0-9가-힣-_]{2,20}$/.test(formData.nickname)) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임은 2~20자 사이, 한글, 영어, 숫자, _,-만 사용할 수 있습니다." } }));
          return false;
        }
  
        try {
          const response: any = await authService.nicknamecheck({ nickname: formData.nickname });
          const isAvailable = response?.data?.code === 200 && response.data.is_available;
  
          setErrors((prev) => ({
            ...prev,
            nickname: { error: !isAvailable, message: isAvailable ? "" : "이미 사용중인 닉네임입니다." }
          }));
  
          return isAvailable;
        } catch (error) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "서버 오류가 발생했습니다." } }));
          return false;
        }
  
      case "email":
        if (!formData.email?.trim()) {
          // 이메일이 없으면 검증할 필요 없음 (null 또는 빈 문자열 허용)
          setErrors((prev) => ({ ...prev, email: { error: false, message: "" } }));
          return true;
        }
        
        const isValidEmail = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.email);
        setErrors((prev) => ({
          ...prev,
          email: { error: !isValidEmail, message: isValidEmail ? "" : "이메일 형식이 올바르지 않습니다." }
        }));
  
        return isValidEmail;
  
      default:
        return false;
    }
  };

  const handleRegister = async () => {
    const isUsernameValid = await handleBlur("username");
    const isPasswordValid = await handleBlur("password");
    const isPasswordCheckValid = await handleBlur("passwordCheck");
    const isNicknameValid = await handleBlur("nickname");
    const isEmailValid = await handleBlur("email");

    if (!(isUsernameValid && isPasswordValid && isPasswordCheckValid && isNicknameValid && isEmailValid)) {
      return;
    }

    if (Object.values(errors).some((field) => field.error)) return;

    const registerData = {
      ...formData,
      email: formData.email.trim() === "" ? null : formData.email,
    };

    try {
      const response: any = await authService.register(registerData);
      if (response?.data?.code === 201) {
        showSnackbar("회원가입이 완료되었습니다.", <FaCheckCircle size={20} color="green" />);
        navigate("/login");
      }
    } catch (error: any) {
      console.error("회원가입 오류:", error);
    }
  };

  return (
    <RegisterWrapper>
      <HomeButton to="/">FACTICLE</HomeButton>
      <InputContainer onSubmit={handleSubmit}>
        <Input
          type="text"
          value={formData.username}
          placeholder="아이디"
          error={errors.username.error}
          errorMessage={errors.username.message}
          tabIndex={1}
          onChange={(event) => handleChange("username", event.target.value)}
          onBlur={() => handleBlur("username")}
          autoComplete="username"
        />
        <Input
          type="password"
          value={formData.password}
          placeholder="비밀번호"
          error={errors.password.error}
          errorMessage={errors.password.message}
          tabIndex={2}
          onChange={(event) => handleChange("password", event.target.value)}
          onBlur={() => handleBlur("password")}
          autoComplete="password"
        />
        <Input
          type="password"
          value={formData.passwordCheck}
          placeholder="비밀번호 확인"
          error={errors.passwordCheck.error}
          errorMessage={errors.passwordCheck.message}
          tabIndex={3}
          onChange={(event) => handleChange("passwordCheck", event.target.value)}
          onBlur={() => handleBlur("passwordCheck")}
          autoComplete="password-check"
        />
        <Input
          type="text"
          value={formData.nickname}
          placeholder="닉네임"
          error={errors.nickname.error}
          errorMessage={errors.nickname.message}
          tabIndex={4}
          onChange={(event) => handleChange("nickname", event.target.value)}
          onBlur={() => handleBlur("nickname")}
          autoComplete="nickname"
        />
        <Input
          type="text"
          value={formData.email}
          placeholder="이메일 (선택사항)"
          error={errors.email.error}
          errorMessage={errors.email.message}
          tabIndex={5}
          onChange={(event) => handleChange("email", event.target.value)}
          onBlur={() => handleBlur("email")}
          autoComplete="email"
        />
        <RegisterButton type="submit" tabIndex={6}>
          회원가입
        </RegisterButton>
      </InputContainer>
    </RegisterWrapper>
  );
}

export default Register;