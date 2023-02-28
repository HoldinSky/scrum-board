import { useEffect, useState } from "react";
import useAxiosPrivate from "../hooks/useAxiosPrivate";
import { useLocalState } from "../hooks/useLocalStorage";

const Users = () => {
  const [auth, setAuth] = useLocalState(null, "auth");
  const [users, setUsers] = useState();
  const axiosPrivate = useAxiosPrivate();

  // initialization processes
  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();

    const getUsers = async () => {
      try {
        const response = await axiosPrivate.get("/api/user", {
          signal: controller.signal,
        });
        console.log(response.data);
        isMounted && setUsers(response.data);
      } catch (exc) {
        console.log(exc);
      }
    };

    getUsers();
    return () => {
      isMounted = false;
      controller.abort();
    };
  }, []);

  return (
    <>
      <h2 className="text-center p-3 text-xl text-cyan-600 font-semibold underline">
        Users list
      </h2>
      {users?.length ? (
        <ul>
          {users.map((user, i) => (
            <li
              className="text-center py-1 px-3 text-lg text-yellow-500"
              key={i}
            >
              {user.username}
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-center p-3 text-red-500">
          There are no users to list
        </p>
      )}
    </>
  );
};

export default Users;
