package net.uwa.vanillautilityplus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

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
