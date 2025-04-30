import styled from "styled-components";

export const FilterDialogWrapper = styled.div<{open: boolean}>`
    position: absolute;
    display: ${props => props.open ? "flex" : "none"};
    flex-direction: column;
    align-items: center;
    top: 10px;
    box-shadow: 0px 4px 10px 2px rgba(0, 0, 0, 0.25);
    border-radius: 10px;
    background-color: white;
    padding: 10px 0px;
`;