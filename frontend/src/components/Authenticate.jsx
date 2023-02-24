import { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import "../index.css";
import { domain } from "../util/domain";
import { useLocalState } from "../util/useLocalStorage";

function Authenticate() {
  useEffect(() => {
    document.title = "Authentication";
  });

  return (
    <>
      <Authentication />
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

const Authentication = () => {
  const [formSignIn, setFormSignIn] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  return formSignIn ? (
    <Login
      form={[formSignIn, setFormSignIn]}
      password={[showPassword, setShowPassword]}
      user={[username, setUsername, password, setPassword]}
    />
  ) : (
    <SignUp
      form={[formSignIn, setFormSignIn]}
      password={[showPassword, setShowPassword]}
      user={[username, setUsername, password, setPassword]}
    />
  );
};

const Login = ({
  password: [showPassword, setShowPassword],
  form: [formSignIn, setFormSignIn],
  user: [username, setUsername, password, setPassword],
}) => {
  const [token, setToken] = useLocalState(null, "token");
  const [user, setUser] = useLocalState(null, "user");

  const [errorLogin, setErrorLogin] = useState("");

  const loginBody = {
    username: username,
    password: password,
  };

  // send login request
  const handleSignIn = (event) => {
    if (!token) {
      fetch(`${domain}/api/auth/login`, {
        method: "post",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(loginBody),
      })
        .then((response) => {
          if (response.status === 200) return response.json();
          else {
            return Promise.reject(errorLogin);
          }
        })
        .then((data) => {
          const token = {
            access_token: data.tokens.access_token,
            refresh_token: data.tokens.refresh_token,
          };
          setToken(token);
          setUser(data.user);
          return <Navigate to="/" />;
        })
        .catch((errorLogin) => {
          setErrorLogin("Invalid email or password!");
        });
    }
    event.preventDefault();
  };

  return user || token ? (
    <>
      <div className="form-holder">
        <div className="auth-form">
          <h1 className="auth-header">You are successfully logged in!</h1>
          <div className="mt-6 text-center">
            <a className="auth-apply-btn" href="/">
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
  form: [formSignIn, setFormSignIn],
  password: [showPassword, setShowPassword],
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

  // check for equal passwords
  useEffect(() => {
    if (password !== repeatPassword) {
      setErrorRepeatPass("Passwords do not match");
    } else {
      setErrorRepeatPass("");
    }
  }, [password, repeatPassword]);

  // check for valid password
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

  useEffect(() => {
    setErrorEmail("");
  }, [registerBody.username]);

  // send registration request
  const handleSignUp = (event) => {
    fetch(`${domain}/api/auth/register`, {
      method: "post",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(registerBody),
    })
      .then((response) => {
        if (response.status === 201) {
          return response.json();
        } else {
          return Promise.reject("The email is already taken!");
        }
      })
      .then(() => {
        setFormSignIn(true);
        setShowPassword(false);
      })
      .catch((message) => {
        setErrorEmail(message);
      });

    event.preventDefault();
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

export default Authenticate;
