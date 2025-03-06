import { useEffect, useState } from "react";
import Avatar from "../../components/avatar";
import Input from "../../components/input";
import { MypageContainer, ProfileContainer, ProfileTitleContainer, ProfileTextContainer, UpdateButton, AvatarContainer } from "./mypage.styles";
import userService from "../../services/user/user.service";
import { useAuth } from "../../hooks/useAuth";
import authService from "../../services/auth/auth.service";
import { showSnackbar } from "../../components/snackbar/util";
import Profile from '../../assets/images/profile.png';

function Mypage() {
  const { nickname: userNickname, profileImage: userProfileImage, updateProfile } = useAuth();
  const [userEmail, setUserEmail] = useState<string | null>(null); // 이메일은 null일 수 있음 (선택사항)
  const [profileImage, setProfileImage] = useState<File | string | null>(null);

  const [formData, setFormData] = useState({
    nickname: '',
    email: '',
  });

  const [errors, setErrors] = useState({
    nickname: { error: false, message: '' },
    email: { error: false, message: '' },
  });

  const handleInput = (key: string, value: string) => {
    setFormData({
      ...formData,
      [key]: value,
    });
  };

  const handleImageChange = (file: File | null) => {
    if (file) {
      setProfileImage(file);
    }
  };

  const handleBlur = async (key: string): Promise<boolean> => {
    switch (key) {
      case "nickname":
        if (!formData.nickname) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임을 입력해주세요." } }));
          return false;
        }
  
        if (!/^[a-zA-Z0-9가-힣-_]{2,20}$/.test(formData.nickname)) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "닉네임은 2~20자 사이, 한글, 영어, 숫자, _,-만 사용할 수 있습니다." } }));
          return false;
        }

        if (formData.nickname === userNickname) {
          setErrors((prev) => ({ ...prev, nickname: { error: false, message: "" } }));
          return true;
        }
  
        try {
          const response: any = await authService.nicknamecheck({ nickname: formData.nickname });
          const isAvailable = response?.data?.code === 200 && response.data.is_available;
          
          setErrors((prev) => ({ 
            ...prev, 
            nickname: { error: !isAvailable, message: isAvailable ? "" : "이미 사용중인 닉네임입니다." } 
          }));
  
          return isAvailable;
        } catch (error) {
          setErrors((prev) => ({ ...prev, nickname: { error: true, message: "서버 오류가 발생했습니다." } }));
          return false;
        }
  
      case "email":
        if (!formData.email?.trim()) {
          // 이메일이 없으면 검증할 필요 없음 (null 또는 빈 문자열 허용)
          setErrors((prev) => ({ ...prev, email: { error: false, message: "" } }));
          return true;
        }

        //  이메일이 변하지 않았다면 return
        if(formData.email === userEmail) {
          setErrors((prev) => ({ ...prev, email: { error: false, message: "" } }));
          return true;
        }

        const isValidEmail = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.email || "");
        setErrors((prev) => ({ ...prev, email: { error: !isValidEmail, message: isValidEmail ? "" : "이메일 형식이 올바르지 않습니다." } }));
        
        return isValidEmail;
  
      default:
        return false;
    }
  };

  // 프로필 이미지 삭제
  const handleDelete = async () => {
    // 요청 전에 이미지만 올려놨다면 그 이미지만 삭제하고 return
    if (profileImage && profileImage !== userProfileImage) {
      setProfileImage(Profile);
      return;
    }
    // 만약 기본 이미지면 return (마지막 이름이 ...default.png인 경우)
    else if (userProfileImage?.split('/').pop() === 'default.png') {
      return;
    }
    else{

      try {
        const response: any = await userService.deleteProfileImage();
        if (response.data.code === 200) {
          showSnackbar('프로필 사진이 삭제되었습니다.');
          updateProfile(userNickname, response.data.image_url);
          setProfileImage(response.data.image_url); // 여기서 String으로 바꿔줘야 File로 인식하지 않고 계속 요청하지 못함
        }
          
      } catch (error: any) {
        // 오류 처리
      }
    }
  };

  // 회원정보 수정
  const handleUpdate = async () => {
    const nicknameValid = await handleBlur("nickname");
    const emailValid = await handleBlur("email");

    // 최신 상태의 errors를 확인하기 위해 검사 결과를 직접 사용
    if (!(nicknameValid && emailValid)) {
      return;
    }

    // 프로필 이미지 업로드
    if (profileImage instanceof File) {
      try {
        const response: any = await userService.uploadProfileImage(profileImage);
        if (response.data.code === 200) {
          showSnackbar('프로필 사진이 수정되었습니다.');
          updateProfile(userNickname, response.data.image_url);
          setProfileImage(response.data.image_url); // 여기서 String으로 바꿔줘야 File로 인식하지 않고 계속 요청하지 못함
        }
      } catch (error: any) {
        // 오류 처리
      }
    }

    let updatedFormData: any = {};

    // 닉네임이 기존 값과 다를 때만 추가
    if (formData.nickname?.trim() !== userNickname) {
      updatedFormData.nickname = formData.nickname?.trim();
    }
    
    // 원래 이메일이 없고, 새로 입력한 이메일이 없으면 return
    if ((formData.email === null || formData.email?.trim() === "") && userEmail === null) {
      // 이메일이 변하지 않음
    } else if (formData.email?.trim() !== userEmail) {
      updatedFormData.email = formData.email?.trim() === "" ? null : formData.email?.trim(); // 이메일이 비어 있으면 null로 보내기
    }

    if (Object.keys(updatedFormData).length === 0) {
      return;
    }

    try {
      const response: any = await userService.updateUserProfile(updatedFormData);
      if (response.data.code === 200) {
        showSnackbar('회원정보가 수정되었습니다.');
        updateProfile(response.data.User.nickname, response.data.User.profileImage);
      }
    } catch (error: any) {
      // 오류 처리
    };

  };

  const fetchUser = async () => {
    try {
      const response: any = await userService.getUserProfile();
      if (response.data.code === 200) {
        setFormData({
          nickname: response.data.User.nickname,
          email: response.data.User.email,
        });
        setUserEmail(response.data.User.email); 
        setProfileImage(response.data.User.profileImage); // 여기는 URL일 것
      }
    } catch (error: any) {
      // 오류 처리
    }
  };

  // 프로필 이미지 파일일 때는 URL.createObjectURL(profileImage)를 사용하고, 아닐 때는 profileImage를 그대로 사용하면 됨
  const profileImageUrl = () => {
    if (profileImage instanceof File) {
      return URL.createObjectURL(profileImage);
    }else {
      return profileImage;
    }
  }

  useEffect(() => {
    // 유효성 검사
    fetchUser();
  }, []);

  return (
    <MypageContainer>
      <ProfileTitleContainer>
        <h2>프로필</h2>
      </ProfileTitleContainer>

      <ProfileContainer>
        <AvatarContainer>
          <Avatar 
            src={profileImageUrl()}
            size={150}
            control={true}
            onChange={handleImageChange} 
            onDelete={handleDelete}
            />
        </AvatarContainer>
        <h2>{userNickname} 님</h2>
      </ProfileContainer>

      <ProfileTextContainer>
        <h4>닉네임</h4>
      </ProfileTextContainer>

      <Input
        type="text"
        value={formData.nickname}
        placeholder="닉네임을 입력해주세요."
        error={errors.nickname.error}
        errorMessage={errors.nickname.message}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInput('nickname', e.target.value)}
        onBlur={() => handleBlur('nickname')}
      />

      <ProfileTextContainer>
        <h4>이메일 (선택사항)</h4>
      </ProfileTextContainer>

      <Input
        type="text"
        value={formData.email}
        placeholder="이메일을 입력해주세요."
        error={errors.email.error}
        errorMessage={errors.email.message}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleInput('email', e.target.value)}
        onBlur={() => handleBlur('email')}
      />

      <UpdateButton onClick={handleUpdate}>회원정보 수정</UpdateButton>
    </MypageContainer>
  )
}

export default Mypage;