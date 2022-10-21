package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
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
            ),
            cancellable = true
    )
    void d(CallbackInfo ci) {
        // code in here is very iffy, I think the only solution is just to replace it
        MinecraftClient t = (MinecraftClient)(Object) this;
        MinecraftClientMixinAccessor tc = (MinecraftClientMixinAccessor) t;

        LuaExecutionFacade c;
        if ((c = LuaExecutionContainer.getExecutor()) != null) {
            boolean attacking = false;
            assert t.player != null;
            assert t.interactionManager != null;

            if (t.player.isUsingItem()) {
                if ((!t.options.useKey.isPressed() || AutomaClient.inventoryControls.requested()) && !AutomaClient.inventoryControls.usingItem) {
                    t.interactionManager.stopUsingItem(t.player);
                }
                while (t.options.attackKey.wasPressed()) {
                }
                while (t.options.useKey.wasPressed()) {
                }
                while (t.options.pickItemKey.wasPressed()) {
                }
            } else {
                while (t.options.attackKey.wasPressed()) {
                    if (!AutomaClient.inventoryControls.requested()) {
                        attacking |= tc.invokeDoAttack();
                    }
                }
                if (AutomaClient.inventoryControls.attackingItem) {
                    attacking |= tc.invokeDoAttack();
                }
                while (t.options.useKey.wasPressed()) {
                    if (!AutomaClient.inventoryControls.requested()) {
                        tc.invokeDoItemUse();
                    }
                }
                if (AutomaClient.inventoryControls.usingItem) {
                    tc.invokeDoItemUse();
                }
                while (t.options.pickItemKey.wasPressed()) {
                    if (!AutomaClient.inventoryControls.requested()) {
                        tc.invokeDoItemPick();
                    }
                }
            }
            if (t.options.useKey.isPressed() && tc.getItemUseCooldown() == 0 && !t.player.isUsingItem()) {
                tc.invokeDoItemUse();
            }


            tc.invokeHandleBlockBreaking(
                    t.currentScreen == null &&
                            !attacking &&
                            ((t.options.attackKey.isPressed() && !AutomaClient.inventoryControls.requested()) || AutomaClient.inventoryControls.attackingItem) &&
                            (t.mouse.isCursorLocked() || AutomaClient.inventoryControls.attackingItem)
            );
            ci.cancel();
        }
    }
}
