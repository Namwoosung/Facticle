import { ProgressFill, ProgressWrapper } from "./progress.styles";

interface ProgressProps {
  progress: number; // 진행률 (0 ~ 100)
  color?: string; // 진행 바 색상 (기본값: 파란색)
}

function Progress({ progress, color = "black" }: ProgressProps) {
  return (
    <ProgressWrapper>
      <ProgressFill style={{ width: `${progress}%`, backgroundColor: color }} />
    </ProgressWrapper>
  );
}

export default Progress;
