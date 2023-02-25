import { useRef, useState, useEffect } from "react";
import {
  faCheck,
  faTimes,
  faInfoCircle,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useLocalState } from "../hooks/useLocalStorage";
import axios from "../api/axios";

const LOGIN_URL = "/api/auth/login";
const SIGNUP_URL = "/api/auth/register";

const EMAIL_REGEX = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%&_]).{8,24}$/;

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
  const [formSignIn, setFormSignIn] = useState(true);
  const [showPassword, setShowPassword] = useState(false);

  const [email, setEmail] = useState("");
  const [password, setPwd] = useState("");

  return formSignIn ? (
    <Login
      formType={[formSignIn, setFormSignIn]}
      showPass={[showPassword, setShowPassword]}
      user={[email, setEmail, password, setPwd]}
    />
  ) : (
    <SignUp
      formType={[formSignIn, setFormSignIn]}
      showPass={[showPassword, setShowPassword]}
      user={[email, setEmail, password, setPwd]}
    />
  );
};

const Login = ({
  showPass: [showPassword, setShowPassword],
  formType: [formSignIn, setFormSignIn],
  user: [email, setEmail, pwd, setPwd],
}) => {
  const [auth, setAuth] = useLocalState(null, "auth");

  const [errorLogin, setErrorLogin] = useState("");

  // send login request
  const handleSignIn = async (event) => {
    event.preventDefault();

    try {
      const response = await axios.post(
        LOGIN_URL,
        JSON.stringify({
          username: email,
          password: pwd,
        }),
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      setEmail("");
      setPwd("");

      const access_token = response?.data?.tokens.access_token;
      const refresh_token = response?.data?.tokens?.refresh_token;

      setAuth({
        user: response?.data?.user,
        tokens: { access_token, refresh_token },
      });

      console.log(`Auth context is: ${auth}`);
    } catch (exc) {
      console.log(exc);
      setErrorLogin("Invalid email or password!");
    }
  };

  console.log(`AuthContext is ${auth?.user}`);
  return auth ? (
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
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="mb-2">
            <label htmlFor="password" className="auth-field-label">
              Password
            </label>
            <input
              type={showPassword ? "text" : "password"}
              className="auth-field-input"
              value={pwd}
              onChange={(e) => setPwd(e.target.value)}
            />
            <div className="display-error text-error">
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
  user: [email, setEmail, pwd, setPwd],
}) => {
  const fnameRef = useRef();
  const lnameRef = useRef();
  const emailRef = useRef();
  const errRef = useRef();

  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");

  // const [email, setEmail] = useState("");
  const [validEmail, setValidEmail] = useState(false);
  const [emailFocus, setEmailFocus] = useState(false);
  const [takenEmail, setTakenEmail] = useState(false);

  // const [pwd, setPwd] = useState("");
  const [validPwd, setValidPwd] = useState(false);
  const [pwdFocus, setPwdFocus] = useState(false);

  const [matchPwd, setMatchPwd] = useState("");
  const [validMatch, setValidMatch] = useState(false);
  const [matchFocus, setMatchFocus] = useState(false);

  const takenEmailError = "Email is already taken";

  useEffect(() => {
    fnameRef.current.focus();
    setValidEmail(false);
  }, []);

  useEffect(() => {
    setValidEmail(EMAIL_REGEX.test(email));
  }, [email]);

  // check passwords for equality
  useEffect(() => {
    setValidPwd(PWD_REGEX.test(pwd));
    setValidMatch(pwd === matchPwd);
  }, [pwd, matchPwd]);

  // reset unique email error when is is changed
  useEffect(() => {
    setTakenEmail(false);
  }, [email]);

  // send registration request
  const handleSignUp = async (event) => {
    event.preventDefault();

    try {
      await axios.post(
        SIGNUP_URL,
        JSON.stringify({
          firstname: firstname,
          lastname: lastname,
          username: email,
          password: pwd,
        }),
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      setFirstname("");
      setLastname("");
      setMatchPwd("");

      setFormSignIn(true);
      setShowPassword(false);
    } catch (exc) {
      console.log(exc);
      setTakenEmail(true);
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
              ref={fnameRef}
              required={true}
              autoComplete="off"
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
              ref={lnameRef}
              required={true}
              autoComplete="off"
              onChange={(e) => setLastname(e.target.value)}
            />
          </div>
          <div className="mb-2">
            <label htmlFor="email" className="auth-field-label">
              Email
              <span className={validEmail ? "icon-valid" : "hidden"}>
                <FontAwesomeIcon icon={faCheck} />
              </span>
              <span
                className={validEmail || !email ? "hidden" : "icon-invalid"}
              >
                <FontAwesomeIcon icon={faTimes} />
              </span>
              <span
                className={takenEmail ? "display-error text-error" : "hidden"}
                aria-live="assertive"
                ref={errRef}
              >
                {renderErrorMessage(takenEmailError)}
              </span>
            </label>
            <input
              type="email"
              id="email"
              className="auth-field-input"
              autoComplete="off"
              value={email}
              ref={emailRef}
              required={true}
              aria-invalid={validEmail ? "false" : "true"}
              aria-describedby="emainote"
              onFocus={() => setEmailFocus(true)}
              onBlur={() => setEmailFocus(false)}
              onChange={(e) => setEmail(e.target.value)}
            />
            <p
              id="emailnote"
              className={
                emailFocus && email && !validEmail
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              {" Must contain '@' character."}
              <br />
              Main part and domain should not start or end with dot.
              <br />
              Must not contain double dots.
              <br />
            </p>
          </div>
          <div className="mb-2">
            <label htmlFor="password" className="auth-field-label">
              Password
              <span className={validPwd ? "icon-valid" : "hidden"}>
                <FontAwesomeIcon icon={faCheck} />
              </span>
              <span className={validPwd || !pwd ? "hidden" : "icon-invalid"}>
                <FontAwesomeIcon icon={faTimes} />
              </span>
            </label>
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              className="auth-field-input"
              value={pwd}
              required={true}
              aria-invalid={validPwd ? "false" : "true"}
              aria-describedby="pwdnote"
              onFocus={() => setPwdFocus(true)}
              onBlur={() => setPwdFocus(false)}
              onChange={(e) => setPwd(e.target.value)}
            />
            <p
              id="pwdnote"
              className={
                pwdFocus && pwd && !validPwd ? "instructions" : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              {" 8 to 24 characters long"}
              <br />
              Must contain at least one uppercase letter, one lowercase.
              <br />
              At least one digit and special character
              <br />
            </p>
          </div>
          <div className="mb-2">
            <label htmlFor="matchPassword" className="auth-field-label">
              Confirm password
              <span
                className={validMatch && matchPwd ? "icon-valid" : "hidden"}
              >
                <FontAwesomeIcon icon={faCheck} />
              </span>
              <span
                className={validMatch || !matchPwd ? "hidden" : "icon-invalid"}
              >
                <FontAwesomeIcon icon={faTimes} />
              </span>
            </label>
            <input
              type={showPassword ? "text" : "password"}
              id="matchPassword"
              className="auth-field-input"
              value={matchPwd}
              required={true}
              aria-invalid={validMatch ? "false" : "true"}
              aria-describedby="matchnote"
              onFocus={() => setMatchFocus(true)}
              onBlur={() => setMatchFocus(false)}
              onChange={(e) => setMatchPwd(e.target.value)}
            />
            <p
              id="matchnote"
              className={
                matchFocus && matchPwd && !validMatch
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              {" Passwords have to be equal!"}
            </p>
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
              disabled={!validEmail && !takenEmail && !validPwd && !validMatch}
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
  return <>{error}</>;
};

export default Authentication;
