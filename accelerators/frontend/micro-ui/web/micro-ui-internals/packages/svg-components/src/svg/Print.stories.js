import React from "react";
import { Print } from "./Print";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "Print",
  component: Print,
};

export const Default = () => <Print />;
export const Fill = () => <Print fill="blue" />;
export const Size = () => <Print height="50" width="50" />;
export const CustomStyle = () => <Print style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <Print className="custom-class" />;
