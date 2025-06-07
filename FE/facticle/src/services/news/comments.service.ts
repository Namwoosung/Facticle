import htttpService from "../htttp.service";

class CommentsService {
  getComments = async (newsId: string) => { //뉴스 리스트 조회
    const endpoint = `/news/${newsId}/comments`;
    return await htttpService.get(endpoint);
  };

  postComment = async (newsId: string, content: string) => { //뉴스 댓글 작성
    const endpoint = `/news/${newsId}/comment`;
    return await htttpService.post(endpoint, { content });
  };

  updateComment = async (newsId: string, commentId: string, content: string) => { // 뉴스 댓글 수정
    const endpoint = `/news/${newsId}/comment/${commentId}`;
    return await htttpService.patch(endpoint, { content });
  }

  deleteComment = async (newsId: string, commentId: string) => { // 뉴스 댓글 삭제
    const endpoint = `/news/${newsId}/comment/${commentId}`;
    return await htttpService.delete(endpoint);
  };

  postReply = async (newsId: string, commentId: string, content: string) => { // 뉴스 댓글 답글 작성
    const endpoint = `/news/${newsId}/comment/${commentId}`;
    return await htttpService.post(endpoint, { content });
  }

  likeComment = async (commentId: string) => { // 뉴스 댓글 좋아요
    const endpoint = `/news/comment/${commentId}/like`;
    return await htttpService.post(endpoint);
  }

  deleteLikeComment = async (commentId: string) => { // 뉴스 댓글 좋아요 취소
    const endpoint = `/news/comment/${commentId}/like`;
    return await htttpService.delete(endpoint);
  }

  hateComment = async (commentId: string) => { // 뉴스 댓글 싫어요
    const endpoint = `/news/comment/${commentId}/hate`;
    return await htttpService.post(endpoint);
  }

  deleteHateComment = async (commentId: string) => { // 뉴스 댓글 싫어요 취소
    const endpoint = `/news/comment/${commentId}/hate`;
    return await htttpService.delete(endpoint);
  }

}

export default new CommentsService();