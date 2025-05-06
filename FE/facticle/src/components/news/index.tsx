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
    axis?: 'row' | 'column';
    imageHeight?: number;
    titleSize?: number;
}

function News({ src, image_url, title, content, hs_score, fs_score, rating, axis = 'column', imageHeight, titleSize }: NewsProps) {
    return (
        <NewsWrapper to={`/news/content/${src}`} direction={axis}>
            <NewsImage src={image_url} direction={axis} $customheight={imageHeight} />
            <ReviewWrapper direction={axis}>
                {/* axis에 따라 사이즈가 달라짐 */}
                <FaStar style={{ color: "#524DD6" }}  size={axis === 'row' ? 12 : 16}/>
                <ReviewText direction={axis}>{rating}</ReviewText>
            </ReviewWrapper>
            <NewsContentWrapper>
                <NewsTitle size={titleSize}>{title}</NewsTitle>
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