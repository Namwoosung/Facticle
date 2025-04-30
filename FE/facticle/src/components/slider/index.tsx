import React, { useState } from 'react';

interface TimeSliderProps {
  type: string; // 타입 (예: "time")
  defaultValue?: number | undefined; // 기본값, undefined일 경우 0으로 설정
  onChange?: (label: any) => void;
}

const Slider: React.FC<TimeSliderProps> = ({ type, defaultValue = 0, onChange }) => {
  const [value, setValue] = useState<number>(defaultValue); // 0 = 1일 전

  const getTimeLabel = (val: number) => {
    if (val === 0) {
      return '1일 전';
    } else if (val < 0) {
      return `${Math.abs(val) + 1}일 전`;
    } else if (val === 24) {
      return '현재';
    } else {
      return `${24 - val}시간 전`;
    }
  };

  const getScoreLabel = (val: number) => {
    return `${val}점`;
  };

  const getStarLabel = (val: number) => {
    return `0 ~ ${val}점`;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = Number(e.target.value);
    setValue(newValue);
    if (onChange) {
      switch (type) {
        case 'time':
          onChange(getTimeLabel(newValue));
          break;
        case 'score':
          onChange(getScoreLabel(newValue));
          break;
        case 'star':
          onChange(getStarLabel(newValue));
          break;
        default:
          onChange(''); // 기본값 설정
          break;
      }
    };
  }

  return (
    <div style={{ width: '300px', margin: '20px' }}>
      {type === 'time' &&
        <input
          type="range"
          min={-6}
          max={23}
          step={1}
          value={value}
          onChange={handleChange}
          style={{
            width: '100%',
            WebkitAppearance: 'none',
          }}
        />
      }
      {type === 'score' &&
        <input
          type="range"
          min={0}
          max={100}
          step={1}
          value={value}
          onChange={handleChange}
          style={{
            width: '100%',
            WebkitAppearance: 'none',
          }}
        />
      }
      {type === 'star' &&
        <input
          type="range"
          min={0}
          max={5}
          step={1}
          value={value}
          onChange={handleChange}
          style={{
            width: '100%',
            WebkitAppearance: 'none',
          }}
        />
      }
      <div style={{ textAlign: 'center', marginTop: '10px' }}>
        {type === 'time' && (
          getTimeLabel(value)
        )}
        {type === 'score' && (
          getScoreLabel(value)
        )}
        {type === 'star' && (
          getStarLabel(value)
        )}
      </div>

      {/* 트랙 색상만 바꾸는 부분 */}
      <style>
        {type === 'time' ? `
          input[type="range"]::-webkit-slider-runnable-track {
            background: linear-gradient(to right, #524DD6 ${((value + 6) / 29) * 100}%, #D9D9D9 ${((value + 6) / 29) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
          input[type="range"]::-moz-range-track {
            background: linear-gradient(to right, #524DD6 ${((value + 6) / 29) * 100}%, #D9D9D9 ${((value + 6) / 29) * 100}%);
            height: 8px;
            border-radius: 5px;
          }

          input[type="range"]::-ms-track {
            background: linear-gradient(to right, #524DD6 ${((value + 6) / 29) * 100}%, #D9D9D9 ${((value + 6) / 29) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
            /* Thumb (슬라이더 손잡이) 스타일 */
          input[type=range]::-webkit-slider-thumb {
            appearance: none;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: #fff;
            border: 2px solid #524DD6;
            margin-top: -6px; /* 트랙과 thumb를 맞추기 위해 margin 설정 */
            cursor: pointer;
          }
        `: ``}
        {type === 'score' ? `
          input[type="range"]::-webkit-slider-runnable-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 100) * 100}%, #D9D9D9 ${((value) / 100) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
          input[type="range"]::-moz-range-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 100) * 100}%, #D9D9D9 ${((value) / 100) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
          input[type="range"]::-ms-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 100) * 100}%, #D9D9D9 ${((value) / 100) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
            /* Thumb (슬라이더 손잡이) 스타일 */
          input[type=range]::-webkit-slider-thumb {
            appearance: none;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: #fff;
            border: 2px solid #524DD6;
            margin-top: -6px; /* 트랙과 thumb를 맞추기 위해 margin 설정 */
            cursor: pointer;
          }
        `: ``}
        {
          type === 'star' ? `
          input[type="range"]::-webkit-slider-runnable-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 5) * 100}%, #D9D9D9 ${((value) / 5) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
          input[type="range"]::-moz-range-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 5) * 100}%, #D9D9D9 ${((value) / 5) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
          input[type="range"]::-ms-track {
            background: linear-gradient(to right, #524DD6 ${((value) / 5) * 100}%, #D9D9D9 ${((value) / 5) * 100}%);
            height: 8px;
            border-radius: 5px;
          }
            /* Thumb (슬라이더 손잡이) 스타일 */
          input[type=range]::-webkit-slider-thumb {
            appearance: none;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: #fff;
            border: 2px solid #524DD6;
            margin-top: -6px; /* 트랙과 thumb를 맞추기 위해 margin 설정 */
            cursor: pointer;
          }
        ` : ``
        }
      </style>
    </div>
  );
};



export default Slider;
