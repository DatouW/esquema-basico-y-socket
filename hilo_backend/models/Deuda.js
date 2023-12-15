const { DataTypes } = require("sequelize");
const sequelize = require("../database/index");

const Deuda = sequelize.define(
  "deuda",
  {
    idDeuda: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true,
    },
    monto: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false,
      validate: {
        isNumeric: true,
        min: 0,
      },
    },
    saldo: {
      type: DataTypes.DECIMAL(10, 2),
      validate: {
        min: 0,
      },
    },
    pagada: {
      type: DataTypes.BOOLEAN,
      defaultValue: false,
    },
  },
  {
    timestamps: false,
  }
);

module.exports = Deuda;
