package com.github.applejuiceyy.automa.client.lua.api.listener;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

@LuaConvertible
public class Event extends Listener {
    public Event(LuaExecutionFacade owner) {
        super(owner);
    }

    @LuaConvertible
    public void fire(Varargs var) {
        super.fire(var);
    }

    static Supplier<Event> with(LuaExecutionFacade owner) {
        return () -> new Event(owner);
    }

    // called with reflection
    static Supplier<Event> getInstanceFactory(LuaExecutionFacade owner) {
        return with(owner);
    }
}
