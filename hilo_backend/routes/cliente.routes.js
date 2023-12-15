const express = require("express");
const router = express.Router();
const { Cliente } = require("../models/index");

//lista de los clientes
router.get("/", async (req, res) => {
  try {
    setTimeout(async () => {
      const clientes = await Cliente.findAll({
        attributes: ["idCliente"],
      });
      res.status(200).json(clientes);
    }, 5000);
  } catch (error) {
    res.status(500).json({
      error,
    });
  }
});

module.exports = router;
