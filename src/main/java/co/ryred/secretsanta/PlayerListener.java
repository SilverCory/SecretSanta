package co.ryred.secretsanta;

import co.ryred.secretsanta.pojo.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 02/12/2015.
 */
public class PlayerListener implements Listener
{

	private final SecretSantaPlugin plugin;

	public PlayerListener( SecretSantaPlugin plugin )
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
	}

	@EventHandler
	public void onJoin( PlayerJoinEvent event )
	{
		plugin.addUser( event.getPlayer().getUniqueId(), new User( plugin, event.getPlayer() ) );
	}

	@EventHandler
	public void onQuit( PlayerQuitEvent event )
	{
		plugin.remove( event.getPlayer().getUniqueId() );
	}

	@EventHandler
	public void onKick( PlayerKickEvent event )
	{
		plugin.remove( event.getPlayer().getUniqueId() );
	}

}
