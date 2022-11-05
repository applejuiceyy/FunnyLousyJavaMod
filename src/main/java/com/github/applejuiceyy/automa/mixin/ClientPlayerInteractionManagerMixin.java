package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method="updateBlockBreakingProgress", at=@At("HEAD"), cancellable = true)
    void doUse(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if (c.performEvent(c.blockBreaking)) {
                ((ClientPlayerInteractionManager)(Object) this).cancelBlockBreaking();
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method="cancelBlockBreaking", at=@At("HEAD"))
    void doUse(CallbackInfo ci) {
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            c.performEvent(c.blockBreakingCancel);
        }
    }

    @Inject(method="breakBlock", at=@At("HEAD"))
    void doUse(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            c.performEvent(c.brokeBlock);
        }
    }
}
