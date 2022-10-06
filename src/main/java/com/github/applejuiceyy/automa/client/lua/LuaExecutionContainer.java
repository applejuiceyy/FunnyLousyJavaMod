package com.github.applejuiceyy.automa.client.lua;

import org.jetbrains.annotations.Nullable;

public class LuaExecutionContainer {
    @Nullable
    static LuaExecutionFacade currentExecutor = null;

    public static void setExecutor(LuaExecutionFacade executor) {
        ensureStopped();
        currentExecutor = executor;
    }

    public static void ensureStopped() {
        if(currentExecutor != null) {
            currentExecutor.stop();
            currentExecutor = null;
        }
    }

    public static void stopExecutor() {
        setExecutor(null);
    }

    public static LuaExecutionFacade getExecutor() {
        return currentExecutor;
    }

    public static boolean hasExecutor() {
        return currentExecutor != null;
    }
}
