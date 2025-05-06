import styled from "styled-components";
import { PiThumbsUpLight, PiThumbsDownLight } from "react-icons/pi";

export const Wrapper = styled.div`
    width: 1280px;
    margin: 0 auto;
    display: flex;
    margin-top: 80px;
    gap: 50px;
`;

export const SideWrapper = styled.div`
    width: 30%;
    position: sticky;
    top: 0;
    max-height: fit-content;
    margin-bottom: 20px;
`;

export const NewsContentWrapper = styled.div`
    width: 70%;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
`;

export const NewsContentHeader = styled.div`
    display: flex;
    flex-direction: column;
    gap: 10px;
`;

export const NewsContentTitle = styled.h1`
    font-size: 30px;
    font-weight: bold;
`;

export const NewsContentInfo = styled.div`
    width: 100%;
    display: flex;
    align-items: center;
    margin-top: 10px;
`;

export const NewsContentDate = styled.span`
    font-size: 14px;
    color: #999999;
`;

export const InfoLine = styled.span`
    width: 1px;
    height: 14px;
    background-color: #999999;
    display: block;
    margin: 0 15px;
`;

export const ScoreWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
`;

export const ScoreContainer = styled.div`
    width: 300px;
    display: flex;
    align-items: center;
    gap: 5px;
`;

export const ScoreText = styled.p`
  width: 100px;
  font-size: 16px;
  font-weight: 600;
  color: black;
`;

export const StarContainer = styled.span`
    width: fit-content;
    padding: 5px 10px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
    background-color: #E2E2E2;
`;

export const StarText = styled.p`
    font-size: 16px;
    font-weight: 600;
    color: black;
    margin-left: 5px;
`;

export const NewsContentImg = styled.img`
    width: 100%;
    height: auto;
`;

export const NewsContentBody = styled.div`
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 30px;
`;

export const NewsContentBodyTextWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 20px;
`;

export const NewsContentBodySubText = styled.p`
    font-size: 14px;
    color: #999999;
    text-align: left;
`;

export const NewsContentBodyText = styled.div`
    font-size: 16px;
    font-weight: 600;
    background-color: #E2E2E2;
    padding: 30px 20px;
    border-radius: 10px;
`;

export const NewsContentLine = styled.div`
    width: 100%;
    height: 0.5px;
    background-color: #D9D9D9;
    margin: 20px 0;
`;

export const ReviewWrapper = styled.div`
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    gap: 20px;
`;

export const StarWrapper = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 30px;
`;

export const ConfiremButton = styled.button`
    width: 50%;
    height: 50px;
    border-radius: 10px;
    border: 1px solid #E2E2E2;
    background-color: #fff;
    font-size: 16px;
    font-weight: 600;
    color: black;
    cursor: pointer;
    margin: 0 auto;
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
`;

export const CommentWrapper = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 0 10px;
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

export const ReplyInputWrapper = styled.div`
  margin-left: 55px;
  margin-top: 8px;
`;
