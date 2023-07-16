import React from "react";
import { Favorite } from "./Favorite";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "Favorite",
  component: Favorite,
};

export const Default = () => <Favorite />;
export const Fill = () => <Favorite fill="blue" />;
export const Size = () => <Favorite height="50" width="50" />;
export const CustomStyle = () => <Favorite style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <Favorite className="custom-class" />;
