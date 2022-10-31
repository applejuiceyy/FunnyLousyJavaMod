package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceScreenHandler.class)
public interface AbstractFurnaceScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
