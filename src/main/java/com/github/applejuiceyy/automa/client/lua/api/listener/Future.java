package com.github.applejuiceyy.automa.client.lua.api.listener;

import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.LuaValue;

import java.util.function.Supplier;

@LuaConvertible
public class Future<T> extends Listener {
    T value = null;
    boolean complete;

    public Future(LuaExecution owner) {
        super(owner);
    }
    @LuaConvertible
    public void complete(T v) {
        if (complete) {
            throw new FutureAlreadyCompletedException();
        }

        value = v;
        complete = true;
        this.fire(owner.boundary.J2L(v));
    }
    @LuaConvertible
    public boolean isComplete() {
        return complete;
    }
    @LuaConvertible
    public T getValue() { return value; }

    @Override
    @LuaConvertible
    public Runnable subscribe(LuaValue value) {
        if (complete) {
            value.invoke(owner.boundary.J2L(value));
            return () -> {};
        }

        return super.subscribe(value);
    }

    // called with reflection
    static <T> Supplier<Future<T>> getInstanceFactory(LuaExecution owner) {
        return () -> new Future<>(owner);
    }
    public static class FutureAlreadyCompletedException extends RuntimeException {}
}
