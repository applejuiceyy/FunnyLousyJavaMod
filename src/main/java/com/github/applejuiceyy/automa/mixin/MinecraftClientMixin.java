package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import com.github.applejuiceyy.automa.mixin.acessors.KeyBindAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method="render", at=@At("HEAD"))
    void doAttack(boolean tick, CallbackInfo ci) {
        LuaExecutionFacade c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            c.performEventWith(c.render, c.boundary.J2L(((MinecraftClient)(Object)this).getTickDelta()));
        }
    }

    @Inject(method="doAttack", at=@At("HEAD"), cancellable = true)
    void doAttack(CallbackInfoReturnable<Boolean> cir) {
        LuaExecutionFacade c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if(c.performEvent(c.attackItem)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
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

    @Inject(
            method="handleInputEvents",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
                    ordinal = 0
            )
    )
    void d(CallbackInfo ci) {
        MinecraftClient tthis = ((MinecraftClient)(Object) this);

        KeyBindAccessor use = cast(tthis.options.useKey);
        KeyBindAccessor attack = cast(tthis.options.attackKey);
        KeyBindAccessor pick = cast(tthis.options.pickItemKey);

        if (AutomaClient.inventoryControls.requested()) {
            use.setTimesPressed(0);
            attack.setTimesPressed(0);
            pick.setTimesPressed(0);

            tthis.options.useKey.setPressed(false);
            tthis.options.attackKey.setPressed(false);
            tthis.options.pickItemKey.setPressed(false);
        }

        if (AutomaClient.inventoryControls.attackingItem) {
            attack.setTimesPressed(attack.getTimesPressed() + 1);
            tthis.options.attackKey.setPressed(true);
        }

        if (AutomaClient.inventoryControls.usingItem) {
            use.setTimesPressed(attack.getTimesPressed() + 1);
            tthis.options.useKey.setPressed(true);
        }
    }

    @Unique
    KeyBindAccessor cast(KeyBinding key) {
        return (KeyBindAccessor) key;
    }
}
