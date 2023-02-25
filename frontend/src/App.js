import RequireAuth from "./components/RequireAuth";
import Authentication from "./components/Authentication";
import Homepage from "./components/Homepage";
import About from "./components/About";
import Layout from "./components/Layout";

import { Routes, Route } from "react-router-dom";
import { AuthContext } from "./util/AuthProvider";
import { useMemo, useState } from "react";

function App() {
  const [auth, setAuth] = useState(null);

  const value = useMemo(() => ({ auth, setAuth }), [auth, setAuth]);

  return (
    <>
      <AuthContext.Provider value={value}>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/*This routes require login*/}
            <Route element={<RequireAuth />}>
              <Route path="/about" element={<About />} />
            </Route>

            {/* This routes are public */}
            <Route path="/" element={<Homepage />} />
            <Route path="/authenticate" element={<Authentication />} />
          </Route>
        </Routes>
      </AuthContext.Provider>
    </>
  );
}

export default App;
