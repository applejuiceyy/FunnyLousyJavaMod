package com.github.applejuiceyy.automa.client.automatedscreenhandler;

import com.github.applejuiceyy.automa.client.automatedscreenhandler.inventory.DynamicSlotReference;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.github.applejuiceyy.automa.mixin.screenhandler.*;
import net.minecraft.screen.*;

import java.util.HashMap;

public class AutomatedScreenHandlers {
    public static HashMap<Class<? extends ScreenHandler>, Factory> map = new HashMap<>(){{
        this.put(PlayerScreenHandler.class, AutomatedPlayerScreen::new);

        this.put(GenericContainerScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, ((GenericContainerScreenHandler) handler).getRows(), 9, ((GenericContainerScreenHandler) handler).getInventory())
        );
        this.put(Generic3x3ContainerScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, 3, 3, ((Generic3x3ContainerScreenHandlerAccessor) handler).getInventory())
        );
        this.put(AnvilScreenHandler.class, AutomatedAnvil::new);
        this.put(BeaconScreenHandler.class, AutomatedBeacon::new);
        this.put(BlastFurnaceScreenHandler.class, AutomatedFurnace::new);
        this.put(BrewingStandScreenHandler.class, AutomatedBrewingStand::new);
        this.put(CraftingScreenHandler.class, AutomatedCrafting::new);
        this.put(EnchantmentScreenHandler.class, AutomatedEnchantmentTable::new);
        this.put(FurnaceScreenHandler.class, AutomatedFurnace::new);
        this.put(GrindstoneScreenHandler.class, (executor, handler) -> new Generic2ItemMerger<>(executor, handler,
                (c) -> new DynamicSlotReference(c, ((GrindstoneScreenHandlerAccessor) handler).getInput(), 0),
                (c) -> new DynamicSlotReference(c, ((GrindstoneScreenHandlerAccessor) handler).getInput(), 1),
                (c) -> new DynamicSlotReference(c, ((GrindstoneScreenHandlerAccessor) handler).getResult(), 0)
        ));
        this.put(HopperScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, 1, 5, ((HopperScreenHandlerAccessor) handler).getInventory())
        );
        this.put(LecternScreenHandler.class, AutomatedLectern::new);

        // missing LoomScreenHandler
        // missing MerchantScreenHandler

        this.put(ShulkerBoxScreenHandler.class, (executor, handler) ->
                new AutomatedGeneric(executor, handler, 3, 9, ((ShulkerBoxScreenHandlerAccessor) handler).getInventory())
        );

        this.put(SmithingScreenHandler.class, (executor, handler) -> new Generic2ItemMerger<>(executor, handler,
                (c) -> new DynamicSlotReference(c, ((ForgingScreenHandlerAccessor) handler).getInput(), 0),
                (c) -> new DynamicSlotReference(c, ((ForgingScreenHandlerAccessor) handler).getInput(), 1),
                (c) -> new DynamicSlotReference(c, ((ForgingScreenHandlerAccessor) handler).getOutput(), 0)
        ));

        this.put(SmokerScreenHandler.class, AutomatedFurnace::new);
        this.put(CartographyTableScreenHandler.class, (executor, handler) -> new Generic2ItemMerger<>(executor, handler,
                (c) -> new DynamicSlotReference(c, ((CartographyTableScreenHandler) handler).inventory, 0),
                (c) -> new DynamicSlotReference(c, ((CartographyTableScreenHandler) handler).inventory, 1),
                (c) -> new DynamicSlotReference(c, ((CartographyTableScreenHandlerAccessor) handler).getResultInventory(), 0)
        ));

        // missing stonecutter
    }};

    @FunctionalInterface
    public interface Factory {
        AutomatedScreenHandler<?> create(LuaExecution executor, ScreenHandler handler);
    }
}
