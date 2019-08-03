package com.cimcorp.plc.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Message {
    private List<String> msg;
    private ReentrantLock l;

    public Message(){
        this.msg = new ArrayList<>();
        this.l = new ReentrantLock();
    }

    public String getNextMsg() {
        while (l.isLocked()){}
        l.lock();
        String m = msg.get(0);
        msg.remove(0);
        l.unlock();
        return m;
    }

    public Message addMsg(String s) {
        while (l.isLocked()){}
        l.lock();
        this.msg.add(s);
        l.unlock();
        return this;
    }
    
    public boolean isEmpty() {
    	while (l.isLocked()){}
    	l.lock();
        boolean r = msg.isEmpty();
    	l.unlock();
    	return r;
    }
}
