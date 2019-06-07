package com.example.demo.thread;

public class ThreadNotify implements Runnable {
    private Object lock;

    public ThreadNotify(Object lock) {
        super();
        this.lock = lock;
    }

    public synchronized void notifyOthers(){
        synchronized (lock){
            lock.notifyAll();
        }
    }
    public void run() {
        notifyOthers();
    }
}
