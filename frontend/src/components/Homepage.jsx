import { useEffect } from "react";
import { useLocalState } from "../hooks/useLocalStorage";

function Homepage() {
  const [user, setUser] = useLocalState(null, "user");
  useEffect(() => {
    document.title = "SCRUM board";
  });

  return (
    <>
      <h1 className="text-4xl font-bold text-green-700 text-center mt-10">
        {user ? `Welcome, ${user.firstname}! ` : ""}You are on Homepage of the
        project!
      </h1>
    </>
  );
}

export default Homepage;
