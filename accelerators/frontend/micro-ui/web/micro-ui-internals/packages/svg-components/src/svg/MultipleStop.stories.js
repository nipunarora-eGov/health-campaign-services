import React from "react";
import { MultipleStop } from "./MultipleStop";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "MultipleStop",
  component: MultipleStop,
};

export const Default = () => <MultipleStop />;
export const Fill = () => <MultipleStop fill="blue" />;
export const Size = () => <MultipleStop height="50" width="50" />;
export const CustomStyle = () => <MultipleStop style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <MultipleStop className="custom-class" />;
