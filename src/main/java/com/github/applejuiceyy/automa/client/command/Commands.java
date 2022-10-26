package com.github.applejuiceyy.automa.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Commands {
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        LiteralArgumentBuilder<FabricClientCommandSource> arg = LiteralArgumentBuilder.literal("automation");

        arg.then(LoadScriptCommand.create());
        arg.then(EvaluateCommand.create());
        arg.then(UnloadCommand.create());

        dispatcher.register(arg);
    }
}
