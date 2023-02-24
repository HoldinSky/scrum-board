import { Routes, Route } from "react-router-dom";
import Authenticate from "./components/Authenticate.jsx";
import Homepage from "./components/Homepage.jsx";
import PrivateRoute from "./util/PrivateRoute.jsx";

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
        <Route path="/authenticate" element={<Authenticate />} />
      </Routes>
    </>
  );
}

export default App;
