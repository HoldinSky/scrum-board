import { useEffect } from "react";
import { useLocalState } from "../hooks/useLocalStorage";
import { Link } from "react-router-dom";

function Homepage() {
  const [auth, setAuth] = useLocalState(null, "auth");
  useEffect(() => {
    document.title = "SCRUM board";
  });

  return (
    <>
      <h1 className="text-4xl font-bold text-green-700 text-center mt-10">
        {auth ? `Welcome, ${auth.user.firstname}! ` : ""}You are on Homepage of
        the project!
      </h1>
      <div className="text-center py-3 px-4">
        <Link
          className="text-xl font-semibold text-yellow-400 hover:underline"
          to={"/about"}
        >
          Link to the learn more about project
        </Link>
      </div>
      <div className="text-center py-3 px-4">
        <Link
          className="text-xl font-semibold text-cyan-500 hover:underline"
          to={"/admin"}
        >
          Link to the admin panel
        </Link>
      </div>
      <div className="text-center py-3 px-4">
        <Link
          className="text-xl font-semibold text-yellow-400 hover:underline"
          to={"/authenticate"}
        >
          Log in
        </Link>
      </div>
      <div className="text-center py-3 px-4">
        <p className="text-2xl font-bold text-blue-600">
          {auth ? `User is: ${JSON.stringify(auth.user)}` : ""}
        </p>
      </div>
    </>
  );
}

export default Homepage;
