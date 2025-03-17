package net.uwa.vanillautilityplus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class VanillaUtilityPlusCommands {

    /**
     * Converts coordinates to overworld/nether from the input
     */
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
