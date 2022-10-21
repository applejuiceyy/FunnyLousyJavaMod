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

    public void forwards() { checkControl(); owner.forwards = true; }
    public void backwards() { checkControl(); owner.backwards = true; }
    public void left() { checkControl(); owner.left = true; }
    public void right() { checkControl(); owner.right = true; }
    public void jumping() { checkControl(); owner.jumping = true; }
    public void sneaking() { checkControl(); owner.sneaking = true; }

    public void stopForwards() { checkControl(); owner.forwards = false; }
    public void stopBackwards() { checkControl(); owner.backwards = false; }
    public void stopLeft() { checkControl(); owner.left = false; }
    public void stopRight() { checkControl(); owner.right = false; }
    public void stopJumping() { checkControl(); owner.jumping = false; }
    public void stopSneaking() { checkControl(); owner.sneaking = false; }

    public void sprinting() {checkControl(); owner.sprinting = true; }
    public void stopSprinting() {checkControl(); owner.sprinting = false; }

    public void autoJump() {checkControl(); owner.autoJump = true; }
    public void stopAutoJump() {checkControl(); owner.autoJump = false; }

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
