import { NewsContent, NewsContentWrapper, NewsImage, NewsInfoWrapper, NewsTitle, NewsWrapper, ReviewText, ReviewWrapper, ScoreNumber, ScoreText, ScoreWrapper, TimeText } from "./news.styles";
import { FaStar } from "react-icons/fa";

interface NewsProps {
    src: string;
    image_url: string;
    title: string;
    content?: string;
    hs_score: number;
    fs_score: number;
    rating: number;
    time: string;
    axis?: 'row' | 'column';
    imageHeight?: number;
    titleSize?: number;
}

function News({ src, image_url, title, content, hs_score, fs_score, rating, time, axis = 'column', imageHeight, titleSize }: NewsProps) {
    //2025-03-14T13:21:29이런 식으로 오는 형식을 바탕으로 몇 분전, 몇 일전, 몇 달전, 몇 년전으로 변환하는 함수
    const convertTime = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

        if (diffInSeconds < 60) {
            return `${diffInSeconds}초 전`;
        } else if (diffInSeconds < 3600) {
            return `${Math.floor(diffInSeconds / 60)}분 전`;
        } else if (diffInSeconds < 86400) {
            return `${Math.floor(diffInSeconds / 3600)}시간 전`;
        } else if (diffInSeconds < 2592000) {
            return `${Math.floor(diffInSeconds / 86400)}일 전`;
        } else if (diffInSeconds < 31536000) {
            return `${Math.floor(diffInSeconds / 2592000)}개월 전`;
        } else {
            return `${Math.floor(diffInSeconds / 31536000)}년 전`;
        }
    };

    return (
        <NewsWrapper to={`/news/content/${src}`} direction={axis}>
            <NewsImage src={image_url} direction={axis} $customheight={imageHeight} />
            <ReviewWrapper direction={axis}>
                {/* axis에 따라 사이즈가 달라짐 */}
                <FaStar style={{ color: "#524DD6" }}  size={axis === 'row' ? 12 : 16}/>
                <ReviewText direction={axis}>{rating}</ReviewText>
            </ReviewWrapper>
            <NewsContentWrapper>
                <NewsTitle size={titleSize} direction={axis}>{title}</NewsTitle>
                {content && <NewsContent>{content}</NewsContent>}

                <NewsInfoWrapper>
                    <ScoreWrapper>
                        <ScoreText>유사도</ScoreText>
                        <ScoreNumber>{Math.round(hs_score)}%</ScoreNumber>
                        <ScoreText>신뢰도</ScoreText>
                        <ScoreNumber>{Math.round(fs_score)}%</ScoreNumber>
                    </ScoreWrapper>

                    <TimeText>{convertTime(time)}</TimeText>
                </NewsInfoWrapper>
            </NewsContentWrapper>
        </NewsWrapper>
    );
}

export default News;