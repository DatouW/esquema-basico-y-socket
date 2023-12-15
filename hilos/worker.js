const { parentPort } = require("worker_threads");
const {
  elegirClienteAleatorio,
  getDeudas,
  deudasAPagar,
  pagarDeuda,
  getClientes,
} = require("./utils");

if (parentPort) {
  parentPort.on("message", async (message) => {
    if (message === "start") {
      try {
        const { data: clientes } = await getClientes();

        while (true) {
          const id = elegirClienteAleatorio(clientes);
          const deudas = await getDeudas(id);
          if (deudas.length !== 0) {
            const deudasArr = deudasAPagar(deudas);

            for (let deuda of deudasArr) {
              const data = await pagarDeuda(deuda);
              parentPort.postMessage(`Cliente ${id} --- ${data}`);
              // sleep(2000);
              await sleep(1000);
            }
          } else {
            await sleep(2000);
            parentPort.postMessage(
              `Cliente ${id} --- no tiene deudas pendientes por pagar.`
            );
          }
        }
      } catch (error) {
        parentPort.postMessage({ type: "error", error });
      }
    }
  });
}

// function sleep(ms) {
//   const start = Date.now();
//   while (Date.now() - start < ms) {}
// }

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
