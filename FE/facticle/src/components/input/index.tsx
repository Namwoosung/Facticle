import { useState } from "react";
import { IoEyeSharp } from "react-icons/io5";
import { FaEyeSlash } from "react-icons/fa";
import { InputWrapper, InputField, ErrorMessage, ClearButton, ShowPasswordButton, IconButton } from "./input.styles";

interface InputProps {
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void;
  error?: boolean;
  errorMessage?: string;
  placeholder?: string;
  title?: string;
  // 아이콘 관련 props 추가
  icon?: React.ReactNode;
  onIconClick?: () => void; // 아이콘 클릭 핸들러 추가
  disabled?: boolean;
  [key: string]: any;
}

function Input({ 
  type, 
  value, 
  onChange, 
  onBlur, 
  error, 
  errorMessage, 
  placeholder, 
  title, 
  icon, 
  onIconClick, // 아이콘 클릭 핸들러
  disabled, 
  ...props 
}: InputProps) {
  const [showPassword, setShowPassword] = useState(false);

  const handleClear = () => {
    onChange({ target: { value: "" } } as React.ChangeEvent<HTMLInputElement>);
  };

  // 아이콘 클릭 핸들러
  const handleIconClick = () => {
    if (onIconClick) {
      onIconClick(); // 전달받은 핸들러 실행
    }
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
      
      {/* 커스텀 아이콘 (Send 버튼 등) */}
      {!disabled && icon && (
        <IconButton onClick={handleIconClick}>
          {icon}
        </IconButton>
      )}
      
      {/* Clear 버튼 (X 버튼) */}
      {!disabled && value && !icon && ( // icon이 있을 때는 clear 버튼 숨김
        <ClearButton onClick={handleClear}>
          &times;
        </ClearButton>
      )}
      
      {/* 패스워드 표시/숨김 버튼 */}
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