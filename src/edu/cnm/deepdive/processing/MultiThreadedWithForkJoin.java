package edu.cnm.deepdive.processing;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadedWithForkJoin extends DataProcessor {

  private static final int FORK_THRESHOLD = 10_000;

  private double logSum;

  @Override
  public void process() throws InterruptedException {
    ForkJoinPool pool = new ForkJoinPool();
    logSum = pool.invoke(new Task(getData(), 0, getData().length));
  }

  @Override
  public double getGeometricMean() {
    return Math.exp(logSum / getData().length);
  }

  private static class Task extends RecursiveTask<Double> {

    private final int[] data;
    private final int start;
    private final int end;

    private Task(int[] data, int start, int end) {
      this.data = data;
      this.start = start;
      this.end = end;
    }

    @Override
    protected Double compute() {
      double sum;
      if (end - start < FORK_THRESHOLD) {
        sum = 0;
        for (int i = start; i < end; i++) {
          sum += Math.log(data[i]);
        }
      } else {
        int midpoint = (start + end) / 2;
        Task left = new Task(data, start, midpoint);
        left.fork();
        Task right = new Task(data, midpoint, end);
        sum = right.compute() + left.join();
      }
      return sum;
    }
  }
}
