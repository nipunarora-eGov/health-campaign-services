import React from "react";
import { CallMissed } from "./CallMissed";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "CallMissed",
  component: CallMissed,
};

export const Default = () => <CallMissed />;
export const Fill = () => <CallMissed fill="blue" />;
export const Size = () => <CallMissed height="50" width="50" />;
export const CustomStyle = () => <CallMissed style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <CallMissed className="custom-class" />;
