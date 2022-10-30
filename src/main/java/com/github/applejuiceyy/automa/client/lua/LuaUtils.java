package com.github.applejuiceyy.automa.client.lua;

import org.luaj.vm2.LuaError;

public class LuaUtils {
    public static LuaError wrapException(Throwable exc) {
        if (exc instanceof LuaError err) {
            return err;
        }
        return new LuaError(exc);
    }
}
