import styled from "styled-components";
import { PiThumbsUpLight, PiThumbsDownLight } from "react-icons/pi";

export const CommentWrapper = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 0 10px;
    margin-bottom: 20px;
`;

export const CommentTitleWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
`;

export const CommentTItleNumber = styled.p`
    font-size: 16px;
    font-weight: 600;
    color: grey;
`;

export const UserCommentWrapper = styled.div`
    width: 100%;
    margin-top: 20px;
    align-self: end;
`;

export const CommentsWrapper = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 30px;
    box-sizing: border-box;
    position: relative;
`;

export const LoginOverlay = styled.div`
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 10;
    background-color: rgba(255, 255, 255, 0.95);
    padding: 30px 40px;
    text-align: center;
    border-radius: 12px;
    border: 2px dashed #ddd;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

export const LoginOverlayTitle = styled.h3`
    color: #333;
    margin-bottom: 10px;
    font-size: 18px;
`;

export const LoginOverlayText = styled.p`
    color: #666;
    font-size: 14px;
    margin: 0;
`;

export const CommentsContainer = styled.div<{ $isLoggedIn: boolean }>`
    filter: ${props => props.$isLoggedIn ? 'none' : 'blur(4px)'};
    pointer-events: ${props => props.$isLoggedIn ? 'auto' : 'none'};
    opacity: ${props => props.$isLoggedIn ? 1 : 0.4};
    transition: all 0.3s ease;
`;

export const EmptyCommentsMessage = styled.div`
    text-align: center;
    padding: 40px 20px;
    color: #666;
    font-size: 16px;
    
    p:first-child {
        margin-bottom: 8px;
    }
    
    p:last-child {
        font-size: 14px;
        margin-top: 8px;
    }
`;

export const Comment = styled.div`
    width: 100%;
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    box-sizing: border-box;
`;

export const CommentContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
`;

export const CommentTextWrapper = styled.div`
    height: fit-content;
    min-height: 40px;
    display: flex;
    flex-direction: column;
    gap: 5px;
    background-color: #F5F5F5;
    padding: 15px 20px;
    border-radius: 10px;
`;

export const CommentTextTitleWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 5px;
`;

export const CommentTextTitle = styled.p`
    font-size: 16px;
    font-weight: 600;
    color: black;
    margin-right: 5px;
`;

export const CommentTextDate = styled.p`
    font-size: 14px;
    color: #999999;
    margin-right: 5px;
`;

export const CommentActionsWrapper = styled.div`
    margin-left: auto;
    display: flex;
    gap: 8px;
`;

export const CommentActionButton = styled.button<{ $isDelete?: boolean }>`
    background: none;
    border: none;
    color: ${props => props.$isDelete ? '#dc3545' : '#666'};
    cursor: pointer;
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 4px;
    
    &:hover {
        background-color: ${props => props.$isDelete ? '#f8d7da' : '#f0f0f0'};
    }
`;

export const ReplyActionButton = styled.button<{ $isDelete?: boolean }>`
    background: none;
    border: none;
    color: ${props => props.$isDelete ? '#dc3545' : '#666'};
    cursor: pointer;
    font-size: 11px;
    padding: 2px 6px;
    border-radius: 3px;
    
    &:hover {
        background-color: ${props => props.$isDelete ? '#f8d7da' : '#f0f0f0'};
    }
`;

export const CommentThumbsUpButton = styled(PiThumbsUpLight)`
    cursor: pointer;
    border-radius: 50%;
    padding: 5px;
    margin: 0;
    &:hover {
        background-color: #E2E2E2;
    }
`;

export const CommentThumbsDownButton = styled(PiThumbsDownLight)`
    cursor: pointer;
    border-radius: 50%;
    padding: 5px;
    margin: 0;
    &:hover {
        background-color: #E2E2E2;
    }
`;

export const CommentText = styled.p`
    font-size: 16px;
    color: black;
    margin-top: 5px;
    text-align: left;
`;

export const CommentEditWrapper = styled.div`
    margin-top: 10px;
`;

export const CommentEditButtonsWrapper = styled.div`
    margin-top: 8px;
    display: flex;
    gap: 8px;
`;

export const CommentEditButton = styled.button<{ $isPrimary?: boolean }>`
    padding: 6px 12px;
    background-color: ${props => props.$isPrimary ? '#080E4B' : '#A4A4A4'};
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
`;

export const ReplyEditWrapper = styled.div`
    margin-top: 8px;
`;

export const ReplyEditButtonsWrapper = styled.div`
    margin-top: 6px;
    display: flex;
    gap: 6px;
`;

export const ReplyEditButton = styled.button<{ $isPrimary?: boolean }>`
    padding: 4px 8px;
    background-color: ${props => props.$isPrimary ? '#080E4B' : '#A4A4A4'};
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    font-size: 11px;
`;

export const CommentReplyButton = styled.button`
    width: fit-content;
    font-size: 14px;
    font-weight: 600;
    margin-left: 20px;    
    border: none;
    border-radius: 10px;
    background-color: white;
    text-align: left;
`;

export const ReplyWrapper = styled.div`
  width: 100%-55px;
  display: flex;
  gap: 15px;
  margin-top: 10px;
  margin-left: 55px; /* 원 댓글보다 들여쓰기 */
`;

export const ReplyContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 5px;
  background-color: #f5f5f5;
  padding: 10px 15px;
  border-radius: 10px;
`;

export const ReplyActionsWrapper = styled.div`
    margin-left: auto;
    display: flex;
    gap: 6px;
`;

export const ReplyInputWrapper = styled.div`
  margin-left: 55px;
  margin-top: 8px;
`;