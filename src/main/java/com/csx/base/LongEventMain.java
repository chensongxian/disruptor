package com.csx.base;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
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

        //创建disruptor
        //1第一个参数为工厂类对象，用于创建一个个的LongEvent，LongEvent是实际的消费数据
        //2第二个参数为缓存区大小
        //3第三个参数线程池进行Disruptor内部的数据接收处理调度
        //4第四个参数ProducerType.SINGLE和ProducerType.MULTI
        //5第五个参数是一种策略，WaitStrategy
        Disruptor<LongEvent> disruptor=new Disruptor<LongEvent>(eventFactory,ringBufferSize
                ,executors, ProducerType.SINGLE,new YieldingWaitStrategy());

        //连接消费事件方法
        disruptor.handleEventsWith(new LongEventHandler());

        disruptor.start();

        //Disruptor的事件发布过程是一个两阶段提交的过程
        //发布事件
        RingBuffer<LongEvent> ringBuffer=disruptor.getRingBuffer();

        LongEventProducer producer=new LongEventProducer(ringBuffer);

        ByteBuffer byteBuffer=ByteBuffer.allocate(8);
        for(long l=0;l<100;l++){
            byteBuffer.putLong(0,l);
            producer.onData(byteBuffer);
        }
        //关闭disruptor,方法会堵塞,直到所有的事件都得到处理
        disruptor.shutdown();
        //关闭disruptor使用的线程池
        executors.shutdown();
    }


}
