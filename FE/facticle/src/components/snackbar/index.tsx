import { useEffect, useState, ReactNode } from "react";
import { createPortal } from "react-dom";
import { FaCheckCircle } from "react-icons/fa";
import styled from "styled-components";


interface SnackbarProps {
  message: string;
  icon?: ReactNode;
}

export default function Snackbar({ message, icon= <FaCheckCircle size={20} color="green"/> }: SnackbarProps) {
  const [visible, setVisible] = useState(true);
  const duration = 3000;

  useEffect(() => {
    const timer = setTimeout(() => setVisible(false), duration);
    return () => clearTimeout(timer);
  }, [duration]);

  if (!visible) return null;

    return createPortal(
        <StyledSnackbar>
            <IconWrapper>
                {icon}
            </IconWrapper>
            {message}
        </StyledSnackbar>
        , document.body);
}

const StyledSnackbar = styled.div`
  position: fixed;
  bottom: 20px;
  left: 50%;
  display: flex;
  align-items: center;
  transform: translateX(-50%);
  padding: 10px 15px;
  background-color: #333;
  color: white;
  font-size: 14px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: opacity 0.3s ease-in-out;
  opacity: 1;
`;

const IconWrapper = styled.div`
    margin-right: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
`;
