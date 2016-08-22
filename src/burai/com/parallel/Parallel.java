/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.parallel;

public class Parallel<E, R> {

    public static SumRule<Boolean> booleanAndRule() {
        return (b1, b2) -> {
            boolean b3 = false;
            b3 = b3 && (b1 == null ? false : b1);
            b3 = b3 && (b2 == null ? false : b2);
            return b3;
        };
    }

    public static SumRule<Boolean> booleanOrRule() {
        return (b1, b2) -> {
            boolean b3 = false;
            b3 = b3 || (b1 == null ? false : b1);
            b3 = b3 || (b2 == null ? false : b2);
            return b3;
        };
    }

    public static SumRule<Integer> integerSumRule() {
        return (i1, i2) -> {
            int i3 = 0;
            i3 += i1 == null ? 0 : i1;
            i3 += i2 == null ? 0 : i2;
            return i3;
        };
    }

    public static SumRule<Double> doubleSumRule() {
        return (d1, d2) -> {
            double d3 = 0.0;
            d3 += d1 == null ? 0.0 : d1;
            d3 += d2 == null ? 0.0 : d2;
            return d3;
        };
    }

    private int numThreads;

    private int threadCounter;

    private E[] elements;

    private R result;

    private SumRule<R> sumRule;

    public Parallel(E[] elements) {
        if (elements == null) {
            throw new IllegalArgumentException("elements is null.");
        }

        this.numThreads = 1;
        this.threadCounter = 0;
        this.elements = elements;
        this.result = null;
        this.sumRule = null;
    }

    public synchronized void setNumThreads(int numThreads) {
        this.numThreads = Math.max(1, numThreads);
    }

    public synchronized R getResult() {
        return this.result;
    }

    public synchronized void setSumRule(SumRule<R> sumRule) {
        this.sumRule = sumRule;
    }

    public synchronized R forEach(Performance<E, R> performance) {
        if (performance == null) {
            return null;
        }

        this.threadCounter = 0;
        int numThreads2 = this.numThreads;

        for (int iThread = 0; iThread < numThreads2; iThread++) {
            this.forEachKernel(iThread, numThreads2, performance);
        }

        while (this.threadCounter < numThreads2) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return this.result;
    }

    private void forEachKernel(int iThread, int numThreads, Performance<E, R> performance) {
        Thread thread = new Thread(() -> {
            R result1 = null;
            for (int iElement = 0; iElement < this.elements.length; iElement++) {
                if ((iElement % numThreads) == iThread) {
                    E element = this.elements[iElement];
                    R result2 = performance.perform(element);
                    if (this.sumRule != null) {
                        result1 = this.sumRule.sum(result1, result2);
                    }
                }
            }

            synchronized (this) {
                this.threadCounter++;
                if (this.sumRule != null) {
                    this.result = this.sumRule.sum(this.result, result1);
                }

                this.notifyAll();
            }
        });

        thread.start();
    }
}
