import { Routes, Route } from "react-router-dom";
import Landing from "./components/Landing.jsx";
import About from "./components/About.jsx";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/about" element={<About />} />
      </Routes>
    </>
  );
}

export default App;
