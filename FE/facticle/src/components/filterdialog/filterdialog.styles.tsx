import styled from "styled-components";

interface FilterDialogWrapperProps {
  open: boolean;
  hasSlider?: boolean;
}

export const FilterDialogWrapper = styled.div.withConfig({
  shouldForwardProp: (prop) => !['open', 'hasSlider'].includes(prop),
})<FilterDialogWrapperProps>`
    position: absolute;
    display: ${props => props.open ? "flex" : "none"};
    flex-direction: column;
    align-items: center;
    top: 10px;
    box-shadow: 0px 4px 10px 2px rgba(0, 0, 0, 0.25);
    border-radius: 10px;
    background-color: white;
    padding: ${props => props.hasSlider ? '20px' : '0px'};
    min-width: ${props => props.hasSlider ? '350px' : '200px'};
    z-index: 1000;
`;

export const FilterDialogTitle = styled.div`
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
`;