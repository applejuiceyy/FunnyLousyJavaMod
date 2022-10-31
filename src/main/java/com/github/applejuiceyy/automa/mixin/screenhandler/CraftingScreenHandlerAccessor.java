package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingScreenHandlerAccessor {
    @Accessor("input")
    CraftingInventory getInput();
    @Accessor("result")
    CraftingResultInventory getResult();
}
