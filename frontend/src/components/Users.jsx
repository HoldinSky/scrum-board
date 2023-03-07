import { useEffect, useState } from "react";
import useAxiosPrivate from "../hooks/useAxiosPrivate";

const Users = () => {
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
        // if an error occurs -> refresh token has expired, renavigate to '/authenticate page'
        console.log(exc);
      }
    };

    getUsers();
    return () => {
      isMounted = false;
      controller.abort();
    };
  }, [axiosPrivate]);

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
