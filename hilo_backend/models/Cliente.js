const { DataTypes } = require("sequelize");
const sequelize = require("../database/index");

const Cliente = sequelize.define(
  "cliente",
  {
    idCliente: {
      type: DataTypes.UUID,
      primaryKey: true,
      defaultValue: DataTypes.UUIDV4,
    },
    nombre: {
      type: DataTypes.STRING,
      allowNull: false,
    },
  },
  {
    timestamps: false,
  }
);

module.exports = Cliente;
