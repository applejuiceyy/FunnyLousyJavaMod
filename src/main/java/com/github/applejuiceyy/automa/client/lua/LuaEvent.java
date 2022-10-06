package com.github.applejuiceyy.automa.client.lua;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

@LuaConvertible
public class LuaEvent {
    private final LinkedHashMap<Integer, LuaValue> subscribers;
    private final LuaExecutionFacade owner;
    private int next = 0;

    LuaEvent(LuaExecutionFacade owner) {
        this.owner = owner;
        subscribers = new LinkedHashMap<>();
    }

    @LuaConvertible
    public Runnable subscribe(LuaValue value) {
        int id = next++;
        subscribers.put(id, value);
        return () -> {
            System.out.println("cheese");
            subscribers.remove(id);
        };
    }

    @LuaConvertible
    public void fire(Varargs var) {
        for (LuaValue sub: subscribers.values().stream().toList()) {
            owner.manageCoroutine(new LuaThread(this.owner.globals, new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    sub.invoke(var);
                    return LuaValue.NIL;
                }
            }));
        }
    }

    static Supplier<LuaEvent> with(LuaExecutionFacade owner) {
        return () -> new LuaEvent(owner);
    }

    // called with reflection
    static Supplier<LuaEvent> getInstanceFactory(LuaExecutionFacade owner) {
        return with(owner);
    }

    @LuaConvertible
    public static class CancellationState {
        boolean cancelled;

        public CancellationState() {
            cancelled = false;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
        }
    }
}
