import styled from 'styled-components';

export const AvatarContainer = styled.div<{ size: number, borderStyle: string }>`
  width: ${({ size }) => size}px;
  height: ${({ size }) => size}px;
  border-radius: 50%; /* borderRadius -> border-radius로 수정 */
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f0f0;
  font-size: ${({ size }) => size / 3}px;
  border: 1px ${({ borderStyle }) => borderStyle} black;
  color: #fff;
`;

export const AvatarImage = styled.img<{ size: number }>`
  width: ${({ size }) => size - 5}px;
  height: ${({ size }) => size - 5}px;
  border-radius: 50%;
  object-fit: cover;
`;

export const CameraContainer = styled.div`
  position: relative;
  bottom: 40px;
  left: 110px;
  background: black;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.2);
  cursor: pointer;
`;
