package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.lua.LuaEvent;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import net.minecraft.client.MinecraftClient;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method="doAttack", at=@At("HEAD"), cancellable = true)
    void doAttack(CallbackInfoReturnable<Boolean> cir) {
        LuaExecutionFacade c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if(c.performEvent(c.attackItem)) {
                cir.setReturnValue(false);
                cir.cancel();
            };
        }
    }

    @Inject(method="doItemUse", at=@At("HEAD"), cancellable = true)
    void doUse(CallbackInfo ci) {
        LuaExecutionFacade c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if (c.performEvent(c.useItem)) {
                ci.cancel();
            }
        }
    }
}
