import { useEffect, useRef } from "react";
import { FilterDialogWrapper } from "./filterdialog.styles";

interface FilterDialogProps {
  open: boolean;
  onClose: () => void;
  children: React.ReactNode;
}

function FilterDialog({ open, onClose, children }: FilterDialogProps) {
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
    <div ref={wrapperRef} style={{ position: "relative", zIndex: 1000 }}>
      <FilterDialogWrapper open={open}>
        {children}
      </FilterDialogWrapper>
    </div>
  );
}

export default FilterDialog;
