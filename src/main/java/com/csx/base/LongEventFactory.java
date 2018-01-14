package com.csx.base;

import com.lmax.disruptor.EventFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 需要让disruptor为我们创建事件，我们同时还声明了一个EventFactory来实例化Event对象。
 * @Author: csx
 * @Date: 2018-01-14
 */
public class LongEventFactory implements EventFactory{
    public Object newInstance() {
        return new LongEvent();
    }
}
