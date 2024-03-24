package io.teamplayer.teammate.pickup;

import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import io.teamplayer.teammate.command.CommonBaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

/**
 * The command to manage pickup toggling.
 */
@CommandAlias("pickup")
public class PickupToggleCommand extends CommonBaseCommand {

    /**
     * Base permission node for all pickup commands.
     */
    private static final String PERMISSION_BASE_NODE = "teammate.pickup.";

    /**
     * Permission node for modifying own ability to pick up items.
     */
    private static final String SELF_PERMISSION_NODE = PERMISSION_BASE_NODE + "self";

    /**
     * Permission node for modifying other people's ability to pick up items.
     */
    private static final String OTHER_PERMISSION_NODE = PERMISSION_BASE_NODE + "other";

    /**
     * Amount of seconds to temporarily enable pickups for.
     */
    private static final int TEMP_PICKUP_SECONDS = 5;

    /**
     * Controls whether players can pick up items.
     */
    private final PickupHandler pickupHandler;

    /**
     * Construct a new {@link PickupToggleCommand}.
     *
     * @param pickupHandler The handler that allows manipulation of pickups.
     */
    public PickupToggleCommand(PickupHandler pickupHandler) {
        super(TextColor.color(0xddcbff), TextColor.color(0x8a2be2));
        this.pickupHandler = pickupHandler;
    }

    @Default
    @Description("Toggle your ability to pick up items")
    @CommandPermission(SELF_PERMISSION_NODE)
    public void onSelfToggle(Player player) {
        boolean newState = pickupHandler.isPickupDisabled(player.getUniqueId());

        pickupHandler.setPickupToggle(player.getUniqueId(), newState);
        player.sendMessage(getSelfMessage(newState));
    }

    @Subcommand("toggle")
    @Description("Toggle a player's ability to pick up items")
    @Syntax("<player>")
    @CommandPermission(OTHER_PERMISSION_NODE)
    public void onOtherToggle(Player player, OnlinePlayer target) {
        boolean newState = pickupHandler.isPickupDisabled(target.getPlayer().getUniqueId());

        pickupHandler.setPickupToggle(target.getPlayer().getUniqueId(), newState);
        player.sendMessage(getOtherMessage(target.getPlayer().getName(), newState));
    }

    @Subcommand("set")
    @Description("Set the ability to pick up items for another player")
    @Syntax("<player> <on|off>")
    @CommandPermission(OTHER_PERMISSION_NODE)
    @CommandCompletion("@players on|off")
    public void onSet(Player player, OnlinePlayer target, boolean canPickup) {
        pickupHandler.setPickupToggle(target.getPlayer().getUniqueId(), canPickup);
        player.sendMessage(getOtherMessage(target.getPlayer().getName(), canPickup));
    }

    @Subcommand("check")
    @Description("Check if a player can pickup items")
    @Syntax("<player>")
    @CommandPermission(OTHER_PERMISSION_NODE)
    public void onCheck(Player player, OnlinePlayer target) {
        player.sendMessage(
                Component.text()
                        .append(prefix)
                        .append(Component.text().content(target.getPlayer().getName() + ' ').color(secondaryColor))
                        .append(Component.text()
                                .content((pickupHandler.isPickupDisabled(target.getPlayer().getUniqueId()) ? "cannot" : "can") +
                                        " pick up items.").color(primaryColor))
        );
    }

    @Subcommand("temp")
    @Description("Temporarily enable your ability to pick up items")
    @CommandPermission(SELF_PERMISSION_NODE)
    public void onTempToggle(Player player) {
        if (pickupHandler.isPickupDisabled(player.getUniqueId())) {
            player.sendMessage(
                    Component.text()
                            .append(prefix)
                            .append(Component.text().content("You have temporarily enabled your ability to pick up items for ").color(primaryColor))
                            .append(Component.text().content(TEMP_PICKUP_SECONDS + " seconds").color(secondaryColor))
                            .append(Component.text().content(".").color(primaryColor))
            );
            pickupHandler.tempEnablePickups(player.getUniqueId(), 20 * TEMP_PICKUP_SECONDS);
        } else {
            player.sendMessage(
                    Component.text()
                            .append(prefix)
                            .append(Component.text().content("Your ability to pick up items is already enabled.").color(primaryColor))
            );
        }

    }

    /**
     * Get the message for enable or disabling pickup on self.
     *
     * @param enabled Whether you are enabling pickup.
     * @return a message
     */
    private Component getSelfMessage(boolean enabled) {
        return Component.text()
                .append(prefix)
                .append(Component.text().content("You have ").color(primaryColor))
                .append(Component.text().content(enabled ? "enabled" : "disabled").color(secondaryColor))
                .append(Component.text().content(" your ability to pick up items.").color(primaryColor))
                .build();
    }

    /**
     * Get the message for enable or disabling pickup on another player.
     *
     * @param enabled    Whether you are enabling pickup.
     * @param targetName Name of the target.
     * @return a message
     */
    private Component getOtherMessage(String targetName, boolean enabled) {
        return Component.text()
                .append(prefix)
                .append(Component.text().content("You have ").color(primaryColor))
                .append(Component.text().content(enabled ? "enabled" : "disabled").color(secondaryColor))
                .append(Component.text().content(" the ability to pick up items for ").color(primaryColor))
                .append(Component.text().content(targetName).color(secondaryColor))
                .append(Component.text().content(".").color(primaryColor))
                .build();
    }

}
