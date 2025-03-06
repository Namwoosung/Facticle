import styled from 'styled-components';

export const MypageContainer = styled.div`
  width: 60%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  margin-top: 50px;
`;

export const ProfileContainer = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 60px;
`;

export const AvatarContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const ProfileTitleContainer = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 20px;
`;

export const ProfileTextContainer = styled.div`
  display: flex;
  align-items: center;
`;

export const ProfileInputContainer = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 20px;
`;

export const UpdateButton = styled.button`
    height: 50px;
    margin: 20px auto;
    margin-bottom: 50px;
    padding: 10px 20px;
    border: none;
    border-radius: 10px;
    background-color: #080E4B;
    color: white;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;

    &:hover {
        opacity: 0.8;
    }
`;