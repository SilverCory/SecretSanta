package co.ryred.secretsanta.commands;

import co.ryred.secretsanta.SecretSantaPlugin;
import co.ryred.secretsanta.util.GeneralUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 02/12/2015.
 */
public class SetLoreCommand implements CommandExecutor
{

	@Getter
	private final SecretSantaPlugin plugin;

	public SetLoreCommand( SecretSantaPlugin plugin )
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
	{

		if( !(sender instanceof Player ) ) {
			sender.sendMessage( GeneralUtils.c( "&cOnly players use this command!" ) );
			return true;
		}

		Player player = (Player) sender;

		if( args.length < 2 ) {
			sendUsage( sender, s );
			return true;
		}

		if( player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR ) {
			sender.sendMessage( GeneralUtils.c( "&cPut the item you want to add lore to in your hand." ) );
			return true;
		}

		int line = 0;
		try {
			line = Integer.parseInt( args[ 0 ] );
		} catch ( NumberFormatException ex ) {
			sender.sendMessage( GeneralUtils.c( "&c\"" + args[0] + "\" isn't a whole number!" ) );
			return true;
		}

		StringBuilder sb = new StringBuilder();
		for( int i = 1; i < args.length; i++ )
			sb.append( args[i] ).append( " " );

		ItemMeta im = player.getItemInHand().getItemMeta();

		List<String> loreList = new ArrayList<>();
		if( im.getLore() != null )
			loreList = im.getLore();

		loreList.set( line, GeneralUtils.c( sb.toString() ) );

		im.setLore( loreList );
		player.getItemInHand().setItemMeta( im );

		player.sendMessage( GeneralUtils.c( "The item lore has been set!" ) );

		return true;

	}

	private void sendUsage( CommandSender sender, String label )
	{

		sender.sendMessage( GeneralUtils.c(
				"&e== &9Set Lore &e==",
				"&a  /" + label + " [line] [... lore message ...]",
				"&b   Add [... lore message ...] to line number: [line]."
		) );

	}

}
