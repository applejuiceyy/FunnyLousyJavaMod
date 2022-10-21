package com.github.applejuiceyy.automa.client.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaExecutionDebugger {
    private final LuaExecutionFacade owner;
    private final LuaValue debuglib;

    public LuaExecutionDebugger(LuaExecutionFacade owner) {
        this.owner = owner;
        debuglib = this.owner.globals.get("debug");
    }

    void processLine(int line) {
        LuaTable value = (LuaTable) debuglib.get("getinfo").call(LuaValue.valueOf(1), LuaValue.valueOf("S"));

        System.out.println("processline:");
        LuaValue[] keys = value.keys();
        for (LuaValue key : keys) {
            System.out.println("    " + key + ": " + value.get(key));
        }
    }

    public LuaValue generateCustomCoroutineCreate() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return createCoroutine(arg);
            }
        };
    }

    public LuaThread createCoroutine(LuaValue func) {
        return new LuaThread(owner.globals, func);

        /*return new LuaThread(owner.globals, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                debuglib.get("sethook").call(new TwoArgFunction() {
                    @Override
                    public LuaValue call(LuaValue arg1, LuaValue arg2) {
                        processLine(arg2.toint());
                        return LuaValue.NIL;
                    }
                }, LuaValue.valueOf("l"));
                return func.invoke(args);
            }
        })*/
    }
}
