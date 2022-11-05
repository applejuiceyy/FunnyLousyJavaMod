package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerScreenHandlerAccessor {
    @Accessor("craftingInput")
    CraftingInventory getCraftingInput();
    @Accessor("craftingResult")
    CraftingResultInventory getCraftingResult();
}
