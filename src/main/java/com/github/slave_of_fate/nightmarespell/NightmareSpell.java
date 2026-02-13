package com.github.slave_of_fate.nightmarespell;

import com.github.slave_of_fate.aspect.registries.AspectRank;
import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.capabilities.SoulRank;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;

public class NightmareSpell {
    private static final Map<UUID, Queue<String>> MESSAGE_QUEUES = new HashMap<>();
    private static final Map<UUID, Integer> TICK_COUNTERS = new HashMap<>();
    private static final int DELAY_BETWEEN_MESSAGES = 60;

    public static void queueMessage(ServerPlayer player, String message) {
        MESSAGE_QUEUES.computeIfAbsent(player.getUUID(), k -> new LinkedList<>()).add(message);
    }

    public static void tickQueue(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Queue<String> queue = MESSAGE_QUEUES.get(uuid);

        if (queue == null || queue.isEmpty()) return;

        int counter = TICK_COUNTERS.getOrDefault(uuid, 0);

        if (counter <= 0) {
            String nextMsg = queue.poll();
            speak(player, nextMsg);
            TICK_COUNTERS.put(uuid, DELAY_BETWEEN_MESSAGES);
        } else {
            TICK_COUNTERS.put(uuid, counter - 1);
        }
    }

    private static void playSpellSounds(ServerPlayer player) {
        player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 0.4f, 0.5f);
        player.playNotifySound(SoundEvents.WARDEN_SNIFF, SoundSource.PLAYERS, 0.5f, 1.2f);
        player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.3f, 0.1f);
    }

    private static void speak(ServerPlayer player, String message) {
        player.sendSystemMessage(
                Component.literal("[ ")
                        .append(Component.literal(message).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC))
                        .append(Component.literal(" ]"))
        );
        playSpellSounds(player);
    }

    public record Results(String assessment, AspectRank rank, ResourceLocation aspectId, ResourceLocation flawId) {
    }

    public static Results evaluate(int score) {
        String assessment;
        AspectRank rank;

        if (score >= 120) {
            assessment = "Impossible";
            rank = AspectRank.DIVINE;
        } else if (score >= 90) {
            assessment = "Glorious";
            rank = AspectRank.SUPREME;
        } else if (score >= 70) {
            assessment = "Heroic";
            rank = AspectRank.TRANSCENDENT;
        } else {
            assessment = "Decent";
            rank = AspectRank.AWAKENED;
        }

        ResourceLocation aspectId = pickRandomAspect(rank);
        ResourceLocation flawId = pickRandomFlaw();

        return new Results(assessment, rank, aspectId, flawId);
    }

    public static void promotePlayer(ServerPlayer player, Results results) {
        PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

        // Update Rank to Awakened (since they finished a nightmare)
        soul.setRank(SoulRank.AWAKENED);
        soul.setAspectId(results.aspectId());
        soul.setFlawId(results.flawId());

        if (results.rank() == AspectRank.DIVINE || results.assessment().equals("Glorious") || results.assessment().equals("Impossible")) {
            String generatedName = generateTrueName();
            soul.setTrueName(generatedName);
            queueMessage(player, "You have been granted a True Name: [" + generatedName + "].");
        }

        player.setData(SlaveOfFateAttachments.SOUL_DATA, soul);
        player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private static String generateTrueName() {
        String[] prefix = {"Lost", "Shadow", "Sunless", "Stared", "Pale"};
        String[] suffix = {"Light", "King", "Traveler", "Slave", "Prince"};
        java.util.Random rand = new java.util.Random();
        return prefix[rand.nextInt(prefix.length)] + " " + suffix[rand.nextInt(suffix.length)];
    }

    public static boolean commandByTrueName(ServerPlayer caster, ServerPlayer target, String spokenName) {
        PlayerSoul targetSoul = target.getData(SlaveOfFateAttachments.SOUL_DATA);
        if (targetSoul.getTrueName().equalsIgnoreCase(spokenName)) {
            speak(target, "Your True Name has been spoken... your soul is bound.");
            return true;
        }
        return false;
    }

    private static ResourceLocation pickRandomAspect(AspectRank rank) {
        return ResourceLocation.fromNamespaceAndPath("slave_of_fate", "test_aspect");
    }

    private static ResourceLocation pickRandomFlaw() {
        return ResourceLocation.fromNamespaceAndPath("slave_of_fate", "fair_weather");
    }

    public static void startFirstNightmare(ServerPlayer player) {
        queueMessage(player, "Prepare for your nightmare, Sleeper " + player.getName().getString());
    }

    public static void onNightmareDeath(ServerPlayer player) {
        speak(player, "You have failed your nightmare.");
        speak(player, "The Nightmare has consumed you.");
        player.kill();
    }

    public static void completeFirstNightmare(ServerPlayer player, int score) {
        queueMessage(player, "Wake up, " + player.getName().getString() + "! Your nightmare is over.");
        queueMessage(player, "Prepare for appraisal...");

        Results results = evaluate(score);

        String aspectDisplay = results.aspectId().getPath().replace("_", " ");
        String flawDisplay = results.flawId().getPath().replace("_", " ");

        queueMessage(player, "Final Appraisal: " + results.assessment());
        queueMessage(player, "Dreamer " + player.getName().getString() + ", receive your boon!");
        queueMessage(player, "Aspect: " + results.rank().name() + " [" + aspectDisplay + "].");
        queueMessage(player, "Flaw: [" + flawDisplay + "].");

        promotePlayer(player, results);
    }
}