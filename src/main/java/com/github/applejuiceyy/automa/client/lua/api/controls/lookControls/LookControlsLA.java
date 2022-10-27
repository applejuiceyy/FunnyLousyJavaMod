package com.github.applejuiceyy.automa.client.lua.api.controls.lookControls;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCriticalLuaInterface;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Vec3d;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

@LuaConvertible
public class LookControlsLA extends MissionCriticalLuaInterface<LookControls> {
    public LookControlsLA(LuaExecutionFacade f, LookControls obj) { super(f, obj); }

    @LuaConvertible
    public void lookAt(float pitch, float yaw) {
        checkControl();
        owner.lookAt(pitch, yaw);
    }

    @LuaConvertible
    public void lookAt(float x, float y, float z) {
        checkControl();
        getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(x, y, z));
    }

    @LuaConvertible
    public float getPitch() {
        return getPlayer().getPitch();
    }
    @LuaConvertible
    public float getYaw() {
        return getPlayer().getYaw();
    }
    @LuaConvertible
    public void setPitch(double pitch) {
        checkControl();
        owner.setPitch(pitch);
    }
    @LuaConvertible
    public void setYaw(double yaw) {
        checkControl();
        owner.setYaw(yaw);
    }
}
