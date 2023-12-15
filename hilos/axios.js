const axios = require("axios");

const axiosInstance = axios.create({
  baseURL: "http://192.168.43.66:3000/api",
  timeout: 5000, // Establece el tiempo máximo de espera en milisegundos (5 segundos en este caso)
  retry: 3, // Número de reintentos automáticos en caso de fallo
  retryDelay: 1000, // Tiempo de espera entre reintentos en milisegundos (1 segundo en este caso)
});

const retryRequest = async (config) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(axiosInstance(config));
    }, config.retryDelay);
  });
};

// Interceptores
axiosInstance.interceptors.response.use(
  (res) => res,
  (error) => {
    const { config } = error;

    if (config.retry > 0) {
      config.retry = config.retry - 1;

      // la solicitud ha excedido el tiempo de espera
      if (error.code === "ECONNABORTED") {
        if (config.url === "/pagos") {
          const obj = JSON.parse(config.data);
          console.log(
            `Request timeout. Retrying... ${config.url} 
                 ===> nro trans. ${obj.idPago}`
          );
        } else {
          console.log("Request timeout. Retrying...", config.url);
        }
      } else if (error.code === "ECONNREFUSED") {
        console.log("Connection refused. Retrying... ", config.url);
      } else if (error.code === "ERR_BAD_RESPONSE") {
        console.log("Internal Server Error. Retrying... ", config.url);
      } else if (error.code === "ERR_BAD_REQUEST") {
        console.log("Bad Request. Retrying... ", config.url);
      } else {
        console.log("Retrying...", error);
      }
      return retryRequest(config);
    } else {
      console.log(
        "Número máximo de reintentos alcanzado. Error:",
        error.message
      );
      return Promise.reject(error.code);
    }
  }
);

module.exports = axiosInstance;
