import { Link } from "react-router-dom";
import Users from "./Users";

const AdminPanel = () => {
  return (
    <>
      <h1 className="text-center p-3 text-2xl text-blue-500 font-semibold">
        Admins Page
      </h1>
      <Users />
      <br />
      <div className="text-center hover:font-semibold">
        <Link to="/">Home</Link>
      </div>
    </>
  );
};

export default AdminPanel;
