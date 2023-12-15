const express = require("express");
const { Deuda } = require("../models");
const router = express.Router();

// obtener las deudas dependientes de pago de un cliente
router.get("/:id", async (req, res) => {
  const { id } = req.params;

  try {
    const deudas = await Deuda.findAll({
      attributes: [
        ["idDeuda", "deudaId"],
        ["saldo", "monto"],
      ],
      where: {
        clienteId: id,
        pagada: false,
      },
    });
    res.status(200).json(deudas);
  } catch (error) {
    res.status(500).json({
      error,
    });
  }
});

module.exports = router;
