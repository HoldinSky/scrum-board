import axios from "../api/axios";
import { useLocalState } from "./useLocalStorage";

const REFRESH_URL = "/api/auth/refresh";

const useRefreshToken = () => {
  const [auth, setAuth] = useLocalState(null, "auth");

  const refresh = async () => {
    const response = await axios.post(
      REFRESH_URL + `?token=${auth.tokens.refresh_token}`,
      {
        withCredentials: true,
      }
    );
    setAuth({
      user: auth.user,
      roles: auth.roles,
      tokens: {
        access_token: response.data.access_token,
        refresh_token: auth.tokens.refresh_token,
      },
    });

    return response.data.access_token;
  };

  return refresh;
};

export default useRefreshToken;
