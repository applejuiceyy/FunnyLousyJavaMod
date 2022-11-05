package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method="canPlace", at=@At("HEAD"), cancellable = true)
    void doUse(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(!context.getWorld().isClient()) {
            return;
        }

        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if (c.performEvent(c.blockPlacing)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
