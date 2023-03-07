import RequireAuth from "./components/RequireAuth";
import Authentication from "./components/Authentication";
import Homepage from "./components/Homepage";
import About from "./components/About";
import Layout from "./components/Layout";
import AdminPanel from "./components/AdminPanel";

import { Routes, Route } from "react-router-dom";
import Unauthorized from "./components/Unauthorized";

const USER = "ROLE_USER";
const ADMIN = "ROLE_ADMIN";
const TEAM_MEMBER = "ROLE_TEAM_MEMBER";
const TEAM_MANAGER = "ROLE_TEAM_MANAGER";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Layout />}>
          {/* This routes are public */}
          <Route path="/authenticate" element={<Authentication />} />

          {/*This routes require login*/}
          <Route element={<RequireAuth allowedRoles={[USER]} />}>
            <Route path="/" element={<Homepage />} />
          </Route>
          <Route element={<RequireAuth allowedRoles={[USER]} />}>
            <Route path="/about" element={<About />} />
          </Route>
          <Route element={<RequireAuth allowedRoles={[ADMIN]} />}>
            <Route path="/admin" element={<AdminPanel />} />
          </Route>

          <Route>
            <Route path="/unauthorized" element={<Unauthorized />} />
          </Route>

          {/*  */}
        </Route>
      </Routes>
    </>
  );
}

export default App;
