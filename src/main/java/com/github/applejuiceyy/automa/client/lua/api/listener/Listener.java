package com.github.applejuiceyy.automa.client.lua.api.listener;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.LinkedHashMap;

@LuaConvertible
public class Listener {
    protected final LuaExecutionFacade owner;

    private final LinkedHashMap<Integer, LuaValue> subscribers;
    private int next = 0;

    Listener(LuaExecutionFacade owner) {
        this.owner = owner;
        subscribers = new LinkedHashMap<>();
    }

    @LuaConvertible
    public Runnable subscribe(LuaValue value) {
        int id = next++;
        subscribers.put(id, value);
        return () -> subscribers.remove(id);
    }
    @LuaConvertible
    public Runnable subscribeOnce(LuaValue value) {
        int id = next++;
        subscribers.put(id, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                subscribers.remove(id);
                value.invoke(args);
                return super.invoke(args);
            }
        });
        return () -> subscribers.remove(id);
    }

    @LuaConvertible
    protected void fire(Varargs var) {
        for (LuaValue sub: subscribers.values().stream().toList()) {
            owner.manageCoroutine(owner.createCoroutine(new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    sub.invoke(var);
                    return LuaValue.NIL;
                }
            }));
        }
    }
}
