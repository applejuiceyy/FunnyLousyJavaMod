package com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls;

import com.github.applejuiceyy.automa.client.lua.api.controls.MissionCritical;

public class InventoryControls extends MissionCritical {
    @Override
    public String getControllingAspect() {
        return "inventory";
    }

    public boolean usingItem;
    public boolean attackingItem;
    public int timesUsingItem;
    public int timesAttackingItem;
}
