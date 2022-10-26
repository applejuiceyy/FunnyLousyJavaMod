package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgingScreenHandler.class)
public interface ForgingScreenHandlerAccessor {
    @Accessor("input")
    Inventory getInput();
    @Accessor("output")
    CraftingResultInventory getOutput();
}
