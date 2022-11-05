package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.mixin.acessors.KeyBindAccessor;
import net.minecraft.client.MinecraftClient;
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
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            c.performEventWith(c.render, c.boundary.J2L(((MinecraftClient)(Object)this).getTickDelta()));
        }
    }

    @Inject(method="doAttack", at=@At("HEAD"), cancellable = true)
    void doAttack(CallbackInfoReturnable<Boolean> cir) {
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if(c.performEvent(c.attackItem)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method="doItemUse", at=@At("HEAD"), cancellable = true)
    void doUse(CallbackInfo ci) {
        LuaExecution c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            if (c.performEvent(c.useItem)) {
                ci.cancel();
            }
        }
    }

    boolean wasUsingItem;
    boolean wasAttacking;

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

        wasUsingItem = tthis.options.useKey.isPressed();
        wasAttacking = tthis.options.attackKey.isPressed();

        if (AutomaClient.inventoryControls.requested()) {
            use.setTimesPressed(0);
            attack.setTimesPressed(0);
            pick.setTimesPressed(0);

            tthis.options.useKey.setPressed(false);
            tthis.options.attackKey.setPressed(false);
            tthis.options.pickItemKey.setPressed(false);
        }

        attack.setTimesPressed(attack.getTimesPressed() + AutomaClient.inventoryControls.timesAttackingItem);

        tthis.options.attackKey.setPressed(tthis.options.attackKey.isPressed() || AutomaClient.inventoryControls.attackingItem);

        use.setTimesPressed(use.getTimesPressed() + AutomaClient.inventoryControls.timesUsingItem);

        tthis.options.useKey.setPressed(tthis.options.useKey.isPressed() || AutomaClient.inventoryControls.usingItem);

        AutomaClient.inventoryControls.timesAttackingItem = 0;
        AutomaClient.inventoryControls.timesUsingItem = 0;
    }

    @Inject(
            method="handleInputEvents",
            at=@At(
                    value = "TAIL",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
                    ordinal = 0
            )
    )
    void e(CallbackInfo ci) {
        MinecraftClient tthis = ((MinecraftClient)(Object) this);

        if (tthis.options.attackKey.isPressed() || !wasAttacking) {
            // the script can make an unpressed button pressed, but can't unpress the button
            // which means that
            // this likely happened from KeyBinds.unpressAll(); from the setScreen method
            // we should honor it

            tthis.options.attackKey.setPressed(wasAttacking);
        }
        if (tthis.options.useKey.isPressed() || !wasUsingItem) {
            tthis.options.useKey.setPressed(wasUsingItem);
        }

        wasAttacking = false;
        wasUsingItem = false;
    }

    @Unique
    KeyBindAccessor cast(KeyBinding key) {
        return (KeyBindAccessor) key;
    }
}
