package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Generic3x3ContainerScreenHandler.class)
public interface Generic3x3ContainerScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
