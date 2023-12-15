const { Sequelize } = require("sequelize");
require("dotenv").config();

const sequelize = new Sequelize(
  process.env.DB_NAME,
  process.env.DB_USER,
  process.env.DB_PASS,
  {
    host: process.env.DB_HOST,
    dialect: "postgres",
    pool: {
      max: 20, // Tamaño máximo del pool de conexiones
      // acquire: 30000, // Tiempo máximo para adquirir una conexión
      // idle: 10000, // Tiempo máximo de inactividad de una conexión antes de ser liberada
    },
  }
);

module.exports = sequelize;
