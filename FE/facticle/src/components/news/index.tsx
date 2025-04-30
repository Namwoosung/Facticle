import { NewsContent, NewsContentWrapper, NewsImage, NewsInfoWrapper, NewsTitle, NewsWrapper,ReviewWrapper,ScoreNumber, ScoreText, ScoreWrapper, TimeText } from "./news.styles";
import { FaStar } from "react-icons/fa";

interface NewsProps {
    src: string;
    image_url: string;
    title: string;
    content?: string;
    hs_score: number;
    fs_score: number;
    rating: number;
    axis?: 'row' | 'column';
}

function News({ src, image_url, title, content, hs_score, fs_score, rating, axis = 'column' }: NewsProps) {
    return (
        <NewsWrapper to={`/news/${src}`} direction={axis}>
            <NewsImage src={image_url} direction={axis} />
            <ReviewWrapper>
            <FaStar style={{ color: "#524DD6" }} />
                <ScoreText>{rating}</ScoreText>
            </ReviewWrapper>
            <NewsContentWrapper>
                <NewsTitle>{title}</NewsTitle>
                {content && <NewsContent>{content}</NewsContent>}
                
                <NewsInfoWrapper>
                    <ScoreWrapper>
                        <ScoreText>유사도</ScoreText>
                        <ScoreNumber>{hs_score}%</ScoreNumber>
                        <ScoreText>신뢰도</ScoreText>
                        <ScoreNumber>{fs_score}%</ScoreNumber>
                    </ScoreWrapper>

                    <TimeText>{5}분 전</TimeText>
                </NewsInfoWrapper>
            </NewsContentWrapper>
        </NewsWrapper>
    );
}

export default News;