package co.ryred.secretsanta.commands;

import co.ryred.secretsanta.SecretSantaPlugin;
import co.ryred.secretsanta.pojo.User;
import co.ryred.secretsanta.util.CooldownUtil;
import co.ryred.secretsanta.util.GeneralUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 01/12/2015.
 */
public class SecretSantaCommand implements CommandExecutor
{

	@Getter
	private final SecretSantaPlugin plugin;

	public SecretSantaCommand( SecretSantaPlugin plugin )
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
	{

		if ( args.length == 1 && args[ 0 ].equalsIgnoreCase( "open" ) ) {

			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( GeneralUtils.c( "&cOnly players can do this." ) );
				return true;
			}

			if ( !SecretSantaPlugin.isClaimable() ) {
				sender.sendMessage( GeneralUtils.c( "&cYou can't open your sack yet! &c&lIt's not time!!" ) );
				return true;
			}

			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();

			if ( !plugin.hasUser( uuid ) ) {
				player.sendMessage( GeneralUtils.c( "&cSorry we can't find you.. :(",
													"&c  Try relogging?" ) );
				return true;
			}

			player.openInventory( plugin.getUser( uuid ).getInventory( false ) );

			return true;

		}
		else if ( args.length == 1 && args[ 0 ].equalsIgnoreCase( "toggle" ) ) {

			if ( !( sender instanceof ConsoleCommandSender ) ) {
				sender.sendMessage( GeneralUtils.c( "&cOnly console can do this. ;)" ) );
			}
			else {

				boolean toggled = !plugin.getConfig().getBoolean( "claimable", false );
				plugin.getConfig().set( "claimable", toggled );
				plugin.saveConfig();
				SecretSantaPlugin.setClaimable( toggled );

				String sentence = toggled ? " &anow be opened" : " &cnot be opened any more";

				sender.sendMessage( GeneralUtils.c( "Secret Santa sacks can" + sentence + "!" ) );

			}

			return true;

		}
		else if ( args.length == 1 ) {

			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( GeneralUtils.c( "&cOnly players can do this." ) );
				return true;
			}

			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();

			if ( CooldownUtil.getCooldown().isChilling( uuid ) ) {
				long remaningTime = CooldownUtil.getCooldown().getCooldownTime( TimeUnit.SECONDS, uuid );
				sender.sendMessage( GeneralUtils.c( "&cHey there, you're too eager!",
													"&c Just wait " + remaningTime + " more seconds!" ) );
				return true;
			}
			else {
				CooldownUtil.getCooldown().chillBabes( player.getUniqueId() );
			}

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( args[ 0 ] );

			if ( offlinePlayer == null || !offlinePlayer.hasPlayedBefore() ) {
				sender.sendMessage( GeneralUtils.c( "&cThat player doesn't exist or hasn't played on HC before! :(" ) );
				return true;
			}

			if( player.getUniqueId().equals( offlinePlayer.getUniqueId() ) ) {
				sender.sendMessage( GeneralUtils.c( "&c&lNo! You can't give yourself gifts." ) );
				return true;
			}

			User user;
			if( plugin.hasUser( offlinePlayer.getUniqueId() ) && plugin.getUser( uuid ) != null ) {
				user = plugin.getUser( uuid );
			} else {
				user = new User( plugin, offlinePlayer );
			}

			try {
				ItemStack is = player.getItemInHand().clone();
				is.setAmount(1);
				user.addItem( is, player.getUniqueId() );
				user.saveUser( plugin );
				player.sendMessage( GeneralUtils.c( "&aYour item has been added to " + user.getUserName() + "'s secret santa sack!" ) );
				player.setItemInHand( new ItemStack( Material.AIR ) );
				player.playSound( player.getLocation(), Sound.HORSE_SKELETON_HIT, 1L, 1L );
			} catch ( Exception e ) {
				player.sendMessage( GeneralUtils.c( "&c" + e.getMessage() ) );
			}

			return true;

		} else if ( args.length == 2 && args[0].equalsIgnoreCase( "admin" ) ) {

			if( !sender.hasPermission( "secretsanta.admin" ) ) {
				sendUsage( sender, s );
				return true;
			}

			if ( !( sender instanceof Player ) ) {
				sender.sendMessage( GeneralUtils.c( "&cOnly players can do this." ) );
				return true;
			}

			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( args[ 1 ] );

			if ( offlinePlayer == null || !offlinePlayer.hasPlayedBefore() ) {
				sender.sendMessage( GeneralUtils.c( "&cThat player doesn't exist or hasn't played on HC before! :(" ) );
				return true;
			}

			if( player.getUniqueId().equals( offlinePlayer.getUniqueId() ) ) {
				sender.sendMessage( GeneralUtils.c( "&c&lI'm not going to let you spoil the surprise!" ) );
				return true;
			}

			User user;
			if( plugin.hasUser( offlinePlayer.getUniqueId() ) && plugin.getUser( uuid ) != null ) {
				user = plugin.getUser( uuid );
			} else {
				user = new User( plugin, offlinePlayer );
			}

			player.openInventory( user.getInventory( true ) );

			return true;

		}

		sendUsage( sender, s );
		return true;

	}

	private void sendUsage( CommandSender sender, String label )
	{

		ArrayList<String> arrayList = new ArrayList<>();

		arrayList.addAll( Arrays.asList( GeneralUtils.c(
				"&e== &9Secret Santa &e==",
				"&a  /" + label + " [player]",
				"&b   Submit an item to [player]'s secret santa."
		) ) );

		if ( SecretSantaPlugin.isClaimable() ) {
			arrayList.add( GeneralUtils.c( "&a  /" + label + " open" ) );
			arrayList.add( GeneralUtils.c( "&b   Opens your sack!" ) );
		}

		if ( sender.hasPermission( "secretsanta.admin" ) ) {
			arrayList.add( GeneralUtils.c( "&a  /" + label + " admin [player]" ) );
			arrayList.add( GeneralUtils.c( "&b   Open [player]'s sack!" ) );
		}

		if ( sender instanceof ConsoleCommandSender ) {
			arrayList.add( GeneralUtils.c( "&a  /" + label + " toggle" ) );
			arrayList.add( GeneralUtils.c( "&b   Enables opening of the sacks!" ) );
		}

		sender.sendMessage( arrayList.toArray( new String[ arrayList.size() ] ) );

	}

}
