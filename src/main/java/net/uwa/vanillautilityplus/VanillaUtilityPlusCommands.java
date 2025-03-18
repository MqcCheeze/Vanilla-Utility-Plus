package net.uwa.vanillautilityplus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class VanillaUtilityPlusCommands {

    private static HashSet<String> playerTrackingList = new HashSet<>();

    private static final MinecraftClient client = MinecraftClient.getInstance();

    // Do things if specific messages are sent by the client. E.g. dot commands
    public static boolean parseChatMessage(String message) {

        // Quick check to see if the message was sent by a player and not for example a server console
        if (client.player == null) {
            return true;
        }

        // Check the message
        switch(message.toLowerCase()) {

            //=======================
            // Coordinate converting
            //=======================

            // Send the converted overworld coordinates
            case ".overworld":

                if (client.player.getWorld().getRegistryKey() == World.OVERWORLD) {
                    client.player.sendMessage(Text.literal("You are already in the overworld"), false);
                    return false;
                }

                int playerNX = (int)(client.player.getX());
                int playerNY = (int)(client.player.getY());
                int playerNZ = (int)(client.player.getZ());
                client.player.sendMessage(Text.literal("X = %d, Y = %d, Z = %d".formatted(
                        playerNX * 8,
                        playerNY,
                        playerNZ * 8
                )), false);
                return false;

            // Send the converted nether coordinates
            case ".nether":

                if (client.player.getWorld().getRegistryKey() == World.NETHER) {
                    client.player.sendMessage(Text.literal("You are already in the nether"), false);
                    return false;
                }

                int playerOX = (int)(client.player.getX());
                int playerOY = (int)(client.player.getY());
                int playerOZ = (int)(client.player.getZ());
                client.player.sendMessage(Text.literal("X = %d, Y = %d, Z = %d".formatted(
                        playerOX / 8,
                        playerOY,
                        playerOZ / 8
                )), false);
                return false;

            // Just for fun
            case ".end":
                client.player.sendMessage(Text.literal("What even is the point of this one"), false);
                return false;


            //========================
            // Other utility commands
            //========================

            // Get the local time just so you can track how long you've been playing for...
            case ".time":
                Instant now = Instant.now();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                client.player.sendMessage(Text.literal("The current time is " + localDateTime.format(formatter)), false);
                return false;


            // List all the commands available
            case ".help":
                client.player.sendMessage(Text.literal("Available commands:"), false);
                client.player.sendMessage(Text.literal("overworld - use in the nether to get the overworld equivalent coordinates"), false);
                client.player.sendMessage(Text.literal("nether - use in the overworld to get the nether equivalent coordinates"), false);
                client.player.sendMessage(Text.literal("end - try it out :)"), false);
                client.player.sendMessage(Text.literal("time - get the current local time"), false);
                client.player.sendMessage(Text.literal("nyan <message> - nyan'ifies the message"), false);
                client.player.sendMessage(Text.literal("track <add | remove | list> <playerName> - adds the player to the tracking menu"), false);

                return false;


            //=========================
            // TESTING TESTING TESTING
            //=========================

            case ".testing":

                return false;


            // Other "dot" commands that might have unpredictable arguments after the initial command
            // Simply send the message if it doesn't match any cases
            default:

                //=====
                // Fun
                //=====

                // nyan'ify the message
                if (message.startsWith(".nyan")) {
                    String actualMessage = message.substring(6).toLowerCase();
                    actualMessage = actualMessage.replaceAll("n([aeiou])", "ny$1");

                    client.player.networkHandler.sendChatMessage(actualMessage);
                    return false;
                } else if (message.startsWith(".track")) {
                    String subCmd = message.substring(7);
                    if (subCmd.startsWith("add")) {
                        String player = subCmd.substring(4);
                        playerTrackingList.add(player);
                    } else if (subCmd.startsWith("remove")) {
                        String player = subCmd.substring(7);
                        playerTrackingList.remove(player);
                    } else if (subCmd.equals("list")) {
                        client.player.sendMessage(Text.literal("Currently tracking: " + playerTrackingList.toString()), false);
                    } else {
                        client.player.sendMessage(Text.literal("Command format: .track <add | remove | list> <playerName>"), false);
                    }
                    return false;
                }

                return true;
        }
    }

    // Detector for joining and leaving players
    // Can be used to track the join/leave activity of a player
    public static void playerJoinLeaveDetector(ServerPlayNetworkHandler handler, boolean isJoin) {

        ServerPlayerEntity player = handler.getPlayer();
        String playerName = player.getName().getString();
        Vec3d playerPosition = player.getPos();
        if (playerTrackingList.contains(playerName)) {
            client.player.sendMessage(Text.literal("Notice: Player %s has %s the server".formatted(
                    playerName,
                    isJoin ? "joined" : "left"
            )), true);
            client.player.sendMessage(Text.literal("%s %s at position X=%s, Y=%s, Z=%s".formatted(
                    playerName,
                    isJoin ? "joined" : "left",
                    String.format("%.2f", playerPosition.x),
                    String.format("%.2f", playerPosition.y),
                    String.format("%.2f", playerPosition.z)
                    )), false);
        }
    }


    /**
     * Converts coordinates to overworld/nether from the input
     */
    // Integrated method to add commands to the "/" menu
    public static int convertCoordinates(CommandContext<ServerCommandSource> context, String dimension) {
        int xPos = IntegerArgumentType.getInteger(context, "x");
        int yPos = IntegerArgumentType.getInteger(context, "y");
        int zPos = IntegerArgumentType.getInteger(context, "z");

        boolean isNether = dimension.equals("nether");

        int convertedX = isNether ? xPos / 8 : xPos * 8;
        int convertedZ = isNether ? zPos / 8 : zPos * 8;

        context.getSource().sendFeedback(() ->
                Text.literal("%s: X = %d, Y = %d, Z = %d".formatted(
                        (isNether ? "Nether" : "Overworld"),
                        convertedX,
                        yPos,
                        convertedZ)),
                        false
                );
        return 1;
    }
}
