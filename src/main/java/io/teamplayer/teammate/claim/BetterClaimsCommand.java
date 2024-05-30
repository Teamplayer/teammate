package io.teamplayer.teammate.claim;

import co.aikar.commands.annotation.*;
import io.teamplayer.teammate.command.CommonBaseCommand;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@CommandAlias("betterclaims|bclaims")
public class BetterClaimsCommand extends CommonBaseCommand {

    /**
     * Base permission node for better claims commands.
     */
    private static final String PERMISSION_BASE_NODE = "teammate.betterclaims.";

    /**
     * The teleport subcommand name.
     */
    private static final String TELEPORT_COMMAND = "tp";

    /**
     * The instance of the plugin that registered this command.
     */
    private final Plugin plugin;

    /**
     * Grief prevention plugin to manage grief prevention hooks.
     */
    private final GriefPrevention griefPrevention;

    /**
     * Construct a new {@link BetterClaimsCommand}.
     *
     * @param plugin          The instance of the plugin registering this command.
     * @param griefPrevention The instance of the GriefPrevention plugin.
     */
    public BetterClaimsCommand(Plugin plugin, GriefPrevention griefPrevention) {
        super(TextColor.color(0xbfffbf), TextColor.color(0x00ff00));
        this.plugin = plugin;
        this.griefPrevention = griefPrevention;
    }

    @Subcommand("list")
    @CommandPermission(PERMISSION_BASE_NODE + "list")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @Description("List a player's claims")
    public void onList(CommandSender sender, OfflinePlayer target) {
        getPlayerClaims(target.getUniqueId())
                .thenAccept(c ->
                    sender.sendMessage(
                            Component.text()
                                    .append(getHeaderFooter())
                                    .append(Component.newline())
                                    .append(getTitle(target.getName() + "'s Claims"))
                                    .append(Component.newline())
                                    .append(getClaimsListMessage(c))
                                    .append(getHeaderFooter())
                                    .build()
                    )
                )
                .orTimeout(3, TimeUnit.SECONDS)
                .whenComplete((v, e) -> {
                    if (e != null) {
                        Bukkit.getLogger().log(Level.WARNING, "Issue getting player claims.", e);
                        sender.sendMessage(
                                Component.text("Unable to retrieve player claims.", NamedTextColor.RED));
                    }
                });
    }

    @Subcommand(TELEPORT_COMMAND)
    @CommandPermission(PERMISSION_BASE_NODE + "teleport")
    @Syntax("<claim-id>")
    @Description("Teleport to a claim by ID")
    public void onTeleport(Player player, long id) {
        getClaim(id)
                .thenAccept(c -> {
                    Component message;

                    if (c.isPresent()) {
                        message = Component.text()
                                .append(prefix)
                                .append(Component.text().content("Teleporting you to claim ").color(primaryColor))
                                .append(Component.text().content("#" + c.get().getID()).color(secondaryColor))
                                .append(Component.text().content(".").color(primaryColor))
                                .build();
                        Bukkit.getScheduler().runTask(plugin, () -> player.teleport(getCentralLocation(c.get())));
                    } else {
                        message = Component.text()
                                .append(prefix)
                                .append(Component.text("Unable to find claim with that ID.", primaryColor))
                                .build();
                    }

                    player.sendMessage(message);
                });
    }

    /**
     * Retrieves the claims owned by a player.
     *
     * @param playerUUID The UUID of the player.
     * @return A future that will be completed with a list of the player's claims.
     */
    private CompletableFuture<List<Claim>> getPlayerClaims(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> griefPrevention.dataStore.getPlayerData(playerUUID)
                .getClaims().stream()
                .sorted(Comparator.comparing(Claim::getArea).reversed())
                .toList());
    }

    /**
     * Retrieves a claim based on the specified ID.
     *
     * @param id The ID of the claim to retrieve.
     * @return A future that will be completed with the retrieved claim.
     */
    private CompletableFuture<java.util.Optional<Claim>> getClaim(long id) {
        return CompletableFuture.supplyAsync(() ->
                java.util.Optional.ofNullable(griefPrevention.dataStore.getClaim(id)));
    }

    private Component getClaimsListMessage(Collection<Claim> claims) {
        TextComponent.Builder message = Component.text();

        for (Claim claim : claims) {
            Location claimCorner = claim.getGreaterBoundaryCorner();

            message.append(Component.text().content(" - ").color(NamedTextColor.GRAY))
                    .append(Component.text().content("#" + claim.getID()).color(secondaryColor))
                    .append(Component.text().content(" " + claim.getArea() + " blocks ").color(primaryColor))
                    .append(
                            Component.text()
                                    .content('(' + claimCorner.getWorld().getName() + '/' + claimCorner.getBlockX() +
                                            '/' + claimCorner.getBlockZ() + ')')
                                    .color(NamedTextColor.GRAY)
                                    .hoverEvent(
                                            HoverEvent.showText(
                                                    Component.text("Click to teleport to claim center")
                                                            .color(NamedTextColor.GRAY)
                                            )
                                    )
                                    .clickEvent(
                                            ClickEvent.runCommand(
                                                    '/' + getName() + ' ' + TELEPORT_COMMAND + ' ' + claim.getID()
                                            )
                                    )
                    )
                    .append(Component.newline());
        }

        return message.build();
    }

    /**
     * Calculates the central location of a claim.
     *
     * @param claim The claim for which to calculate the central location.
     * @return The central location of the claim.
     */
    private Location getCentralLocation(Claim claim) {
        Location center = claim.getLesserBoundaryCorner().add(claim.getGreaterBoundaryCorner()).multiply(0.5);

        return center.getWorld().getHighestBlockAt(center.getBlockX(), center.getBlockZ())
                .getLocation().add(0, 1, 0);
    }

}
