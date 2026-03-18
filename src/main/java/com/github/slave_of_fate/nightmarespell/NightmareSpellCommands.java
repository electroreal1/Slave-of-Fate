package com.github.slave_of_fate.nightmarespell;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.flaw.return_by_death.ReturnByDeathEngine;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NightmareSpellCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nightmare_spell")
                .requires(source -> source.hasPermission(4)) // Op level 4
                .then(Commands.literal("setFlaw")
                        .then(Commands.argument("flaw_id", ResourceLocationArgument.id())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("slave_of_fate:return_by_death");
                                    builder.suggest("slave_of_fate:greed");
                                    return builder.buildFuture();
                                })
                                .executes(
                                        ctx -> setFlaw(ctx,
                                                ResourceLocationArgument.getId(ctx, "flaw_id")
                                        )
                                )
                        )
                )

                .then(Commands.literal("setAspect")
                        .then(Commands.argument("aspect_id", ResourceLocationArgument.id())
                                .executes(ctx -> setAspect(ctx,
                                                ResourceLocationArgument.getId(ctx, "aspect_id")
                                        )
                                )
                        )
                )


                .then(Commands.literal("return_by_death")
                        .then(Commands.literal("force_save")
                                .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayer();
                                            if (player != null) {
                                                ReturnByDeathEngine.setCheckpoint(player);
                                                ctx.getSource().sendSuccess(() -> Component.literal("Time Anchor Set.")
                                                        .withStyle(ChatFormatting.DARK_PURPLE), true);
                                            }
                                            return 1;
                                        }
                                )
                        )
                )

        );
    }

    private static int setFlaw(CommandContext<CommandSourceStack> context, ResourceLocation id) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);
            soul.setFlawId(id);

            context.getSource().sendSuccess(() -> Component.literal("Set Flaw to: " + id.toString()), true);
        }
        return 1;
    }

    private static int setAspect(CommandContext<CommandSourceStack> context, ResourceLocation id) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);
            soul.setAspectId(id);

            context.getSource().sendSuccess(() -> Component.literal("Set Aspect to: " + id.toString()), true);
        }
        return 1;
    }
}