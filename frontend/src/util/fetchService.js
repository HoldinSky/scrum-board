import { domain } from "./domain";

const ajax = async (url, requestMethod, jwt, requestBody) => {
  const data = {
    headers: {
      "Content-Type": "application/json",
    },
    method: requestMethod,
  };

  if (jwt) {
    data.headers.Authorization = `Bearer ${jwt}`;
  }

  if (requestBody) {
    data.body = JSON.stringify(requestBody);
  }

  const response = await fetch(`${domain}${url}`, data);
  if (response.status === 200) return response.json();
};

export default ajax;
