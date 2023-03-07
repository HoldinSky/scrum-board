import { useLocalState } from "./useLocalStorage";

const useAuth = () => {
  const [auth, setAuth] = useLocalState(null, "auth");
  return { auth, setAuth };
};

export default useAuth;
