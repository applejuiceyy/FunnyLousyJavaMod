package com.github.applejuiceyy.automa.client.lua;

import org.luaj.vm2.OrphanedThread;

// imagine existing only because of luaJ's coroutines as threads
public class LuaExecutionAttempt {
    private final Runnable run;
    private final Object hostSideInterrupt = new Object();
    private final Object codeSideInterrupt = new Object();
    private Runnable executor;
    private RuntimeException exc;

    LuaExecutionAttempt(Runnable run) {
        this.run = run;
    }

    public void execute() {
        Thread thread = new Thread(null, () -> {
            try {
                run.run();
            } catch (RuntimeException err) {
                exc = err;
            }

            synchronized (hostSideInterrupt) {
                hostSideInterrupt.notify();
            }
        }, "execution-attempt");

        synchronized (hostSideInterrupt) {
            thread.start();
            try {
                hostSideInterrupt.wait();
            } catch (InterruptedException e) {
                throw new OrphanedThread();
            }
        }

        while (executor != null) {
            if (exc != null) {
                throw exc;
            }
            executor.run();
            executor = null;
            synchronized (hostSideInterrupt) {
                synchronized (codeSideInterrupt) {
                    codeSideInterrupt.notify();
                }
                try {
                    hostSideInterrupt.wait();
                } catch (InterruptedException e) {
                    throw new OrphanedThread();
                }
            }
        }

        if (exc != null) {
            throw exc;
        }
    }

    public void executeInMain(Runnable execution) {
        executor = execution;
        synchronized (codeSideInterrupt) {
            synchronized (hostSideInterrupt) {
                hostSideInterrupt.notify();
            }

            try {
                codeSideInterrupt.wait();
            } catch (InterruptedException e) {
                throw new OrphanedThread();
            }
        }
    }
}
