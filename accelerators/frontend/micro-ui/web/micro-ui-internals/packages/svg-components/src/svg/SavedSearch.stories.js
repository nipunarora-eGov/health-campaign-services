import React from "react";
import { SavedSearch } from "./SavedSearch";

export default {
  tags: ['autodocs'],
  argTypes: {
    className: {
        options: ['custom-class'],
        control: { type: 'check' },
    }
  },
  title: "SavedSearch",
  component: SavedSearch,
};

export const Default = () => <SavedSearch />;
export const Fill = () => <SavedSearch fill="blue" />;
export const Size = () => <SavedSearch height="50" width="50" />;
export const CustomStyle = () => <SavedSearch style={{ border: "1px solid red" }} />;
export const CustomClassName = () => <SavedSearch className="custom-class" />;
