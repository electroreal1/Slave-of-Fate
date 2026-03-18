package com.github.slave_of_fate.nightmarespell;

import com.github.slave_of_fate.aspect.registries.AspectRank;
import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.capabilities.SoulRank;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

import static com.github.slave_of_fate.aspect.registries.AspectRank.TRANSCENDENT;
import static net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT;

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
            rank = TRANSCENDENT;
        } else {
            assessment = "Decent";
            rank = AspectRank.AWAKENED;
        }

        ResourceLocation aspectId = pickRandomAspect(rank);
        ResourceLocation flawId = pickRandomFlaw();

        return new Results(assessment, rank, aspectId, flawId);
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().getName().getString().equals("Dev")) {

            if (event.getEntity() instanceof ServerPlayer player) {

                PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

                soul.setFlawId(ResourceLocation.parse("slave_of_fate:return_by_death"));

                System.out.println("Dev login detected: Assigned Return by Death flaw.");
            }
        }
    }

    public static void promotePlayer(ServerPlayer player, Results results) {
        PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

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
        String[] prefix = {"Lost", "Shadow", "Sunless", "Stared", "Pale", "Blind", " "};
        String[] suffix = {"Light", "King", "Traveler", "Slave", "Prince", "Lord", "Trash", "Idiot", "From", " "};
        String[] threefix = {"Light", "King", "Traveler", "Slave", "Prince", "Lord", "Trash", "Idiot", " "};
        java.util.Random rand = new java.util.Random();
        return prefix[rand.nextInt(prefix.length)] + " " + suffix[rand.nextInt(suffix.length)] + " " + threefix[rand.nextInt(threefix.length)];
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
        queueMessage(player, "Prepare for your nightmare, Dreamer " + player.getName().getString());
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

    public static void sendRuneStats(ServerPlayer player) {
        PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);
        ResourceLocation rawFlaw = soul.getFlawId();
        ResourceLocation rawAspect = soul.getAspectId();
        String formattedFlaw = formatFlawName(rawFlaw);
        String formattedAspect = formantAspectName(rawAspect);
        SoulRank soulRank = soul.getRank();

        String topBorder    = "᚛─────────────── ◈ ───────────────᚜";
        String bottomBorder = "᚛─────────────────────────────────᚜";

        ChatFormatting primary = ChatFormatting.GOLD;
        ChatFormatting secondary = ChatFormatting.DARK_PURPLE;
        ChatFormatting valueColor = ChatFormatting.AQUA;

        MutableComponent message = Component.literal("\n" + topBorder + "\n").withStyle(secondary);

        message.append(Component.literal("        STATUS: ").withStyle(primary).withStyle(ChatFormatting.BOLD));
        message.append(player.getName().copy().withStyle(ChatFormatting.WHITE)).append("\n\n");

        addStatLine(message, " ❤ Health", String.format("%.1f/%.1f", player.getHealth(), player.getMaxHealth()), ChatFormatting.RED, valueColor);
        addStatLine(message, " ❂ Hunger", String.valueOf(player.getFoodData().getFoodLevel()), ChatFormatting.GOLD, valueColor);
        addStatLine(message, " ✨ Soul Essence", String.valueOf(soul.getEssence()), ChatFormatting.GREEN, valueColor);
        addStatLine(message, " " + getRankIcon(soulRank) + " Soul Rank:", String.valueOf(soul.getRank()), ChatFormatting.GREEN, valueColor);

        message.append(Component.literal("    [!] Flaw: ").withStyle(ChatFormatting.GOLD))
                .append(Component.literal(formattedFlaw).withStyle(style -> style
                        .withColor(ChatFormatting.DARK_RED)
                        .withItalic(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("The burden bound to your soul.")))
                )).append("\n");

        message.append(Component.literal("    [*] Aspect: ").withStyle(ChatFormatting.GOLD))
                .append(Component.literal(formattedAspect).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC))
                .append("\n");
        message.append(Component.literal("\n" + bottomBorder).withStyle(secondary));

        player.sendSystemMessage(message);
    }

    private static void addStatLine(MutableComponent msg, String label, String value, ChatFormatting labelCol, ChatFormatting valCol) {
        msg.append(Component.literal("  " + label + ": ").withStyle(labelCol))
                .append(Component.literal(value).withStyle(valCol))
                .append("\n");
    }

    private static String getRankIcon(SoulRank rank) {
        return switch (rank) {
            case DORMANT -> "🕯️";
            case AWAKENED -> "✨";
            case ASCENDED -> "✵";
            case TRANSCENDED -> "𖤓";
            default -> "⚪";
        };
    }

    public static String formatFlawName(ResourceLocation flawId) {
        if (flawId == null) return "None";

        String path = flawId.getPath();

        String spaced = path.replace("_", " ");


        StringBuilder result = new StringBuilder();
        for (String word : spaced.split(" ")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String formantAspectName(ResourceLocation aspectId) {
        if (aspectId == null) return "None";

        String path = aspectId.getPath();

        String spaced = path.replace("_", " ");


        StringBuilder result = new StringBuilder();
        for (String word : spaced.split(" ")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}