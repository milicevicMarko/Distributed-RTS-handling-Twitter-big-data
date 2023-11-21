import { BrowserRouter, Route, Routes } from "react-router-dom";

import Home from "./../components/Home";
import Contact from "./../components/Contact";
import NoPageScreen from "../components/NoPageScreen";
import { About } from "../components/About";
import { EditStructure } from "../components/EditStreamStructure";
import Chart from "../components/StreamComponent";

export function AllRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/edit" element={<EditStructure />} />
      <Route path="/about" element={<About />} />
      <Route path="/contact" element={<Contact />} />
      <Route path="/stream" element={<Chart />} />
      <Route path="*" element={<NoPageScreen />} />
    </Routes>
  );
}
