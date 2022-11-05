package com.github.applejuiceyy.automa.client.command;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.File;

import static com.github.applejuiceyy.automa.client.AutomaClient.getScriptsPath;

public class LoadScriptCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> create() {
        ArgumentBuilder<FabricClientCommandSource, ?> literal = LiteralArgumentBuilder.literal("load");
        RequiredArgumentBuilder<FabricClientCommandSource, ?> sub = RequiredArgumentBuilder.argument("name", StringArgumentType.string());
        sub.suggests((a, b) -> {
            File folder = new File(String.valueOf(getScriptsPath()));
            File[] files;
            if ((files = folder.listFiles()) != null) {
                for (File file : files) {
                    b.suggest(file.getName());
                }
            }

            return b.buildFuture();
        });
        sub.executes(context -> {
            LuaExecutionContainer.setExecutor(
                    new LuaExecution(getScriptsPath().resolve(context.getArgument("name", String.class)))
            );
            return 1;
        });
        return literal.then(sub);
    }
}
