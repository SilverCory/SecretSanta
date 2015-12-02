package co.ryred.secretsanta;

import co.ryred.secretsanta.commands.SecretSantaCommand;
import co.ryred.secretsanta.commands.SetLoreCommand;
import co.ryred.secretsanta.pojo.SackItem;
import co.ryred.secretsanta.pojo.User;
import co.ryred.secretsanta.util.CooldownUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 01/12/2015.
 */
public class SecretSantaPlugin extends JavaPlugin
{

	@Getter
	private static boolean debugging = false;

	private static Logger logger = Logger.getGlobal();

	@Getter
	@Setter
	private static boolean claimable = false;

	private HashMap<UUID, User> userMap = new HashMap<>();

	@Override
	public void onLoad()
	{
		logger = getLogger();
	}

	@Override
	public void onEnable()
	{

		getServer().getScheduler().runTaskTimerAsynchronously( this, () -> CooldownUtil.getCooldown().purge(), 1500, 1500 );

		if ( !new File( getDataFolder(), "config.yml" ).exists() ) { saveDefaultConfig(); }

		debugging = getConfig().getBoolean( "debugging", false );
		claimable = getConfig().getBoolean( "claimable", false );
		CooldownUtil.setCooldownTime( TimeUnit.SECONDS, getConfig().getLong( "cooldown-time", 60L ) );

		_D( "Debugging enabled" );

		ConfigurationSerialization.registerClass( SackItem.class );

		getCommand( "secretsanta" ).setExecutor( new SecretSantaCommand( this ) );
		getCommand( "secretsanta" ).setExecutor( new SetLoreCommand( this ) );
		new PlayerListener( this );

	}

	public void _D( String s )
	{
		if ( isDebugging() ) { logger.info( "[D]" + s ); }
	}

	public void _D( String... s )
	{
		if ( !isDebugging() ) return;
		StringBuilder sb = new StringBuilder( "[D] " );
		for ( String str : s )
			sb.append( "| " ).append( str );
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
	}

	public void addUser( UUID uuid, User user )
	{
		userMap.put( uuid, user );
	}

	public void remove( UUID uuid )
	{
		userMap.remove( uuid );
	}

	public User getUser( UUID uuid )
	{
		return userMap.get( uuid );
	}

	public boolean hasUser( UUID uuid )
	{
		return userMap.containsKey( uuid );
	}

}
