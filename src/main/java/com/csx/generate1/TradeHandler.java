package com.csx.generate1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @Author: csx
 * @Date: 2018/01/15
 */
public class TradeHandler implements EventHandler<Trade>,WorkHandler<Trade>{

    public void onEvent(Trade event, long l, boolean b) throws Exception {
        onEvent(event);
    }

    public void onEvent(Trade event) throws Exception {
        //这里做具体的消费逻辑
        event.setId(UUID.randomUUID().toString());//简单生成下ID
        System.out.println(event.getId());
    }
}
