package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.CartographyTableScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CartographyTableScreenHandler.class)
public interface CartographyTableScreenHandlerAccessor {
    @Accessor("resultInventory")
    CraftingResultInventory getResultInventory();
}
