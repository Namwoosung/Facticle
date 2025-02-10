import Header from "../../components/header";
import styled from "styled-components";

const PageWrapper = styled.div`
    display: flex;
    flex-direction: column;
`;

function Home() {
    return (
        <PageWrapper>
            <Header />
            <h1>Home</h1>
            <h1>Home</h1>
        </PageWrapper>
    );
}

export default Home;