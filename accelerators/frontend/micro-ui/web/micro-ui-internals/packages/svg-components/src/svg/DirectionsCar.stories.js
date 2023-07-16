import React from "react";
import { DirectionsCar } from "./DirectionsCar";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "DirectionsCar",
  component: DirectionsCar,
};

export const Default = () => <DirectionsCar />;
export const Fill = () => <DirectionsCar fill="blue" />;
export const Size = () => <DirectionsCar height="50" width="50" />;
export const CustomStyle = () => <DirectionsCar style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <DirectionsCar className="custom-class" />;
