import React from "react";
import { SupervisorAccount } from "./SupervisorAccount";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "SupervisorAccount",
  component: SupervisorAccount,
};

export const Default = () => <SupervisorAccount />;
export const Fill = () => <SupervisorAccount fill="blue" />;
export const Size = () => <SupervisorAccount height="50" width="50" />;
export const CustomStyle = () => <SupervisorAccount style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <SupervisorAccount className="custom-class" />;
