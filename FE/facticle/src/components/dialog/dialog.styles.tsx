import styled from 'styled-components';

export const DialogOverlay = styled.div<{ open: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  display: ${({ open }) => (open ? 'flex' : 'none')};
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

export const DialogContainer = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
`;

export const DialogHeader = styled.div`
  h2 {
    margin: 0;
    font-size: 18px;
  }
  display: flex;
  justify-content: center;
`;

export const DialogContent = styled.div`
  margin: 20px 0;
`;

export const DialogActions = styled.div`
  display: flex;
  justify-content: center;
  gap: 10px;
`;

export const ConfirmButton = styled.button`
  padding: 8px 16px;
  border: none;
  background-color: black;
  color: white;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #333;
  }

  &:focus {
    outline: none;
  }
`;