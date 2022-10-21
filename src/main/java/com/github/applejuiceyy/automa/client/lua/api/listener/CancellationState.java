package com.github.applejuiceyy.automa.client.lua.api.listener;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;

@LuaConvertible
public class CancellationState {
    boolean cancelled;

    public CancellationState() {
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
    }
}