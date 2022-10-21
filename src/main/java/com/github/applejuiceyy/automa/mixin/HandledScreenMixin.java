package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method="onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at=@At("HEAD"), cancellable = true)
    void doUse(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (AutomaClient.inventoryControls.requested()) {
            ci.cancel();
        }
    }
}
