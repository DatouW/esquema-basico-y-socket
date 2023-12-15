const { DataTypes } = require("sequelize");
const sequelize = require("../database/index");

const Pago = sequelize.define(
  "pago",
  {
    idPago: {
      type: DataTypes.STRING,
      primaryKey: true,
    },
    monto: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false,
      validate: {
        isNumeric: true,
        min: 0,
      },
    },
  },
  {
    // tableName: "pago",
    timestamps: true,
    updatedAt: false,
  }
);

module.exports = Pago;
