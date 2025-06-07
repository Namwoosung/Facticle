import React, { useState, useEffect, useCallback, useMemo } from 'react';
import styled from 'styled-components';

interface DualSliderProps {
  type: string;
  defaultMinValue?: number;
  defaultMaxValue?: number;
  onChange?: (minLabel: string, maxLabel: string, minValue: number, maxValue: number) => void;
}

const SliderContainer = styled.div`
  width: 300px;
  margin: 20px 0;
  position: relative;
`;

const SliderTrack = styled.div`
  width: 100%;
  height: 8px;
  border-radius: 5px;
  background: #D9D9D9;
  position: relative;
`;

const SliderRange = styled.div<{ left: number; width: number }>`
  position: absolute;
  height: 8px;
  border-radius: 5px;
  background: #524DD6;
  left: ${props => props.left}%;
  width: ${props => props.width}%;
`;

const SliderInput = styled.input`
  position: absolute;
  width: 100%;
  height: 8px;
  background: none;
  pointer-events: none;
  appearance: none;
  border: none;
  outline: none;
  top: 0;
  left: 0;

  &::-webkit-slider-thumb {
    appearance: none;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #fff;
    border: 2px solid #524DD6;
    cursor: pointer;
    pointer-events: all;
    position: relative;
    z-index: 3;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  }

  &::-moz-range-thumb {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: #fff;
    border: 2px solid #524DD6;
    cursor: pointer;
    pointer-events: all;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  }

  &:first-of-type {
    z-index: 1;
  }

  &:last-of-type {
    z-index: 2;
  }
`;

const SliderLabels = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 12px;
  color: #666;
`;

const SliderValue = styled.div`
  text-align: center;
  margin-top: 10px;
  font-weight: 600;
  color: #524DD6;
  font-size: 14px;
`;

const DualSlider: React.FC<DualSliderProps> = ({ 
  type, 
  defaultMinValue = 0, 
  defaultMaxValue = 0, 
  onChange 
}) => {
  const { min, max } = useMemo(() => {
    switch (type) {
      case 'time':
        return { min: -6, max: 23 };
      case 'score':
        return { min: 0, max: 100 };
      case 'star':
        return { min: 0, max: 5 };
      default:
        return { min: 0, max: 100 };
    }
  }, [type]);
  
  const [minValue, setMinValue] = useState(defaultMinValue);
  const [maxValue, setMaxValue] = useState(defaultMaxValue === 0 ? max : defaultMaxValue);

  const getLabel = useCallback((val: number) => {
    switch (type) {
      case 'time':
        if (val === 0) return '1일 전';
        if (val < 0) return `${Math.abs(val) + 1}일 전`;
        if (val >= 23) return '현재';
        return `${24 - val}시간 전`;
      case 'score':
        return `${val}점`;
      case 'star':
        return `${val}점`;
      default:
        return val.toString();
    }
  }, [type]);

  const handleMinChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = Math.min(Number(e.target.value), maxValue - (type === 'star' ? 0 : 1));
    setMinValue(value);
  };

  const handleMaxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = Math.max(Number(e.target.value), minValue + (type === 'star' ? 0 : 1));
    setMaxValue(value);
  };

  // 슬라이더 범위 계산
  const getPercent = (value: number) => ((value - min) / (max - min)) * 100;

  // onChange를 debounce로 처리하여 너무 자주 호출되지 않도록 함
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      if (onChange) {
        const minLabel = getLabel(minValue);
        const maxLabel = getLabel(maxValue);
        onChange(minLabel, maxLabel, minValue, maxValue);
      }
    }, 300); // 300ms로 증가

    return () => clearTimeout(timeoutId);
  }, [minValue, maxValue]);

  const displayValues = useMemo(() => {
    return {
      minLabel: getLabel(min),
      maxLabel: getLabel(max),
      currentMinLabel: getLabel(minValue),
      currentMaxLabel: getLabel(maxValue),
      rangeLeft: getPercent(minValue),
      rangeWidth: getPercent(maxValue) - getPercent(minValue)
    };
  }, [min, max, minValue, maxValue, getLabel]);

  return (
    <SliderContainer>
      <SliderTrack>
        <SliderRange 
          left={displayValues.rangeLeft} 
          width={displayValues.rangeWidth} 
        />
      </SliderTrack>
      
      <SliderInput
        type="range"
        min={min}
        max={max}
        step={1}
        value={minValue}
        onChange={handleMinChange}
      />
      
      <SliderInput
        type="range"
        min={min}
        max={max}
        step={1}
        value={maxValue}
        onChange={handleMaxChange}
      />

      <SliderLabels>
        <span>{displayValues.minLabel}</span>
        <span>{displayValues.maxLabel}</span>
      </SliderLabels>

      <SliderValue>
        {displayValues.currentMinLabel} ~ {displayValues.currentMaxLabel}
      </SliderValue>
    </SliderContainer>
  );
};

export default React.memo(DualSlider);
