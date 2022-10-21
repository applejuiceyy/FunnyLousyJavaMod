package com.github.applejuiceyy.automa.client.lua.api.controls.lookControls;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCriticalLuaInterface;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class LookControlsLA extends MissionCriticalLuaInterface<LookControls> {
    public LookControlsLA(LuaExecutionFacade f, LookControls obj) { super(f, obj); }

    public void lookAt(float pitch, float yaw) {
        checkControl();
        owner.lookAt(pitch, yaw);
    }

    public float getPitch() {
        return getPlayer().getPitch();
    }

    public float getYaw() {
        return getPlayer().getYaw();
    }

    public void setPitch(double pitch) {
        checkControl();
        owner.setPitch(pitch);
    }

    public void setYaw(double yaw) {
        checkControl();
        owner.setYaw(yaw);
    }
}
