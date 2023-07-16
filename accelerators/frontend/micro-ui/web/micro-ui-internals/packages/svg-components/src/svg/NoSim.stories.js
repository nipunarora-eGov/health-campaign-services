import React from "react";
import { NoSim } from "./NoSim";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "NoSim",
  component: NoSim,
};

export const Default = () => <NoSim />;
export const Fill = () => <NoSim fill="blue" />;
export const Size = () => <NoSim height="50" width="50" />;
export const CustomStyle = () => <NoSim style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <NoSim className="custom-class" />;
