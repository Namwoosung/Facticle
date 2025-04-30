import styled from 'styled-components';

export const FilterWrapper = styled.div`
    position: relative;
    display: flex;
    flex-direction: column;
`;

export const FilterButton = styled.button<{ open?: boolean }>`
    font-size: 18px;
    border: none;
    background-color: white;
    display: flex;
    align-items: center;
    border-radius: 5px;
    padding: 5px 10px;

    &:hover {
        cursor: pointer;
        color: #524DD6;
    }

    ${({ open }) =>
        open &&
        `
        background-color: #E1EEFE;
        color: #524DD6;
        font-weight: 600;
    `}
`;

export const FilterResetButton = styled.button`
    font-size: 18px;
    border: none;
    background-color: white;
    display: flex;
    align-items: center;
    gap: 5px;
    color: #A8A8A8;
    border-radius: 5px;

    &:hover {
        cursor: pointer;
        color: #A8A8A8;
        background-color: #D9D9D9;
    }
`;

export const FilterDialogButton = styled.button< { selected: boolean } >`
    width: 200px;
    font-size: 18px;
    font-weight: ${props => props.selected ? "600" : "400"};
    border: none;
    background-color: white;
    color: black;
    border-bottom: 1px solid #D9D9D9;
    padding: 10px 0;
    padding-left: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    &:hover {
        cursor: pointer;
        background-color: #D9D9D9;
    }
`;

export const DialogText = styled.p`
    font-size: 12px;
    color: #A8A8A8;
    margin: 0;
`

export const StarContainer = styled.div`
    display: flex;
    align-items: center;
    gap: 5px;
    margin-top: 10px;
    margin-bottom: 0px;
    position: relative;
    top: 5px;
`;

// 세로 구분선
export const FilterLine = styled.div`
    width: 1px;
    height: 25px;
    background-color: #D9D9D9;
    display: block;
    margin: 0 10px;
`;