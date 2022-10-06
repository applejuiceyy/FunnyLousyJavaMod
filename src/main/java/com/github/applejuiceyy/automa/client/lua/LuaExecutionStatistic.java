package com.github.applejuiceyy.automa.client.lua;

import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaExecutionStatistic {
    private final LuaExecutionFacade owner;
    private final LuaValue debuglib;

    public LuaExecutionStatistic(LuaExecutionFacade owner) {
        this.owner = owner;
        LuaExecutionStatistic o = this;
        debuglib = this.owner.globals.get("debug");
    }

    void processLine(int line) {
        System.out.println(debuglib.get("getinfo").call(LuaValue.valueOf(1)));
    }

    public LuaValue generateCustomCoroutineCreate() {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return new LuaThread(owner.globals, new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        debuglib.get("sethook").call(new TwoArgFunction() {
                            @Override
                            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                                processLine(arg2.toint());
                                return LuaValue.NIL;
                            }
                        }, LuaValue.valueOf("l"));
                        return arg.invoke(args);
                    }
                });
            }
        };
    }
}
