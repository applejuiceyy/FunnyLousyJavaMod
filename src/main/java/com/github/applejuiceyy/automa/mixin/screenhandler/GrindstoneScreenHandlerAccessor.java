package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GrindstoneScreenHandler.class)
public interface GrindstoneScreenHandlerAccessor {
    @Accessor("result")
    Inventory getResult();
    @Accessor("input")
    Inventory getInput();
}
