const axiosInstance = require("./axios");
const crypto = require("crypto");
const { v4: uuidv4 } = require("uuid");

const PORCENTAJE = [0.5, 0.8, 1];

//generar un numero aleatorio entero entre [min, max-1]
const getRandomInt = (min, max) => {
  return Math.floor(Math.random() * (max - min)) + min;
};

const generateId = () => {
  const hash = crypto.createHash("sha256");
  const idUnico = hash.update(uuidv4()).digest("hex");
  // console.log(idUnico);
  return idUnico;
};
// Función para obtener la lista de clientes
const getClientes = () => {
  return axiosInstance.get("/clientes");
};

// Función para elegir aleatoriamente un cliente
const elegirClienteAleatorio = (clientes) => {
  const index = getRandomInt(0, clientes.length);
  return clientes[index].idCliente;
};

// Función para obtener las deudas de un cliente
const getDeudas = async (idCliente) => {
  try {
    const { data } = await axiosInstance.get(`/deudas/${idCliente}`);
    return data;
  } catch (error) {
    console.error(
      `Error al obtener las deudas para ${idCliente}: ${handleError(error)}`
    );
    return [];
  }
};

// Función para pagar las deudas de un cliente
const deudasAPagar = (deudas) => {
  const numDeudas = getRandomInt(1, deudas.length);
  let arr = [];
  for (let i = 0; i < numDeudas; i++) {
    const porc = PORCENTAJE[getRandomInt(0, PORCENTAJE.length)];
    arr.push({
      ...deudas[i],
      monto: (deudas[i].monto * porc).toFixed(2),
    });
  }
  return arr;
};

const pagarDeuda = async (deuda) => {
  try {
    const { data } = await axiosInstance.post("/pagos", {
      ...deuda,
      idPago: generateId(),
    });

    return data;
  } catch (error) {
    return handleError(error);
  }
};

const pagarDeudas = async (deudas) => {
  const numDeudas = getRandomInt(1, deudas.length);
  //pagar n deudas
  const arr = [];
  for (let i = 0; i < numDeudas; i++) {
    const porc = PORCENTAJE[getRandomInt(0, PORCENTAJE.length)];
    arr.push({
      ...deudas[i],
      monto: (deudas[i].monto * porc).toFixed(2),
    });
  }
  try {
    const { data } = await axiosInstance.post("/pagos", {
      deudas: arr,
    });
    console.log(data);
  } catch (error) {
    console.log(error.message);
  }
};

const handleError = (error) => {
  if (
    error === "ECONNABORTED" ||
    error === "ECONNREFUSED" ||
    error === "ERR_BAD_RESPONSE"
  ) {
    return "Por favor, inténtalo nuevamente más tarde. ";
  } else if (error === "ERR_BAD_REQUEST") {
    return "Datos incorrectos o faltantes. Por favor, verifica y vuelve a intentarlo.";
  } else {
    return error;
  }
};
module.exports = {
  getClientes,
  elegirClienteAleatorio,
  getDeudas,
  deudasAPagar,
  pagarDeuda,
};
