package com.csx.base;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @Author: csx
 * @Date: 2018-01-14
 */
public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;
    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb){
        //1. 可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
        long sequence=ringBuffer.next();
        try {
            //2. 用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
            LongEvent event=ringBuffer.get(sequence);
            //3. 获取要通过事件传递的业务数据
            event.setValue(bb.getLong(0));
        }finally {
            //4. 发布事件
            //注意，最后的ringBuffer.publish方法必须包含在finally中以确保必须得到调用
            //如果某个请求的sequence未提交，将会堵塞后续的发布操作或者其他的producer
            ringBuffer.publish(sequence);
        }
    }


}
