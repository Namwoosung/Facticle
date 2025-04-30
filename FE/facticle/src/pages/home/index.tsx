import News from "../../components/news";
import { CategoryNewsWrapper, CategoryText, CategoryTitle, CategoryTitleWrapper, CategoryWrapper, MoreButton, MoreNewsButton, MoreNewsTitle, MoreNewsWrapper, NewsHighlightContainer, NewsHighlightContent, NewsHighlightWrapper, PageWrapper} from "./home.styles";
import { FaChevronRight } from "react-icons/fa";

function Home() {
    return (
        <PageWrapper>
            <CategoryWrapper>
                <CategoryTitleWrapper>
                    <CategoryTitle>
                        <h2>최신 뉴스 보기</h2>
                        <CategoryText>뉴스 제목과 내용의 유사도를 분석해 신뢰할 수 있는 뉴스를 제공합니다.</CategoryText>
                    </CategoryTitle>
                    <MoreButton to="/news">
                        더보기
                        <FaChevronRight />
                    </MoreButton>
                </CategoryTitleWrapper>

                <CategoryNewsWrapper>
                    <News
                        src="1"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
                        hs_score={80}
                        fs_score={90}
                        rating={4.7}
                    />
                    <News
                        src="2"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                        hs_score={20}
                        fs_score={30}
                        rating={4.0}
                    />
                    <News
                        src="3"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다. 망자는 40명으로 늘었습니다."
                        hs_score={70}
                        fs_score={80}
                        rating={4.5}
                    />
                    <News
                        src="3"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                        hs_score={100}
                        fs_score={100}
                        rating={5.0}
                    />
                </CategoryNewsWrapper>
            </CategoryWrapper>

            <NewsHighlightWrapper>
                <NewsHighlightContainer>
                    <CategoryTitleWrapper>
                        <CategoryTitle>
                            <h2>인기 뉴스</h2>
                        </CategoryTitle>
                        <MoreButton to="/news">
                            더보기
                            <FaChevronRight />
                        </MoreButton>
                    </CategoryTitleWrapper>

                    <NewsHighlightContent>
                        <div style={{ display: "flex", flexDirection: "column", gap: "20px",width:"100%", height: "100%" }}> 
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                rating={5.0}
                            />
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                axis='row'
                                rating={3.5}
                            />
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                axis='row'
                                rating={4.0}
                            />
                        </div>
                    </NewsHighlightContent>
                </NewsHighlightContainer>

                <NewsHighlightContainer>
                    <CategoryTitleWrapper>
                        <CategoryTitle>
                            <h2>유사도 높은 뉴스</h2>
                        </CategoryTitle>
                        <MoreButton to="/news">
                            더보기
                            <FaChevronRight />
                        </MoreButton>
                    </CategoryTitleWrapper>

                    <NewsHighlightContent>
                        <div style={{ display: "flex", flexDirection: "column", gap: "20px",width:"100%", height: "100%" }}> 
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                rating={5.0}
                            />
                            
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                axis='row'
                                rating={4.0}
                            />
                            <News
                                src="3"
                                image_url="https://imgnews.pstatic.net/image/311/2025/03/16/0001838655_001_20250316070019616.jpg?type=w647"
                                title="코로나19 신규 확진 1,500명대…사망자 40명"
                                content="코로나19 신규 확진자가 1,500명대를 기록했습니다. 사망자는 40명으로 늘었습니다."
                                hs_score={100}
                                fs_score={100}
                                axis='row'
                                rating={3.0}
                            />
                        </div>
                    </NewsHighlightContent>
                </NewsHighlightContainer>
            </NewsHighlightWrapper>

            <CategoryWrapper>
                <CategoryTitleWrapper>
                    <CategoryTitle>
                        <h2>연예</h2>
                    </CategoryTitle>
                    <MoreButton to="/news">
                        더보기
                        <FaChevronRight />
                    </MoreButton>
                </CategoryTitleWrapper>

                <CategoryNewsWrapper>
                    <News
                        src="1"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={80}
                        fs_score={90}
                        rating={4.7}
                    />
                    <News
                        src="2"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={20}
                        fs_score={30}
                        rating={2.4}
                    />
                    <News
                        src="3"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={70}
                        fs_score={80}
                        rating={3.5}
                    />
                </CategoryNewsWrapper>
            </CategoryWrapper>

            <CategoryWrapper>
                <CategoryTitleWrapper>
                    <CategoryTitle>
                        <h2>스포츠</h2>
                    </CategoryTitle>
                    <MoreButton to="/news">
                        더보기
                        <FaChevronRight />
                    </MoreButton>
                </CategoryTitleWrapper>

                <CategoryNewsWrapper>
                    <News
                        src="1"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={80}
                        fs_score={90}
                        rating={4.7}
                    />
                    <News
                        src="2"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={20}
                        fs_score={30}
                        rating={2.4}
                    />
                    <News
                        src="3"
                        image_url="https://image.dongascience.com/Photo/2024/08/a409753fb8046fc66e8fc506fec06cb2.jpg"
                        title="코로나19 신규 확진 1,500명대…사망자 40명"
                        hs_score={70}
                        fs_score={80}
                        rating={3.5}
                    />
                </CategoryNewsWrapper>
            </CategoryWrapper>

            <MoreNewsWrapper>
                <MoreNewsTitle>더 많은 뉴스 보러가기</MoreNewsTitle>
                <MoreNewsButton to="/news">더보기</MoreNewsButton>
            </MoreNewsWrapper>
        </PageWrapper>
    );
}

export default Home;