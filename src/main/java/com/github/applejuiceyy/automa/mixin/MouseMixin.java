package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method="updateMouse",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            ),
            cancellable = true
    )
    void modifyClicks(CallbackInfo ci) {
        if(AutomaClient.lookControls.requested()) {
            ci.cancel();
        }
    }

    @Inject(method="onMouseScroll",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"
            ),
            cancellable = true
    )
    void onMouseScroll(CallbackInfo ci) {
        if(AutomaClient.inventoryControls.requested()) {
            ci.cancel();
        }
    }
}
