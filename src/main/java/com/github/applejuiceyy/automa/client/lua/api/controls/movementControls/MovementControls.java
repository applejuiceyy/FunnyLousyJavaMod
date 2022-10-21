package com.github.applejuiceyy.automa.client.lua.api.controls.movementControls;

import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCritical;

public class MovementControls extends MissionCritical {
    public boolean forwards = false;
    public boolean backwards = false;
    public boolean left = false;
    public boolean right = false;
    public boolean jumping = false;
    public boolean sneaking = false;
    public boolean sprinting = false;
    public boolean autoJump = false;

    @Override
    public String getControllingAspect() {
        return "movement";
    }
}
