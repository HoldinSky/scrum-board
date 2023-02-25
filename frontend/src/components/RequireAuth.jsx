import { Navigate, useLocation, Outlet } from "react-router-dom";
import { useLocalState } from "../hooks/useLocalStorage";

const RequireAuth = () => {
  const [auth, setAuth] = useLocalState(null, "auth");
  const location = useLocation();

  return auth ? (
    <Outlet />
  ) : (
    <Navigate to="/authenticate" state={{ from: location }} replace />
  );
};

export default RequireAuth;
