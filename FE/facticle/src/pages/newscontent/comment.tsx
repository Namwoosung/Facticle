import React, { useState, useEffect } from 'react';
import { IoMdSend } from "react-icons/io";
import { useNavigate } from 'react-router-dom';
import Input from "../../components/input";
import Dialog from "../../components/dialog";
import { useAuth } from "../../hooks/useAuth";
import {
  Comment,
  CommentsWrapper,
  CommentTextWrapper,
  CommentTItleNumber,
  CommentTitleWrapper,
  CommentWrapper,
  UserCommentWrapper,
  CommentTextTitleWrapper,
  CommentTextTitle,
  CommentTextDate,
  CommentThumbsDownButton,
  CommentThumbsUpButton,
  CommentText,
  CommentReplyButton,
  CommentContainer,
  ReplyWrapper,
  ReplyContainer,
  ReplyInputWrapper,
  LoginOverlay,
  LoginOverlayTitle,
  LoginOverlayText,
  CommentsContainer,
  EmptyCommentsMessage,
  CommentActionsWrapper,
  CommentActionButton,
  ReplyActionButton,
  ReplyActionsWrapper,
  CommentEditWrapper,
  CommentEditButtonsWrapper,
  CommentEditButton,
  ReplyEditWrapper,
  ReplyEditButtonsWrapper,
  ReplyEditButton,
} from "./comment.styles";
import commentsService from '../../services/news/comments.service';

interface Reply {
  commentId: number;
  userId: number;
  newsId: number;
  nickname: string;
  content: string;
  likeCount: number;
  hateCount: number;
  createdAt: string;
  updatedAt: string;
  parentCommentId: number;
  replies: Reply[];
}

interface CommentData {
  commentId: number;
  userId: number;
  newsId: number;
  nickname: string;
  content: string;
  likeCount: number;
  hateCount: number;
  createdAt: string;
  updatedAt: string;
  parentCommentId: number | null;
  replies: Reply[];
}

interface CommentInteraction {
  commentInteractionId: number;
  userId: number;
  commentId: number;
  reaction: 'LIKE' | 'HATE';
  reactionAt: string;
}

interface ApiResponse {
  success: boolean;
  data: {
    code: number;
    commentInteraction: CommentInteraction[];
    comment: CommentData[];
    isUser: boolean;
  };
  message: string;
}

interface NewsCommentProps {
  newsId: string;
}

function NewsComment({ newsId }: NewsCommentProps) {
  const navigate = useNavigate();
  const { isAuthenticated, nickname } = useAuth();
  const [comments, setComments] = useState<CommentData[]>([]);
  const [commentInteractions, setCommentInteractions] = useState<CommentInteraction[]>([]);
  const [newComment, setNewComment] = useState('');
  const [replyInputs, setReplyInputs] = useState<{ [key: string]: string }>({});
  const [showReplyInput, setShowReplyInput] = useState<{ [key: string]: boolean }>({});
  const [loading, setLoading] = useState(false);
  const [showLoginDialog, setShowLoginDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deleteCommentId, setDeleteCommentId] = useState<number | null>(null);
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editCommentContent, setEditCommentContent] = useState('');

  const isLoggedIn = isAuthenticated;

  const dummyComments: CommentData[] = [
    {
      commentId: 1,
      userId: 1,
      newsId: parseInt(newsId),
      nickname: "사용자1",
      content: "이 뉴스 정말 흥미롭네요! 자세한 내용이 궁금합니다.",
      likeCount: 12,
      hateCount: 2,
      createdAt: "2025-06-07T15:30:00",
      updatedAt: "2025-06-07T15:30:00",
      parentCommentId: null,
      replies: []
    },
    {
      commentId: 2,
      userId: 2,
      newsId: parseInt(newsId),
      nickname: "뉴스독자",
      content: "좋은 정보 감사합니다. 공유해주셔서 고맙네요.",
      likeCount: 8,
      hateCount: 0,
      createdAt: "2025-06-07T16:45:00",
      updatedAt: "2025-06-07T16:45:00",
      parentCommentId: null,
      replies: [
        {
          commentId: 3,
          userId: 3,
          newsId: parseInt(newsId),
          nickname: "댓글러",
          content: "저도 같은 생각입니다!",
          likeCount: 3,
          hateCount: 0,
          createdAt: "2025-06-07T17:00:00",
          updatedAt: "2025-06-07T17:00:00",
          parentCommentId: 2,
          replies: []
        }
      ]
    },
    {
      commentId: 4,
      userId: 4,
      newsId: parseInt(newsId),
      nickname: "분석가",
      content: "이런 관점에서 생각해보지 못했는데, 새로운 시각을 얻었습니다.",
      likeCount: 15,
      hateCount: 1,
      createdAt: "2025-06-07T18:20:00",
      updatedAt: "2025-06-07T18:20:00",
      parentCommentId: null,
      replies: []
    }
  ];

  const getUserReaction = (commentId: number): 'LIKE' | 'HATE' | null => {
    if (!isLoggedIn) return null;
    
    const interaction = commentInteractions.find(
      interaction => interaction.commentId === commentId
    );
    return interaction ? interaction.reaction : null;
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchComments();
    } else {
      setComments(dummyComments);
    }
  }, [newsId, isLoggedIn]);

  const fetchComments = async () => {
    try {
      setLoading(true);
      const response = await commentsService.getComments(newsId) as ApiResponse;
      
      if (response.success && response.data.code === 200) {
        setComments(response.data.comment || []);
        setCommentInteractions(response.data.commentInteraction || []);
      } else {
        setComments([]);
        setCommentInteractions([]);
      }
    } catch (error) {
      setComments([]);
      setCommentInteractions([]);
    } finally {
      setLoading(false);
    }
  };

  const isMyComment = (commentNickname: string): boolean => {
    if (!isLoggedIn) return false;
    if (!nickname) return false;
    return nickname === commentNickname;
  };

  const startEditComment = (commentId: number, content: string) => {
    setEditingCommentId(commentId);
    setEditCommentContent(content);
  };

  const cancelEditComment = () => {
    setEditingCommentId(null);
    setEditCommentContent('');
  };

  const handleEditComment = async (commentId: number) => {
    if (!editCommentContent.trim()) return;

    try {
      await commentsService.updateComment(newsId, commentId.toString(), editCommentContent);
      setEditingCommentId(null);
      setEditCommentContent('');
      fetchComments();
    } catch (error) {
      // Handle error silently or show user-friendly message
    }
  };

  const openDeleteDialog = (commentId: number) => {
    setDeleteCommentId(commentId);
    setShowDeleteDialog(true);
  };

  const closeDeleteDialog = () => {
    setDeleteCommentId(null);
    setShowDeleteDialog(false);
  };

  const confirmDeleteComment = async () => {
    if (deleteCommentId === null) return;

    try {
      await commentsService.deleteComment(newsId, deleteCommentId.toString());
      closeDeleteDialog();
      fetchComments();
    } catch (error) {
      closeDeleteDialog();
    }
  };

  const handleCommentSubmit = async () => {
    checkLoginAndProceed(async () => {
      if (!newComment.trim()) return;

      try {
        await commentsService.postComment(newsId, newComment);
        setNewComment('');
        fetchComments();
      } catch (error) {
        // Handle error silently or show user-friendly message
      }
    });
  };

  const handleCommentInputClick = () => {
    if (!isLoggedIn) {
      setShowLoginDialog(true);
    }
  };

  const handleLikeComment = async (commentId: number) => {
    checkLoginAndProceed(async () => {
      try {
        const userReaction = getUserReaction(commentId);
        
        if (userReaction === 'LIKE') {
          await commentsService.deleteLikeComment(commentId.toString());
        } else {
          await commentsService.likeComment(commentId.toString());
        }
        
        fetchComments();
      } catch (error) {
        // Handle error silently or show user-friendly message
      }
    });
  };

  const handleHateComment = async (commentId: number) => {
    checkLoginAndProceed(async () => {
      try {
        const userReaction = getUserReaction(commentId);
        
        if (userReaction === 'HATE') {
          await commentsService.deleteHateComment(commentId.toString());
        } else {
          await commentsService.hateComment(commentId.toString());
        }
        
        fetchComments();
      } catch (error) {
        // Handle error silently or show user-friendly message
      }
    });
  };

  const toggleReplyInput = (commentId: number) => {
    checkLoginAndProceed(() => {
      setShowReplyInput(prev => ({
        ...prev,
        [commentId]: !prev[commentId]
      }));
    });
  };

  const handleReplyInputChange = (commentId: number, value: string) => {
    setReplyInputs(prev => ({
      ...prev,
      [commentId]: value
    }));
  };

  const handleReplySubmit = async (commentId: number) => {
    checkLoginAndProceed(async () => {
      const replyContent = replyInputs[commentId];
      if (!replyContent?.trim()) return;

      try {
        await commentsService.postReply(newsId, commentId.toString(), replyContent);
        setReplyInputs(prev => ({ ...prev, [commentId]: '' }));
        setShowReplyInput(prev => ({ ...prev, [commentId]: false }));
        fetchComments();
      } catch (error) {
        // Handle error silently or show user-friendly message
      }
    });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
  };

  const checkLoginAndProceed = (action: () => void) => {
    if (!isLoggedIn) {
      setShowLoginDialog(true);
      return;
    }
    action();
  };

  const handleLoginConfirm = () => {
    setShowLoginDialog(false);
    navigate('/login');
  };

  const handleLoginDialogClose = () => {
    setShowLoginDialog(false);
  };

  if (loading) {
    return <div>댓글을 불러오는 중...</div>;
  }

  return (
    <>
      <UserCommentWrapper>
        <CommentWrapper>
          <CommentTitleWrapper>
            <h3>댓글</h3>
            <CommentTItleNumber>{isLoggedIn ? comments.length : '?'}</CommentTItleNumber>
          </CommentTitleWrapper>
        </CommentWrapper>
        <Input
          type="text"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          onClick={handleCommentInputClick}
          placeholder={isLoggedIn ? "댓글을 입력하세요" : "로그인 후 댓글을 작성할 수 있습니다"}
          onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleCommentSubmit()}
          icon={
            <IoMdSend 
              size={30} 
              color="#ccc" 
              style={{ cursor: 'pointer' }}
            />
          }
          onIconClick={handleCommentSubmit}
          readOnly={!isLoggedIn}
        />
      </UserCommentWrapper>

      <CommentsWrapper>
        {!isLoggedIn && (
          <LoginOverlay onClick={() => setShowLoginDialog(true)}>
            <LoginOverlayTitle>
              🔒 댓글을 보려면 로그인이 필요합니다
            </LoginOverlayTitle>
            <LoginOverlayText>
              클릭하여 로그인하기
            </LoginOverlayText>
          </LoginOverlay>
        )}

        <CommentsContainer $isLoggedIn={isLoggedIn}>
          {isLoggedIn && comments.length === 0 && (
            <EmptyCommentsMessage>
              <p>아직 댓글이 없습니다.</p>
              <p>첫 번째 댓글을 작성해보세요!</p>
            </EmptyCommentsMessage>
          )}

          {comments.map((comment) => {
            const userReaction = getUserReaction(comment.commentId);
            const isEditing = editingCommentId === comment.commentId;
            const isOwner = isMyComment(comment.nickname);
            
            return (
              <Comment key={comment.commentId}>
                <CommentContainer>
                  <CommentTextWrapper>
                    <CommentTextTitleWrapper>
                      <CommentTextTitle>{comment.nickname}</CommentTextTitle>
                      <CommentTextDate>{formatDate(comment.createdAt)}</CommentTextDate>
                      
                      {isLoggedIn && isOwner && (
                        <CommentActionsWrapper>
                          {!isEditing && (
                            <>
                              <CommentActionButton
                                onClick={() => startEditComment(comment.commentId, comment.content)}
                              >
                                수정
                              </CommentActionButton>
                              <CommentActionButton
                                $isDelete
                                onClick={() => openDeleteDialog(comment.commentId)}
                              >
                                삭제
                              </CommentActionButton>
                            </>
                          )}
                        </CommentActionsWrapper>
                      )}
                      
                      <CommentThumbsUpButton 
                        size={20} 
                        onClick={() => handleLikeComment(comment.commentId)}
                        style={{ 
                          color: userReaction === 'LIKE' ? '#007bff' : '#ccc',
                          cursor: 'pointer'
                        }}
                      />
                      <CommentTextDate>{comment.likeCount}</CommentTextDate>
                      <CommentThumbsDownButton 
                        size={20} 
                        onClick={() => handleHateComment(comment.commentId)}
                        style={{ 
                          color: userReaction === 'HATE' ? '#dc3545' : '#ccc',
                          cursor: 'pointer'
                        }}
                      />
                      <CommentTextDate>{comment.hateCount}</CommentTextDate>
                    </CommentTextTitleWrapper>
                    
                    {isEditing ? (
                      <CommentEditWrapper>
                        <Input
                          type="text"
                          value={editCommentContent}
                          onChange={(e) => setEditCommentContent(e.target.value)}
                          placeholder="댓글을 수정하세요"
                          onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleEditComment(comment.commentId)}
                        />
                        <CommentEditButtonsWrapper>
                          <CommentEditButton
                            $isPrimary
                            onClick={() => handleEditComment(comment.commentId)}
                          >
                            저장
                          </CommentEditButton>
                          <CommentEditButton onClick={cancelEditComment}>
                            취소
                          </CommentEditButton>
                        </CommentEditButtonsWrapper>
                      </CommentEditWrapper>
                    ) : (
                      <CommentText>{comment.content}</CommentText>
                    )}
                  </CommentTextWrapper>
                  
                  {!isEditing && (
                    <CommentReplyButton onClick={() => toggleReplyInput(comment.commentId)}>
                      답글 쓰기
                    </CommentReplyButton>
                  )}

                  {comment.replies?.map((reply) => {
                    const isReplyEditing = editingCommentId === reply.commentId;
                    const isReplyOwner = isMyComment(reply.nickname);
                    
                    return (
                      <ReplyWrapper key={reply.commentId}>
                        <ReplyContainer>
                          <CommentTextTitleWrapper>
                            <CommentTextTitle>{reply.nickname}</CommentTextTitle>
                            <CommentTextDate>{formatDate(reply.createdAt)}</CommentTextDate>
                            
                            {isLoggedIn && isReplyOwner && (
                              <ReplyActionsWrapper>
                                {!isReplyEditing && (
                                  <>
                                    <ReplyActionButton
                                      onClick={() => startEditComment(reply.commentId, reply.content)}
                                    >
                                      수정
                                    </ReplyActionButton>
                                    <ReplyActionButton
                                      $isDelete
                                      onClick={() => openDeleteDialog(reply.commentId)}
                                    >
                                      삭제
                                    </ReplyActionButton>
                                  </>
                                )}
                              </ReplyActionsWrapper>
                            )}
                          </CommentTextTitleWrapper>
                          
                          {isReplyEditing ? (
                            <ReplyEditWrapper>
                              <Input
                                type="text"
                                value={editCommentContent}
                                onChange={(e) => setEditCommentContent(e.target.value)}
                                placeholder="답글을 수정하세요"
                                onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleEditComment(reply.commentId)}
                              />
                              <ReplyEditButtonsWrapper>
                                <ReplyEditButton
                                  $isPrimary
                                  onClick={() => handleEditComment(reply.commentId)}
                                >
                                  저장
                                </ReplyEditButton>
                                <ReplyEditButton onClick={cancelEditComment}>
                                  취소
                                </ReplyEditButton>
                              </ReplyEditButtonsWrapper>
                            </ReplyEditWrapper>
                          ) : (
                            <CommentText>{reply.content}</CommentText>
                          )}
                        </ReplyContainer>
                      </ReplyWrapper>
                    );
                  })}

                  {showReplyInput[comment.commentId] && (
                    <ReplyInputWrapper>
                      <Input
                        type="text"
                        value={replyInputs[comment.commentId] || ''}
                        onChange={(e) => handleReplyInputChange(comment.commentId, e.target.value)}
                        placeholder="답글을 입력하세요"
                        onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleReplySubmit(comment.commentId)}
                        icon={
                          <IoMdSend 
                            size={24} 
                            color="#ccc" 
                            style={{ cursor: 'pointer' }}
                          />
                        }
                        onIconClick={() => handleReplySubmit(comment.commentId)}
                      />
                    </ReplyInputWrapper>
                  )}
                </CommentContainer>
              </Comment>
            );
          })}
        </CommentsContainer>
      </CommentsWrapper>

      <Dialog
        open={showLoginDialog}
        title="로그인 필요"
        content={
          <div>
            <p>로그인이 필요한 기능입니다.</p>
            <p>로그인 페이지로 이동하시겠습니까?</p>
          </div>
        }
        onClose={handleLoginDialogClose}
        onConfirm={handleLoginConfirm}
        confirmText="로그인하러 가기"
        cancelText="취소"
      />

      <Dialog
        open={showDeleteDialog}
        title="댓글 삭제"
        content={
          <div>
            <p>정말로 이 댓글을 삭제하시겠습니까?</p>
            <p style={{ color: '#666', fontSize: '14px' }}>삭제된 댓글은 복구할 수 없습니다.</p>
          </div>
        }
        onClose={closeDeleteDialog}
        onConfirm={confirmDeleteComment}
        confirmText="삭제"
        cancelText="취소"
      />
    </>
  );
}

export default NewsComment;