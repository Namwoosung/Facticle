import { useEffect, useRef } from "react";
import { FilterDialogWrapper } from "./filterdialog.styles";

interface FilterDialogProps {
  open: boolean;
  onClose: () => void;
  children: React.ReactNode;
  hasSlider?: boolean; // 슬라이더가 있는지 여부
}

function FilterDialog({ open, onClose, children, hasSlider = false }: FilterDialogProps) {
  const wrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        onClose();
      }
    };

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <FilterDialogWrapper open={open} hasSlider={hasSlider} ref={wrapperRef}>
      {children}
    </FilterDialogWrapper>
  );
}

export default FilterDialog;
