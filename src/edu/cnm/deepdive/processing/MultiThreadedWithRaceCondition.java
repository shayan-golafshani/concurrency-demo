package edu.cnm.deepdive.processing;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedWithRaceCondition extends
    DataProcessor {

  private static final int NUM_TASKS = Runtime.getRuntime().availableProcessors();

  private double logSum;

  @Override
  public void process() throws InterruptedException {

    logSum = 0;
    int[] data = getData();
    int length = data.length;
    ExecutorService pool = Executors.newFixedThreadPool(NUM_TASKS);
    CountDownLatch latch = new CountDownLatch(NUM_TASKS);
    for (int i = 0; i< NUM_TASKS; i++){
      int start = i * length / NUM_TASKS;
      int end = (i + 1) * length / NUM_TASKS;
      pool.submit(() -> {
        for (int j = start; j < end; j++){
          logSum += Math.log(data[j]);
        }
        latch.countDown();
      });
    }
    latch.await();
    pool.shutdown();
  }

  @Override
  public double getGeometricMean()
  {
    return Math.exp(logSum/getData().length);
  }
}
