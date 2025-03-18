package net.uwa.vanillautilityplus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaUtilityPlus implements ModInitializer {

	public static final String MOD_ID = "vanilla-utility-plus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		// Detects all chat messages sent by the client, something will happen if specific messages are sent
		// register can also be done like .register((message) -> VanillaUtilityPlusCommands.parseChatMessage(message))
		ClientSendMessageEvents.ALLOW_CHAT.register(VanillaUtilityPlusCommands::parseChatMessage);

		// Does not require the sender or server parameters
		// The handler is where the player and their name is provided
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> VanillaUtilityPlusCommands.playerJoinLeaveDetector(handler, true));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> VanillaUtilityPlusCommands.playerJoinLeaveDetector(handler, false));







		// Integrated method to add commands to the "/" menu
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("pos")
					.then(CommandManager.literal("nether")
					.then(CommandManager.argument("x", IntegerArgumentType.integer())
					.then(CommandManager.argument("y", IntegerArgumentType.integer())
					.then(CommandManager.argument("z", IntegerArgumentType.integer())
							.executes(context -> VanillaUtilityPlusCommands.convertCoordinates(context, "nether"))))))
					.then(CommandManager.literal("overworld")
					.then(CommandManager.argument("x", IntegerArgumentType.integer())
					.then(CommandManager.argument("y", IntegerArgumentType.integer())
					.then(CommandManager.argument("z", IntegerArgumentType.integer())
							.executes(context -> VanillaUtilityPlusCommands.convertCoordinates(context, "overworld"))))))
			);
		});

	}
}
