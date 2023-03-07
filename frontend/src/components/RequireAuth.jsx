import { Navigate, useLocation, Outlet } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const RequireAuth = ({ allowedRoles }) => {
  const { auth } = useAuth();
  const location = useLocation();

  return !auth ? (
    <Navigate to="/authenticate" state={{ from: location }} replace={true} />
  ) : auth.roles.find((role) => allowedRoles?.includes(role)) ? (
    <Outlet />
  ) : (
    <Navigate to="/unauthorized" state={{ from: location }} replace={true} />
  );
};

export default RequireAuth;
