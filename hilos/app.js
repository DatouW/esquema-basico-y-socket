const { isMainThread } = require("worker_threads");
const ThreadManager = require("./ThreadManager");

if (isMainThread) {
  const manager = new ThreadManager(10);
  manager.startAllThreads();
}
