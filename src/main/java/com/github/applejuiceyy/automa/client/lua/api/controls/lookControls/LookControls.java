package com.github.applejuiceyy.automa.client.lua.api.controls.lookControls;

import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCritical;
import net.minecraft.util.math.MathHelper;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getPlayer;

public class LookControls extends MissionCritical {
    public void lookAt(float pitch, float yaw) {
        setPitch(pitch);
        setYaw(yaw);
    }

    public void setPitch(double pitch) {
        getPlayer().setPitch((float) MathHelper.clamp(pitch, -90.0f, 90.0f));
    }

    public void setYaw(double yaw) {
        getPlayer().setYaw((float) yaw);
    }

    @Override
    public String getControllingAspect() {
        return "look";
    }
}
