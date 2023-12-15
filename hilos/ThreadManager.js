const { Worker } = require("worker_threads");

class ThreadManager {
  constructor(numThreads) {
    this.threads = [];
    this.addThread(numThreads);
  }

  addThread(numThreads) {
    for (let i = 0; i < numThreads; i++) {
      const worker = new Worker("./worker.js");
      this.setupWorker(worker); // Configurar el manejo de mensajes del hilo secundario
      this.threads.push(worker);
    }
  }

  setupWorker(worker) {
    worker.on("message", (msg) => {
      // Manejar los mensajes recibidos del hilo secundario
      if (msg.type === "error") {
        console.error(`Thread ${worker.threadId} detenido: ${msg.error} `);
        this.terminateThread(worker);
      } else {
        // Otros tipos de mensajes del hilo secundario
        console.log(`Thread ${worker.threadId}: ${msg}`);
      }
    });
  }

  startThread(thread) {
    thread.postMessage("start");
  }

  startAllThreads() {
    this.threads.forEach((thread) => {
      this.startThread(thread);
    });
  }

  terminateThread(thread) {
    thread.terminate();
    this.threads = this.threads.filter((t) => t !== thread);
  }

  terminateAllThreads() {
    for (let thread of this.threads) {
      thread.terminate();
    }
    this.threads = [];
  }
}

module.exports = ThreadManager;
