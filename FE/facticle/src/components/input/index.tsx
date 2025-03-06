import { useState } from "react";
import { IoEyeSharp } from "react-icons/io5";
import { FaEyeSlash } from "react-icons/fa";
import { InputWrapper, InputField, ErrorMessage, ClearButton, ShowPasswordButton } from "./input.styles";

interface InputProps {
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void;
  error?: boolean;
  errorMessage?: string;
  placeholder?: string;
  title?: string;
  disabled?: boolean;
  [key: string]: any;
}

function Input({ type, value, onChange, onBlur, error, errorMessage, placeholder, title, disabled, ...props }: InputProps) {
  const [showPassword, setShowPassword] = useState(false);

  const handleClear = () => {
    onChange({ target: { value: "" } } as React.ChangeEvent<HTMLInputElement>); // 타겟의 값이 빈 문자열인 이벤트 객체를 만들어서 onChange 함수에 전달
  };

  return (
    <InputWrapper>
      <InputField
        {...props}
        type={showPassword && type === "password" ? "text" : type}
        value={value}
        onChange={onChange}
        onBlur={onBlur}
        placeholder={placeholder}
        title={title}
        error={error}
        disabled={disabled}
      />
      {!disabled && value && (
        <ClearButton onClick={handleClear}>
          &times; {/*HTML 엔티티 코드로, × (곱하기 또는 닫기 기호) 를 의미*/}
        </ClearButton>
      )}
      {!disabled && type === "password" && value && (
        <ShowPasswordButton onClick={() => setShowPassword((prev) => !prev)}>
          {showPassword ? <IoEyeSharp /> : <FaEyeSlash />}
        </ShowPasswordButton>
      )}
      {error && <ErrorMessage>{errorMessage}</ErrorMessage>}
    </InputWrapper>
  );
}

export default Input;