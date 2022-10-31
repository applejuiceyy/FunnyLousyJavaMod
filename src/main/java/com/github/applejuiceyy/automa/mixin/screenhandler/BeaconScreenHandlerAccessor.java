package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconScreenHandler.class)
public interface BeaconScreenHandlerAccessor {
    @Accessor("payment")
    Inventory getPayment();
}
