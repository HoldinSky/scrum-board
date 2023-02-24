import { Routes, Route } from "react-router-dom";
import Authentication from "./components/Authentication.jsx";
import Homepage from "./components/Homepage.jsx";
import PrivateRoute from "./components/PrivateRoute.jsx";

function App() {
  return (
    <>
      <Routes>
        <Route
          path="/"
          element={
            <PrivateRoute>
              <Homepage />
            </PrivateRoute>
          }
        />
        <Route path="/authenticate" element={<Authentication />} />
      </Routes>
    </>
  );
}

export default App;
