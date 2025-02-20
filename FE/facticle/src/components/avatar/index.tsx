import { useRef, useState } from 'react';
import { FaCamera } from "react-icons/fa";
import Profile from '../../assets/images/profile.png';
import { AvatarContainer, CameraContainer } from './avatar.styles';

interface AvatarProps {
  src?: string;
  alt?: string;
  size?: number;
  control?: boolean;
}

function Avatar({ src = Profile , alt = '', size = 40, control = false }: AvatarProps) {
    const [image, setImage] = useState<string>(src);
    const fileInputRef = useRef<HTMLInputElement | null>(null); // 파일 업로드 input 요소 참조

    // 파일 업로드 핸들러
    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const file = event.target.files[0];
            const newImageUrl = URL.createObjectURL(file);
            setImage(newImageUrl);
        }
    };

    // 버튼 클릭 시 파일 선택 창 열기
    const handleButtonClick = (event: React.MouseEvent) => {
        event.preventDefault();
        fileInputRef.current?.click();
    };

  return (
      <div>
          <AvatarContainer size={size}>
              <img src={image} alt={alt} style={{ width: "100%", height: "100%", objectFit: "cover" }} />
              <input
                  type="file"
                  ref={fileInputRef}
                  style={{ display: "none" }}
                  onChange={handleFileChange}
                  accept="image/*"
              />
          </AvatarContainer>
          {control && (
                <CameraContainer onClick={handleButtonClick}>
                    <FaCamera size={20} color="white" />
                </CameraContainer>
            )}
      </div>
  );
};

export default Avatar;
