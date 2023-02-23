import { useState, useEffect } from "react";
import "../index.css";
import { domain } from "../assets/domain";

function Landing() {
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

  const [errorPass, setErrorPass] = useState("");
  const [errorRepeatPass, setErrorRepeatPass] = useState("");
  const [errorLogin, setErrorLogin] = useState("");
  const [errorEmail, setErrorEmail] = useState("");

  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");

  const loginBody = {
    username: username,
    password: password,
  };

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

  // send login request
  const handleSignIn = (event) => {
    fetch(`${domain}/api/v1/auth/login`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "post",
      body: JSON.stringify(loginBody),
    })
      .then((response) => response.json())
      .then((data) => console.log(data));

    event.preventDefault();
  };

  // send registration request
  const handleSignUp = (event) => {
    fetch(`${domain}/api/v1/auth/register`, {
      headers: {
        "Content-Type": "application/json",
      },
      method: "post",
      body: JSON.stringify(registerBody),
    })
      .then((response) => response.json())
      .then((data) => console.log(data));

    event.preventDefault();
  };

  if (formSignIn) {
    // Log in form
    return (
      <div className="relative flex flex-col justify-center min-h-screen overflow-hidden">
        <div className="w-full p-6 m-auto bg-white rounded-md shadow-xl lg:max-w-xl">
          <h1 className="text-3xl font-semibold text-center text-blue-500 underline">
            Sign in
          </h1>
          <form className="mt-6" onSubmit={handleSignIn}>
            <div className="mb-2">
              <label
                htmlFor="email"
                className="block text-sm font-semibold text-gray-800"
              >
                Email
              </label>
              <input
                type="email"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div className="mb-2">
              <label
                htmlFor="password"
                className="block text-sm font-semibold text-gray-800"
              >
                Password
              </label>
              <input
                type={showPassword ? "text" : "password"}
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <div className="text-sm font-light text-center">
                {errorLogin && renderErrorMessage(errorLogin)}
              </div>
            </div>
            <div className="mb-2 text-center">
              <button
                className="mt-4 text-sm font-light text-center text-gray-700 hover:underline"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "Hide Password" : "Show Password"}
              </button>
            </div>
            {/* <a href="#" className="text-xs text-purple-600 hover:underline">
            Forget Password?
          </a> */}
            <div className="mt-6">
              <button
                className="w-full px-4 py-2 tracking-wide text-white transition-colors duration-200 transform bg-blue-500 rounded-md hover:bg-blue-600 focus:outline-none focus:bg-blue-600"
                type="submit"
              >
                Login
              </button>
            </div>
          </form>

          <p className="mt-8 text-sm font-light text-center text-gray-700">
            {" "}
            Don't have an account?{" "}
            <button
              className="font-medium text-blue-600 hover:underline"
              onClick={() => setFormSignIn(!formSignIn)}
            >
              Sign up
            </button>
          </p>
        </div>
      </div>
    );
  } else {
    // Sign up form
    return (
      <div className="relative flex flex-col justify-center min-h-screen overflow-hidden">
        <div className="w-full p-6 m-auto bg-white rounded-md shadow-xl lg:max-w-xl">
          <h1 className="text-3xl font-semibold text-center text-blue-500 underline">
            Sign up
          </h1>
          <form className="mt-6" action="" onSubmit={handleSignUp}>
            <div className="mb-2">
              <label
                htmlFor="firstname"
                className="block text-sm font-semibold text-gray-800"
              >
                First Name
              </label>
              <input
                type="text"
                id="firstname"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={firstname}
                onChange={(e) => setFirstname(e.target.value)}
              />
            </div>
            <div className="mb-2">
              <label
                htmlFor="lastname"
                className="block text-sm font-semibold text-gray-800"
              >
                Last Name
              </label>
              <input
                type="text"
                id="lastname"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={lastname}
                onChange={(e) => setLastname(e.target.value)}
              />
            </div>
            <div className="mb-2">
              <label
                htmlFor="email"
                className="block text-sm font-semibold text-gray-800"
              >
                Email
              </label>
              <input
                type="email"
                id="email"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
              <div className="mx-2 text-sm font-light text-center">
                {errorEmail && renderErrorMessage(errorEmail)}
              </div>
            </div>
            <div className="mb-2">
              <label
                htmlFor="password"
                className="block text-sm font-semibold text-gray-800"
              >
                Password
              </label>
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                name="password"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <div className="mt-2 text-sm font-light text-center">
                {errorPass && renderErrorMessage(errorPass)}
              </div>
            </div>
            <div className="mb-2">
              <label
                htmlFor="repeatPassword"
                className="block text-sm font-semibold text-gray-800"
              >
                Repeat password
              </label>
              <input
                type={showPassword ? "text" : "password"}
                id="repeatPassword"
                name="repeatPassword"
                className="block w-full px-4 py-2 mt-2 text-black bg-white border rounded-md focus:border-blue-400 focus:ring-blue-300 focus:outline-none focus:ring focus:ring-opacity-40"
                value={repeatPassword}
                onChange={(e) => setRepeatPassword(e.target.value)}
              />
              <div className="mt-2 text-sm font-light text-center">
                {errorRepeatPass && renderErrorMessage(errorRepeatPass)}
              </div>
            </div>
            <div className="mb-2 text-center">
              <button
                className="mt-4 text-sm font-light text-center text-gray-700 hover:underline"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "Hide Password" : "Show Password"}
              </button>
            </div>
            <div className="mt-6">
              <button
                className="w-full px-4 py-2 tracking-wide text-white transition-colors duration-200 transform bg-blue-500 rounded-md hover:bg-blue-600 focus:outline-none focus:bg-blue-600"
                type="submit"
                disabled={errorPass || errorRepeatPass}
              >
                Sign up
              </button>
            </div>
          </form>

          <p className="mt-8 text-sm font-light text-center text-gray-700">
            {" "}
            Already have an account?{" "}
            <button
              className="font-medium text-blue-600 hover:underline"
              onClick={() => setFormSignIn(!formSignIn)}
            >
              Sign in
            </button>
          </p>
        </div>
      </div>
    );
  }
};

const renderErrorMessage = (error) => {
  return <div className="text-red-600 font-medium underline">{error}</div>;
};

export default Landing;
