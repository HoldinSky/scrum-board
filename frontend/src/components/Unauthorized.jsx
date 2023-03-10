import { useNavigate } from "react-router-dom";

const Unauthorized = () => {
  const navigate = useNavigate();

  const goBack = () => navigate(-1);

  return (
    <>
      <h1 className="text-2xl text-red-600 text-center font-bold">
        Unauthorized
      </h1>
      <br />
      <p className="text-center text-red-400 font-semibold">
        You do not have access to the requested page.
      </p>
      <div className="text-center">
        <button
          className="text-blue-500 hover:underline hover:cursor-pointer"
          onClick={goBack}
        >
          Go Back
        </button>
      </div>
    </>
  );
};

export default Unauthorized;
