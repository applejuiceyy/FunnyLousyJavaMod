package com.github.applejuiceyy.automa.client.lua.entrypoint;

import com.github.applejuiceyy.automa.client.lua.LuaExecution;

import java.util.function.Consumer;

public interface AutomationEntrypoint {
    default void loadClasses(Consumer<Class<?>> consumer) {}
    default void loadRuntime(LuaExecution executor) {}
}
