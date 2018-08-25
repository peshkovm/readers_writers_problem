import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class WriterWork implements Runnable {
    private Buffer buffer;
    private LogFile logFile;

    WriterWork(final Buffer buffer, final LogFile logFile) {
        this.buffer = buffer;
        this.logFile = logFile;
    }

    @Override
    public void run() {
        int page = 0;

        try {
            logFile.writeToFile("Wait emptySemaphore");
            buffer.acquireEmptySemaphore();
            logFile.writeToFile("Acquire emptySemaphore");

            synchronized (this) {
                for (; ; ) {
                    page = buffer.findEmptyCell();
                    if (page > -1 && !buffer.isLocked(page))
                        break;
                }
                logFile.writeToFile("Found clear page # " + page);
                buffer.lockLock(page);
            }
            logFile.writeToFile("Lock page # " + page);

            Random random = new Random();
            int randNum = random.nextInt(100);
            buffer.writeNum(randNum, page);
            int randSleepTime = 1 + random.nextInt(3);
            TimeUnit.SECONDS.sleep(randSleepTime);
            logFile.writeToFile("Write " + randNum + " to buffer page # " + page);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            buffer.unlockLock(page);
            buffer.releaseFilledSemaphore();
            buffer.countDown();
        }
    }
}