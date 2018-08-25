import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class ReaderWork implements Runnable {
    private Buffer buffer;
    private LogFile logFile;

    ReaderWork(final Buffer buffer, final LogFile logFile) {
        this.buffer = buffer;
        this.logFile = logFile;
    }

    @Override
    public void run() {
        int page = 0;

        try {
            logFile.writeToFile("Wait filledSemaphore");
            buffer.acquireFilledSemaphore();
            logFile.writeToFile("Acquire filledSemaphore");

            synchronized (this) {
                for (; ; ) {
                    page = buffer.findFilledCell();
                    if (page > -1 && !buffer.isLocked(page))
                        break;
                    //logFile.writeToFile("Filled page not found. Sleeping");
                    //Thread.sleep(300);
                }
                logFile.writeToFile("Found filled page # " + page);
                buffer.lockLock(page);
            }
            logFile.writeToFile("Lock page # " + page);

            int readNum = buffer.readNum(page);
            Random random = new Random();
            int randSleepTime = 1 + random.nextInt(3);
            TimeUnit.SECONDS.sleep(randSleepTime);
            logFile.writeToFile("Read " + readNum + " from buffer page # " + page);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            buffer.unlockLock(page);
            buffer.releaseEmptySemaphore();
            buffer.countDown();
        }
    }
}