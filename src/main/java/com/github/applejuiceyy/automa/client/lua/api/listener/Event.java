package com.github.applejuiceyy.automa.client.lua.api.listener;

import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.*;

import java.util.function.Supplier;

@LuaConvertible
public class Event extends Listener {
    public Event(LuaExecution owner) {
        super(owner);
    }

    @LuaConvertible
    public void fire(Varargs var) {
        super.fire(var);
    }

    static Supplier<Event> with(LuaExecution owner) {
        return () -> new Event(owner);
    }

    // called with reflection
    static Supplier<Event> getInstanceFactory(LuaExecution owner) {
        return with(owner);
    }
}
