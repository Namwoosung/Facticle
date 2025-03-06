import { useEffect, useRef, useState } from 'react';
import { FaCamera } from "react-icons/fa";
import Profile from '../../assets/images/profile.png';
import { AvatarContainer, AvatarImage, CameraContainer, DeleteButton } from './avatar.styles';
import Dialog from '../dialog';

interface AvatarProps {
  src?: string | null;
  alt?: string;
  size?: number;
  control?: boolean;
  onChange?: (file: File | null) => void;
  onDelete?: () => void;
}

function Avatar({ src , alt = '', size = 40, control = false, onChange, onDelete }: AvatarProps) {
  const [image, setImage] = useState<string>(src || Profile);
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    console.log("event",event.target.files);
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      onChange?.(file);
      // 기존 URL 해제
      if (image.startsWith("blob:")) {
        URL.revokeObjectURL(image);
      }

      const newImageUrl = URL.createObjectURL(file);
      setImage(newImageUrl + "#" + Date.now()); // 캐시 방지
      setDialogOpen(false); // 파일 선택 후 Dialog 닫기
    }
  };

  // 프로필 삭제
  const handleDelete = () => {
    // ref 초기화
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
    onDelete?.();
  };

  const handleButtonClick = (event: React.MouseEvent) => {
    event.preventDefault();
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  const handleFileSelect = () => {
    fileInputRef.current?.click();
  };

  useEffect(() => {
    if (src) {
      setImage(src);
    }
  }, [src]);

  return (
    <div>
      <AvatarContainer size={size} $borderStyle={control ? 'dotted' : 'none'}>
        <AvatarImage size={control ? size : size + 5} src={image} alt={alt} />
      </AvatarContainer>

      {control && (
        <CameraContainer onClick={handleButtonClick}>
          <FaCamera size={20} color="white" />
        </CameraContainer>
      )}

      { control &&
        <DeleteButton onClick={handleDelete}>삭제</DeleteButton>
      }

      <Dialog
        open={dialogOpen}
        onClose={handleDialogClose}
        title="사진 업로드 안내"
        content={
          <div>
            <p>사진을 업로드하기 전에 다음 형식을 확인해주세요:</p>
            <ul>
              <li>최대 크기: 5MB</li>
              <li>허용된 형식: PNG, JPEG</li>
            </ul>
            <button onClick={handleFileSelect}>파일 선택</button>
            <input
              type="file"
              ref={fileInputRef}
              style={{ display: "none" }}
              onChange={handleFileChange}
              accept="image/png, image/jpeg"
            />
          </div>
        }
        confirmText="닫기"
      />
    </div>
  );
}

export default Avatar;
