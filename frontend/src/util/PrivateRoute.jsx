import { Navigate } from "react-router-dom";
import { useLocalState } from "./useLocalStorage";

const PrivateRoute = ({ children }) => {
  const [token, setToken] = useLocalState(null, "token");
  return token ? children : <Navigate to="/authenticate" />;
};

export default PrivateRoute;
