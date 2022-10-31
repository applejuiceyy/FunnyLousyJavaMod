package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandScreenHandler.class)
public interface BrewingStandScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
