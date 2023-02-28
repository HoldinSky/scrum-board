import RequireAuth from "./components/RequireAuth";
import Authentication from "./components/Authentication";
import Homepage from "./components/Homepage";
import About from "./components/About";
import Layout from "./components/Layout";
import AdminPanel from "./components/AdminPanel";

import { Routes, Route } from "react-router-dom";
import Unauthorized from "./components/Unauthorized";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Layout />}>
          {/* This routes are public */}
          <Route path="/" element={<Homepage />} />
          <Route path="/authenticate" element={<Authentication />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          {/*This routes require login*/}
          <Route element={<RequireAuth allowedRoles={["ROLE_USER"]} />}>
            <Route path="/about" element={<About />} />
          </Route>

          <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN"]} />}>
            <Route path="/admin" element={<AdminPanel />} />
          </Route>

          {/*  */}
        </Route>
      </Routes>
    </>
  );
}

export default App;
