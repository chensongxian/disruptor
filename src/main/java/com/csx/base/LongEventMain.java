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
        /**
         //BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
         WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
         //SleepingWaitStrategy 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
         WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
         //YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
         WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
         */

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

//        LongEventProducer producer=new LongEventProducer(ringBuffer);
        LongEventProducerWithTranslator producer=new LongEventProducerWithTranslator(ringBuffer);

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
