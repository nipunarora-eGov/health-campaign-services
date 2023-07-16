import React from "react";
import { Expand } from "./Expand";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "Expand",
  component: Expand,
};

export const Default = () => <Expand />;
export const Fill = () => <Expand fill="blue" />;
export const Size = () => <Expand height="50" width="50" />;
export const CustomStyle = () => <Expand style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <Expand className="custom-class" />;
