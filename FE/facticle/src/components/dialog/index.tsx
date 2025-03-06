import React from 'react';
import { DialogOverlay, DialogContainer, DialogHeader, DialogContent, DialogActions, ConfirmButton } from './dialog.styles';

interface DialogProps {
  open: boolean;
  title: string;
  content: React.ReactNode;
  onClose: () => void;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
}

const Dialog: React.FC<DialogProps> = ({ 
  open, 
  title, 
  content, 
  onClose, 
  onConfirm, 
  confirmText = "확인", 
  cancelText = "닫기" 
}) => {
  return (
    <DialogOverlay open={open} onClick={onClose}>
      <DialogContainer onClick={(e) => e.stopPropagation()}>
        <DialogHeader>
          <h2>{title}</h2>
        </DialogHeader>
        <DialogContent>{content}</DialogContent>
        <DialogActions>
          <ConfirmButton onClick={onClose}>{cancelText}</ConfirmButton>
          {onConfirm && <ConfirmButton onClick={onConfirm}>{confirmText}</ConfirmButton>}
        </DialogActions>
      </DialogContainer>
    </DialogOverlay>
  );
};

export default Dialog;
