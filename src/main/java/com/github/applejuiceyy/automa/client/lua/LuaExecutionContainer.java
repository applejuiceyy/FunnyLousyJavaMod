package com.github.applejuiceyy.automa.client.lua;

import org.jetbrains.annotations.Nullable;

public class LuaExecutionContainer {
    @Nullable
    static LuaExecution currentExecutor = null;

    public static void setExecutor(LuaExecution executor) {
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

    public static LuaExecution getExecutor() {
        return currentExecutor;
    }

    public static boolean hasExecutor() {
        return currentExecutor != null;
    }
}
