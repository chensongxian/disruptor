package com.csx.generate1;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @Author: csx
 * @Date: 2018/01/15
 */
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        int BUFFER_SIZE=1024;
        int THREAD_NUMBER=4;

        EventFactory<Trade> eventFactory=new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        };

        RingBuffer<Trade> ringBuffer=RingBuffer.createSingleProducer(eventFactory,BUFFER_SIZE);

        SequenceBarrier barrier=ringBuffer.newBarrier();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        WorkHandler<Trade> workHandler=new TradeHandler();

        WorkerPool<Trade> workerPool=new WorkerPool<Trade>(ringBuffer,barrier,new IgnoreExceptionHandler(),workHandler);

        workerPool.start(executor);

        //下面这个生产8个数据
        for(int i=0;i<8;i++){
            long seq=ringBuffer.next();
            ringBuffer.get(seq).setPrice(Math.random()*9999);
            ringBuffer.publish(seq);
        }

        Thread.sleep(1000);
        workerPool.halt();
        executor.shutdown();



    }
}
