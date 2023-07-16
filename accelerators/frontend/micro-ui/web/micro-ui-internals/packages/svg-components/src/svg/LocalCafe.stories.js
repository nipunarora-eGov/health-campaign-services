import React from "react";
import { LocalCafe } from "./LocalCafe";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "LocalCafe",
  component: LocalCafe,
};

export const Default = () => <LocalCafe />;
export const Fill = () => <LocalCafe fill="blue" />;
export const Size = () => <LocalCafe height="50" width="50" />;
export const CustomStyle = () => <LocalCafe style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <LocalCafe className="custom-class" />;
