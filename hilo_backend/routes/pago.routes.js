const express = require("express");
const { Pago } = require("../models");
const router = express.Router();

// Routes
router.post("/", async (req, res) => {
  const { idPago, deudaId, monto } = req.body;
  try {
    if (idPago && deudaId && monto) {
      // setTimeout(async () => {
      //   try {
      const exist = await Pago.findByPk(idPago);
      if (exist) {
        res
          .status(200)
          .json(
            `Deuda ${deudaId} ha sido pagada con éxito --- nro trans. ${exist.idPago}`
          );
      } else {
        const pago = await Pago.create({ idPago, deudaId, monto });
        res
          .status(200)
          .json(
            `Deuda ${pago.deudaId} ha sido pagada con éxito --- nro trans. ${pago.idPago}`
          );
      }
      //   } catch (error) {
      //     res.status(500).send(error);
      //   }
      // }, 4996);
    } else {
      res.sendStatus(400);
    }
  } catch (error) {
    console.log("pago post: ", error.message);
    if (error.parent?.code === "P0001") {
      if (error.message === "Monto incorrecto") {
        return res.sendStatus(400);
      } else {
        return res.status(200).json(error.message);
      }
    } else
      res.status(500).json({
        error,
      });
  }
});

router.post("/bulk", async (req, res) => {
  const { deudas } = req.body;

  try {
    if (deudas) {
      await Pago.bulkCreate(deudas);
      res.status(200).json("pago con exito");
    } else {
      res.sendStatus(400);
    }
  } catch (error) {
    console.log(error);
    if (error.parent.code === "P0001")
      return res.status(200).json(error.message);
    res.status(500).json({
      message: error,
    });
  }
});

module.exports = router;
