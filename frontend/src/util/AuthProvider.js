import { createContext } from "react";

export const AuthContext = createContext(null);

// export const AuthProvider = ({ children }) => {
//   const [auth, setAuth] = useState({});

//   const value = useMemo(() => ({ auth, setAuth }), [auth, setAuth]);

//   return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
// };
