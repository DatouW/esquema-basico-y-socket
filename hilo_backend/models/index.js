const Cliente = require("./Cliente");
const Deuda = require("./Deuda");
const Pago = require("./Pago");

// Definir relaciones
Cliente.hasMany(Deuda, { foreignKey: "clienteId", sourceKey: "idCliente" });
Deuda.belongsTo(Cliente, { foreignKey: "clienteId", targetKey: "idCliente" });

Deuda.hasMany(Pago, { foreignKey: "deudaId", sourceKey: "idDeuda" });
Pago.belongsTo(Deuda, { foreignKey: "deudaId", targetKey: "idDeuda" });

module.exports = {
  Cliente,
  Deuda,
  Pago,
};
