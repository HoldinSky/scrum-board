import { useState, useEffect } from "react";
import { useLocalState } from "../hooks/useLocalStorage";
import useAuth from "../hooks/useAuth";
import axios from "../api/axios";

const LOGIN_URL = "/api/auth/login";
const SIGNUP_URL = "/api/auth/register";

function Authentication() {
  useEffect(() => {
    document.title = "Authentication";
  });

  return (
    <>
      <RenderForm />
    </>
  );
}

// const InfoTable = () => {
//   return (
//     <>
//       <div className=""></div>
//     </>
//   );
// };

const RenderForm = () => {
  const { setAuth } = useAuth();
  const [formSignIn, setFormSignIn] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  return formSignIn ? (
    <Login
      formType={[formSignIn, setFormSignIn]}
      showPass={[showPassword, setShowPassword]}
      user={[username, setUsername, password, setPassword]}
      auth={setAuth}
    />
  ) : (
    <SignUp
      formType={[formSignIn, setFormSignIn]}
      showPass={[showPassword, setShowPassword]}
      user={[username, setUsername, password, setPassword]}
    />
  );
};

const Login = ({
  showPass: [showPassword, setShowPassword],
  formType: [formSignIn, setFormSignIn],
  user: [username, setUsername, password, setPassword],
  auth: setAuth,
}) => {
  const [tokens, setTokens] = useLocalState(null, "tokens");
  const [user, setUser] = useLocalState(null, "user");

  const [errorLogin, setErrorLogin] = useState("");

  const loginBody = {
    username: username,
    password: password,
  };

  // send login request
  const handleSignIn = async (event) => {
    event.preventDefault();

    try {
      const response = await axios.post(LOGIN_URL, JSON.stringify(loginBody), {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      setUsername("");
      setPassword("");

      const accessToken = response?.data?.tokens.access_token;
      const refreshToken = response?.data?.tokens?.refresh_token;

      setTokens({ access_token: accessToken, refresh_token: refreshToken });
      setUser(response?.data?.user);

      setAuth({ user, tokens });
    } catch (exc) {
      console.log(exc);
      setErrorLogin("Invalid email or password!");
    }
  };

  return tokens ? (
    <>
      <div className="form-holder">
        <div className="auth-form">
          <h1 className="auth-header">You are successfully logged in!</h1>
          <div className="mt-6 text-center">
            <a className="auth-return-text" href="/">
              Go to HomePage
            </a>
          </div>
        </div>
      </div>
    </>
  ) : (
    <div className="form-holder">
      <div className="auth-form">
        <h1 className="auth-header">Sign in</h1>
        <form className="mt-6" onSubmit={handleSignIn}>
          <div className="mb-2">
            <label htmlFor="email" className="auth-field-label">
              Email
            </label>
            <input
              type="email"
              className="auth-field-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div className="mb-2">
            <label htmlFor="password" className="auth-field-label">
              Password
            </label>
            <input
              type={showPassword ? "text" : "password"}
              className="auth-field-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <div className="display-error">
              {errorLogin && renderErrorMessage(errorLogin)}
            </div>
          </div>
          <div className="mb-2 text-center">
            <button
              className="auth-password-btn"
              type="button"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? "Hide Password" : "Show Password"}
            </button>
          </div>
          <div className="mt-6">
            <button className="auth-apply-btn" type="submit">
              Login
            </button>
          </div>
        </form>

        <p className="auth-switch-form">
          {" "}
          Don't have an account?{" "}
          <button
            className="auth-switch-form-btn"
            onClick={() => setFormSignIn(!formSignIn)}
          >
            Sign up
          </button>
        </p>
      </div>
    </div>
  );
};

const SignUp = ({
  formType: [formSignIn, setFormSignIn],
  showPass: [showPassword, setShowPassword],
  user: [username, setUsername, password, setPassword],
}) => {
  const [errorEmail, setErrorEmail] = useState("");

  const [errorPass, setErrorPass] = useState("");
  const [errorRepeatPass, setErrorRepeatPass] = useState("");

  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");

  const registerBody = {
    firstname: firstname,
    lastname: lastname,
    username: username,
    password: password,
  };

  // check passwords for equality
  useEffect(() => {
    if (password !== repeatPassword) {
      setErrorRepeatPass("Passwords do not match");
    } else {
      setErrorRepeatPass("");
    }
  }, [password, repeatPassword]);

  // check password for validity
  useEffect(() => {
    const uppercaseRegExp = /(?=.*?[A-Z])/;
    const lowercaseRegExp = /(?=.*?[a-z])/;
    const digitsRegExp = /(?=.*?[0-9])/;
    const minLengthRegExp = /.{8,}/;
    const passwordLength = password.length;
    const uppercasePassword = uppercaseRegExp.test(password);
    const lowercasePassword = lowercaseRegExp.test(password);
    const digitsPassword = digitsRegExp.test(password);
    const minLengthPassword = minLengthRegExp.test(password);
    let errMsg = "";
    if (passwordLength === 0) {
      errMsg = "Password is empty";
    } else if (!uppercasePassword) {
      errMsg = "At least one Uppercase";
    } else if (!lowercasePassword) {
      errMsg = "At least one Lowercase";
    } else if (!digitsPassword) {
      errMsg = "At least one digit";
    } else if (!minLengthPassword) {
      errMsg = "At least minumum 8 characters";
    } else {
      errMsg = "";
    }
    setErrorPass(errMsg);
  }, [password]);

  // reset unique email error when username is changed
  useEffect(() => {
    setErrorEmail("");
  }, [registerBody.username]);

  // send registration request
  const handleSignUp = async (event) => {
    event.preventDefault();

    try {
      await axios.post(SIGNUP_URL, JSON.stringify(registerBody), {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      setFirstname("");
      setLastname("");
      setRepeatPassword("");

      setFormSignIn(true);
      setShowPassword(false);
    } catch (exc) {
      console.log(exc);
      setErrorEmail("Email is already taken!");
    }
  };

  return (
    <div className="form-holder">
      <div className="auth-form">
        <h1 className="auth-header">Sign up</h1>
        <form className="mt-6" action="" onSubmit={handleSignUp}>
          <div className="mb-2">
            <label htmlFor="firstname" className="auth-field-label">
              First Name
            </label>
            <input
              type="text"
              id="firstname"
              className="auth-field-input"
              value={firstname}
              onChange={(e) => setFirstname(e.target.value)}
            />
          </div>
          <div className="mb-2">
            <label htmlFor="lastname" className="auth-field-label">
              Last Name
            </label>
            <input
              type="text"
              id="lastname"
              className="auth-field-input"
              value={lastname}
              onChange={(e) => setLastname(e.target.value)}
            />
          </div>
          <div className="mb-2">
            <label htmlFor="email" className="auth-field-label">
              Email
            </label>
            <input
              type="email"
              id="email"
              className="auth-field-input"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
            <div className="display-error">
              {errorEmail && renderErrorMessage(errorEmail)}
            </div>
          </div>
          <div className="mb-2">
            <label htmlFor="password" className="auth-field-label">
              Password
            </label>
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              name="password"
              className="auth-field-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <div className="display-error">
              {errorPass && renderErrorMessage(errorPass)}
            </div>
          </div>
          <div className="mb-2">
            <label htmlFor="repeatPassword" className="auth-field-label">
              Repeat password
            </label>
            <input
              type={showPassword ? "text" : "password"}
              id="repeatPassword"
              name="repeatPassword"
              className="auth-field-input"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
            />
            <div className="display-error">
              {errorRepeatPass && renderErrorMessage(errorRepeatPass)}
            </div>
          </div>
          <div className="mb-2 text-center">
            <button
              className="auth-password-btn"
              type="button"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? "Hide Password" : "Show Password"}
            </button>
          </div>
          <div className="mt-6">
            <button
              className="auth-apply-btn"
              type="submit"
              disabled={errorPass || errorRepeatPass || errorEmail}
            >
              Sign up
            </button>
          </div>
        </form>

        <p className="auth-switch-form">
          {" "}
          Already have an account?{" "}
          <button
            className="auth-switch-form-btn"
            onClick={() => setFormSignIn(!formSignIn)}
          >
            Sign in
          </button>
        </p>
      </div>
    </div>
  );
};

const renderErrorMessage = (error) => {
  return <div className="text-error">{error}</div>;
};

export default Authentication;
