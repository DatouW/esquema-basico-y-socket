const sequelize = require(".");

async function triggers() {
  try {
    await sequelize.query(
      `
      CREATE OR REPLACE FUNCTION update_saldo()
      RETURNS TRIGGER AS $$
      BEGIN
        UPDATE deudas
        SET saldo = NEW.monto
        WHERE "idDeuda" = NEW."idDeuda"; 
        RETURN NEW;
      END;
      $$ LANGUAGE plpgsql;


      DO $$ 
      BEGIN
        IF NOT EXISTS (
          SELECT 1 
          FROM information_schema.triggers 
          WHERE event_object_table = 'deudas'
            AND trigger_name = 'set_saldo_inicial'
        ) THEN
          
          CREATE TRIGGER set_saldo_inicial
            AFTER INSERT ON deudas
            FOR EACH ROW
            EXECUTE FUNCTION update_saldo();
        
        END IF;
        
      END $$;     
`
    );

    await sequelize.query(
      `
    CREATE OR REPLACE FUNCTION verificar_pago()
    RETURNS TRIGGER AS $$
    DECLARE 
        estado BOOLEAN;
        saldo_anterior DECIMAL(10, 2);
    BEGIN
      -- Obtener el saldo de la tabla deuda
      SELECT pagada,saldo INTO estado,saldo_anterior
      FROM deudas
      WHERE "idDeuda" = NEW."deudaId"; 

      IF estado = TRUE THEN
        RAISE EXCEPTION 'Deuda % ya ha sido pagada', NEW."deudaId";
      END IF;

      IF NEW.monto = saldo_anterior THEN
        UPDATE deudas
        SET pagada = true, saldo = 0
        WHERE "idDeuda" = NEW."deudaId";
      ELSIF NEW.monto < saldo_anterior THEN
        UPDATE deudas
        SET saldo = saldo_anterior - NEW.monto 
        WHERE "idDeuda" = NEW."deudaId";
      ELSE
        RAISE EXCEPTION 'Monto incorrecto';
    END IF;
      
      RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;
   
    DO $$ 
      BEGIN
        IF NOT EXISTS (
          SELECT 1 
          FROM information_schema.triggers 
          WHERE event_object_table = 'pagos'
            AND trigger_name = 'before_insert_pagos'
        ) THEN
          
        CREATE TRIGGER before_insert_pagos
        BEFORE INSERT ON pagos 
        FOR EACH ROW
        EXECUTE FUNCTION verificar_pago();
        
        END IF;
      END $$;    
      
`
    );
  } catch (error) {
    console.log(error);
  }
}

module.exports = triggers;
