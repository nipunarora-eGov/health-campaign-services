import React from "react";
import { LockOutline } from "./LockOutline";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "LockOutline",
  component: LockOutline,
};

export const Default = () => <LockOutline />;
export const Fill = () => <LockOutline fill="blue" />;
export const Size = () => <LockOutline height="50" width="50" />;
export const CustomStyle = () => <LockOutline style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <LockOutline className="custom-class" />;
