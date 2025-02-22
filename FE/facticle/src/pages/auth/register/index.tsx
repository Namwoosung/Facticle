import { useState } from "react";
import { HomeButton, RegisterWrapper, RegisterButton, InputWrapper } from "./register.styles";
import Avatar from "../../../components/avatar";
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
  });

  const [errors, setErrors] = useState({
    username: { error: false, message: "" },
    password: { error: false, message: "" },
    passwordCheck: { error: false, message: "" },
    nickname: { error: false, message: "" },
  });

  // 입력 값 변경 함수
  const handleChange = (key: string, value: string) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  // Blur 이벤트 핸들러
  const handleBlur = async (key: string) => {
    switch (key) {
      case "username":
        if (!formData.username) {
          setErrors((prev) => ({ ...prev, username: { error: true, message: "아이디를 입력해주세요." } }));
        } else if (!/^[a-zA-Z0-9_]{4,50}$/.test(formData.username)) {
          setErrors((prev) => ({ ...prev, username: { error: true, message: "아이디는 4~50자 사이, 영어, 숫자, _만 사용할 수 있습니다." } }));
        } else {
          try {
            const response: any = await authService.idcheck({ username: formData.username });
            if (response?.data?.code === 200 && response.data.is_available) {
              setErrors((prev) => ({ ...prev, username: { error: false, message: "" } }));
            } else {
              setErrors((prev) => ({ ...prev, username: { error: true, message: "이미 사용중인 아이디입니다." } }));
            }
          } catch (error) {
            setErrors((prev) => ({ ...prev, username: { error: true, message: "서버 오류가 발생했습니다." } }));
          }
        }
        break;
      case "password":
        if (!formData.password) {
          setErrors((prev) => ({ ...prev, password: { error: true, message: "비밀번호를 입력해주세요." } }));
        } else if (!/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%?&])[A-Za-z\d@$!%*?&]{8,16}$/.test(formData.password)) {
          setErrors((prev) => ({ ...prev, password: { error: true, message: "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자를 사용해야 합니다." } }));
        } else {
          setErrors((prev) => ({ ...prev, password: { error: false, message: "" } }));
        }
        break;
      case "passwordCheck":
        if (!formData.passwordCheck) {
          setErrors((prev) => ({ ...prev, passwordCheck: { error: true, message: "비밀번호 확인을 입력해주세요." } }));
        } else if (formData.password !== formData.passwordCheck) {
          setErrors((prev) => ({ ...prev, passwordCheck: { error: true, message: "비밀번호가 일치하지 않습니다." } }));
        } else {
          setErrors((prev) => ({ ...prev, passwordCheck: { error: false, message: "" } }));
        }
        break;
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
      default:
        break;
    }
  };


  // 회원가입 함수
  const handleRegister = async () => {
    // const newErrors = { ...errors };

    // // ID 유효성 검사
    // if (!formData.username) {
    //   newErrors.username = { error: true, message: "아이디를 입력해주세요." };
    // } else {
    //   newErrors.username = { error: false, message: "" };
    // }

    // // 비밀번호 유효성 검사
    // if (!formData.password) {
    //   newErrors.password = { error: true, message: "비밀번호를 입력해주세요." };
    // } else {
    //   newErrors.password = { error: false, message: "" };
    // }

    // // 비밀번호 확인 유효성 검사
    // if (!formData.passwordCheck) {
    //   newErrors.passwordCheck = { error: true, message: "비밀번호 확인을 입력해주세요." };
    // } else if (formData.password !== formData.passwordCheck) {
    //   newErrors.passwordCheck = { error: true, message: "비밀번호가 일치하지 않습니다." };
    // } else {
    //   newErrors.passwordCheck = { error: false, message: "" };
    // }

    // // 닉네임 유효성 검사
    // if (!formData.nickname) {
    //   newErrors.nickname = { error: true, message: "닉네임을 입력해주세요." };
    // } else {
    //   newErrors.nickname = { error: false, message: "" };
    // }

    // setErrors(newErrors);

    if (Object.values(errors).some((field) => field.error)) return;

    // 에러가 없을 경우 회원가입 요청
    const registerData = {
      username: formData.username,
      password: formData.password,
      nickname: formData.nickname,
    };

    try {
      const response: any = await authService.register(registerData);
      if (response?.data?.code === 201) {
        showSnackbar("회원가입이 완료되었습니다.", <FaCheckCircle size={20} color="green" />);
        navigate("/login");
      }
    } catch (error: any) {
      // 오류 처리
    }
  };

  return (
    <RegisterWrapper>
      <HomeButton to="/">FACTICLE</HomeButton>
      <Avatar size={150} control={true} />

      <InputWrapper>
        <Input
          type="text"
          value={formData.username}
          placeholder="아이디"
          error={errors.username.error}
          errorMessage={errors.username.message}
          tabIndex={1}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => handleChange("username", event.target.value)}
          onBlur={() => handleBlur("username")}
        />
      </InputWrapper>

      <Input
        type="password"
        value={formData.password}
        placeholder="비밀번호"
        error={errors.password.error}
        errorMessage={errors.password.message}
        tabIndex={2}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => handleChange("password", event.target.value)}
        onBlur={() => handleBlur("password")}
      />
      <Input
        type="password"
        value={formData.passwordCheck}
        placeholder="비밀번호 확인"
        error={errors.passwordCheck.error}
        errorMessage={errors.passwordCheck.message}
        tabIndex={3}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => handleChange("passwordCheck", event.target.value)}
        onBlur={() => handleBlur("passwordCheck")}
      />
      <InputWrapper>
        <Input
          type="text"
          value={formData.nickname}
          placeholder="닉네임"
          error={errors.nickname.error}
          errorMessage={errors.nickname.message}
          tabIndex={4}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => handleChange("nickname", event.target.value)}
          onBlur={() => handleBlur("nickname")}
        />
      </InputWrapper>

      <RegisterButton
        onClick={handleRegister}
        tabIndex={5}>
        회원가입
      </RegisterButton>
    </RegisterWrapper>
  );
}

export default Register;
