import React from "react";
import { Login } from "./Login";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "Login",
  component: Login,
};

export const Default = () => <Login />;
export const Fill = () => <Login fill="blue" />;
export const Size = () => <Login height="50" width="50" />;
export const CustomStyle = () => <Login style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <Login className="custom-class" />;
