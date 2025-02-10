import { useState } from "react";
import { HomeButton, RegisterWrapper, RegisterButton, InputWrapper, CheckButton } from "./register.styles";
import Avatar from "../../../components/avatar";
import Input from "../../../components/input";


function Register() {
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

    const [checkFlags, setCheckFlags] = useState({
        username: false,
        nickname: false,
    });

    // 입력 값 변경 함수
    const handleChange = (key: string, value: string) => {
        setFormData((prev) => ({ ...prev, [key]: value }));
    };

    // Blur 이벤트 핸들러
    const handleBlur = (key: string) => {
        switch (key) {
            case "username":
                if (!formData.username) {
                    setErrors((prev) => ({ ...prev, id: { error: true, message: "아이디를 입력해주세요." } }));
                } else if (!checkFlags.username) {
                    setErrors((prev) => ({ ...prev, id: { error: true, message: "아이디 중복 확인을 해주세요." } }));
                } else {
                    setErrors((prev) => ({ ...prev, id: { error: false, message: "" } }));
                }
                break;
            case "password":
                if (!formData.password) {
                    setErrors((prev) => ({ ...prev, password: { error: true, message: "비밀번호를 입력해주세요." } }));
                } else if (!/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*])[A-Za-z\d~!@#$%^&*]{8,16}$/.test(formData.password)) {
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
                } else if (!checkFlags.nickname) {
                    setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임 중복 확인을 해주세요." } }));
                } else {
                    setErrors((prev) => ({ ...prev, nickname: { error: false, message: "" } }));
                }
                break;
            default:
                break;
        }
    };

    // ID 중복 확인 함수
    const handleCheck = (key: "username" | "nickname") => {
        setCheckFlags((prev) => ({ ...prev, [key]: true }));
        if (errors[key].error) {
            setErrors((prev) => ({ ...prev, [key]: { error: false, message: "" } }));
        }
    };

    // 회원가입 함수
    const handleRegister = () => {
        const newErrors = { ...errors };

        // ID 유효성 검사
        if (!formData.username) {
            newErrors.username = { error: true, message: "아이디를 입력해주세요." };
        } else if (!checkFlags.username) {
            newErrors.username = { error: true, message: "아이디 중복 확인을 해주세요." };
        } else {
            newErrors.username = { error: false, message: "" };
        }

        // 비밀번호 유효성 검사
        if (!formData.password) {
            newErrors.password = { error: true, message: "비밀번호를 입력해주세요." };
        } else {
            newErrors.password = { error: false, message: "" };
        }

        // 비밀번호 확인 유효성 검사
        if (!formData.passwordCheck) {
            newErrors.passwordCheck = { error: true, message: "비밀번호 확인을 입력해주세요." };
        } else if (formData.password !== formData.passwordCheck) {
            newErrors.passwordCheck = { error: true, message: "비밀번호가 일치하지 않습니다." };
        } else {
            newErrors.passwordCheck = { error: false, message: "" };
        }

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
    };

    return (
        <RegisterWrapper>
            <HomeButton to="/">FACTICLE</HomeButton>
            <Avatar size={150} />

            <InputWrapper>
                <Input
                    type="text"
                    value={formData.username}
                    placeholder="아이디"
                    error={errors.username.error}
                    errorMessage={errors.username.message}
                    tabIndex={1}
                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                        handleChange("username", event.target.value);
                    }}
                    onBlur={() => handleBlur("username")}
                    disabled={checkFlags.username}
                />
                <CheckButton
                    onClick={() => handleCheck("username")}
                    tabIndex={2}
                    disabled={checkFlags.username}>
                    중복 확인
                </CheckButton>
            </InputWrapper>


            <Input
                type="password"
                value={formData.password}
                placeholder="비밀번호"
                error={errors.password.error}
                errorMessage={errors.password.message}
                tabIndex={3}
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    handleChange("password", event.target.value);
                }}
                onBlur={() => handleBlur("password")}
            />
            <Input
                type="password"
                value={formData.passwordCheck}
                placeholder="비밀번호 확인"
                error={errors.passwordCheck.error}
                errorMessage={errors.passwordCheck.message}
                tabIndex={4}
                onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    handleChange("passwordCheck", event.target.value);
                }}
                onBlur={() => handleBlur("passwordCheck")}
            />
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
                    onBlur={() => handleBlur("nickname")}
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

export default Register;