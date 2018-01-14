package com.csx.base;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @Author: csx
 * @Date: 2018-01-14
 */
public class LongEventMain {
    public static void main(String[] args) {
        //创建缓冲池
        ExecutorService executors = Executors.newCachedThreadPool();
        //创建工厂
        LongEventFactory eventFactory=new LongEventFactory();

        //创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024;

        Disruptor<LongEvent> disruptor=new Disruptor<LongEvent>(eventFactory,ringBufferSize
                ,executors, ProducerType.SINGLE,new YieldingWaitStrategy());

    }
}
