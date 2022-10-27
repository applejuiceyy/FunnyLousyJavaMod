package com.github.applejuiceyy.automa.client.lua.api.controls.movementControls;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCriticalLuaInterface;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

@LuaConvertible
public class MovementControlsLA extends MissionCriticalLuaInterface<MovementControls> {
    public MovementControlsLA(LuaExecutionFacade f, MovementControls obj) {
        super(f, obj);
    }
    @LuaConvertible
    public void forwards() { checkControl(); owner.forwards = true; }
    @LuaConvertible
    public void backwards() { checkControl(); owner.backwards = true; }
    @LuaConvertible
    public void left() { checkControl(); owner.left = true; }
    @LuaConvertible
    public void right() { checkControl(); owner.right = true; }
    @LuaConvertible
    public void jumping() { checkControl(); owner.jumping = true; }
    @LuaConvertible
    public void sneaking() { checkControl(); owner.sneaking = true; }

    @LuaConvertible
    public void stopForwards() { checkControl(); owner.forwards = false; }
    @LuaConvertible
    public void stopBackwards() { checkControl(); owner.backwards = false; }
    @LuaConvertible
    public void stopLeft() { checkControl(); owner.left = false; }
    @LuaConvertible
    public void stopRight() { checkControl(); owner.right = false; }
    @LuaConvertible
    public void stopJumping() { checkControl(); owner.jumping = false; }
    @LuaConvertible
    public void stopSneaking() { checkControl(); owner.sneaking = false; }

    @LuaConvertible
    public void sprinting() {checkControl(); owner.sprinting = true; }
    @LuaConvertible
    public void stopSprinting() {checkControl(); owner.sprinting = false; }

    @LuaConvertible
    public void autoJump() {checkControl(); owner.autoJump = true; }
    @LuaConvertible
    public void stopAutoJump() {checkControl(); owner.autoJump = false; }

    @LuaConvertible
    public void jump() {
        checkControl();
        // dirty hack
        jumping();
        callIn(this::stopJumping, 4);
    }

    void callIn(Runnable what, int ticks) {
        execution.tick.subscribeOnce(new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                if (hasControl()) {
                    if (ticks > 0) {
                        callIn(what, ticks - 1);
                    }
                    else {
                        what.run();
                    }
                }
                return LuaValue.NIL;
            }
        });
    }
}
