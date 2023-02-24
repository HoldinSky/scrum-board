import { Navigate } from "react-router-dom";
import { useLocalState } from "../hooks/useLocalStorage";

const PrivateRoute = ({ children }) => {
  const [tokens, setTokens] = useLocalState(null, "tokens");
  return tokens ? children : <Navigate to="/authenticate" />;
};

export default PrivateRoute;
