package com.github.applejuiceyy.automa.client.lua.api.controls;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.listener.Future;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

@LuaConvertible
public abstract class MissionCriticalLuaInterface<V extends MissionCritical> {
    protected final V owner;
    protected final LuaExecutionFacade execution;

    MissionCritical.MissionCriticalRevoker revoker;

    public MissionCriticalLuaInterface(LuaExecutionFacade f, V obj) {
        owner = obj;
        execution = f;
    }
    @LuaConvertible
    public Future<?> await() {
        Future<?> future = new Future<>(execution);
        owner.completeOnAvailable(future);
        return future;
    }
    @LuaConvertible
    public boolean tryRequest() {
        try {
            revoker = owner.tryRequest(execution.getName());
            return true;
        } catch (MissionCritical.MissionCriticalRequestException e) {
            return false;
        }
    }
    @LuaConvertible
    public void request() {
        Future<?> future = await();

        execution.globals.yield(new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                future.subscribe(new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        arg.checkthread().resume(null);
                        return null;
                    }
                });
                return null;
            }
        });

        tryRequest();
    }
    @LuaConvertible
    public void revoke() {
        revoker.revoke();
    }
    @LuaConvertible
    public boolean hasControl() {
        return revoker != null || !owner.requested();
    }
    @LuaConvertible
    public boolean hasAbsoluteControl() {
        return revoker != null && owner.requested();
    }

    protected void checkControl() {
        if(!hasControl()) {
            throw new LuaError("Cannot control as another script requested Mission Critical");
        }
    }
}
