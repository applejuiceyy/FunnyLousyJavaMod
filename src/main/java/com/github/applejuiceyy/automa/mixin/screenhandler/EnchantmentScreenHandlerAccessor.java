package com.github.applejuiceyy.automa.mixin.screenhandler;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentScreenHandler.class)
public interface EnchantmentScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
