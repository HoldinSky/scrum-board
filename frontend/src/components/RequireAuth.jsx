import { Navigate, useLocation, Outlet } from "react-router-dom";
import { useLocalState } from "../hooks/useLocalStorage";

const RequireAuth = ({ allowedRoles }) => {
  const [auth, setAuth] = useLocalState(null, "auth");
  const location = useLocation();

  return !auth ? (
    <Navigate to="/authenticate" state={{ from: location }} replace />
  ) : auth.roles.find((role) => allowedRoles?.includes(role)) ? (
    <Outlet />
  ) : (
    <Navigate to="/unauthorized" state={{ from: location }} replace />
  );
};

export default RequireAuth;
