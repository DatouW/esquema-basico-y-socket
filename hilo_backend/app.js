const express = require("express");
const sequelize = require("./database/index");
const triggers = require("./database/triggers");
require("./models/index");

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Routes
app.use("/api/clientes", require("./routes/cliente.routes"));
app.use("/api/deudas", require("./routes/deuda.routes"));
app.use("/api/pagos", require("./routes/pago.routes"));

(async () => {
  try {
    //     await sequelize.authenticate();
    //     console.log("Connection has been established successfully.");
    await sequelize.sync();
    await triggers();
    console.log("Modelos sincronizados con la base de datos.");

    app.listen(3000, () => {
      console.log("Running on port 3000");
    });
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
})();
