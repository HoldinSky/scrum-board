import { useEffect } from "react";
import useAuth from "../hooks/useAuth";

function Homepage() {
  const { auth } = useAuth();
  useEffect(() => {
    document.title = "SCRUM board";
  });

  return (
    <>
      <h1 className="text-4xl font-bold text-blue-500 text-center mt-10">
        {auth ? `Welcome, ${auth.user.firstname}! ` : ""}This page will tell You
        all about the project!
      </h1>
    </>
  );
}

export default Homepage;
