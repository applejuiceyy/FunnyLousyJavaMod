package com.github.applejuiceyy.automa.client.lua.entrypoint;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;

import java.util.function.Consumer;

public interface AutomationEntrypoint {
    default void loadClasses(Consumer<Class<?>> consumer) {};
    default void loadRuntime(LuaExecutionFacade executor) {};
}
