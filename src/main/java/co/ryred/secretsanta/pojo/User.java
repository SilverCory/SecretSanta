package co.ryred.secretsanta.pojo;

import co.ryred.secretsanta.SecretSantaPlugin;
import co.ryred.secretsanta.util.GeneralUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 02/12/2015.
 */
@AllArgsConstructor
public class User
{

	@Getter
	final String userName;
	@Getter
	final UUID uuid;
	final File configFile;
	final YamlConfiguration config;
	List<SackItem> sackItemList = new ArrayList<>( 45 );

	@SuppressWarnings("unchecked")
	public User( SecretSantaPlugin plugin, OfflinePlayer player )
	{

		this.configFile = new File( new File( plugin.getDataFolder(), "userdata" ), player.getUniqueId().toString() + ".yml" );
		this.configFile.getParentFile().mkdirs();
		this.config = YamlConfiguration.loadConfiguration( configFile );

		if ( !configFile.exists() ) {
			this.userName = player.getName();
			this.uuid = player.getUniqueId();
			return;
		}

		if ( !player.getName().equals( getConfig().getString( "name" ) ) ) {
			getConfig().set( "name", player.getName() );
			try {
				getConfig().save( configFile );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}

		this.userName = getConfig().getString( "name", player.getName() );
		this.uuid = UUID.fromString( getConfig().getString( "uuid", player.getUniqueId().toString() ) );
		this.sackItemList = (List<SackItem>) getConfig().getList( "itemList" );
		/*for ( Map<?, ?> map : getConfig().getMapList( "itemList" ) ) {

			if ( map == null ) {
				plugin._D( "Map = null" );
				continue;
			} else {
				plugin._D( "SackItem: " + map );
			}
			sackItemList.add( SackItem.deserialize( (Map<String, Object>) map ) );

		}*/

	}

	public void saveUser( SecretSantaPlugin plugin )
	{

		getConfig().set( "name", userName );
		getConfig().set( "uuid", uuid.toString() );
		getConfig().set( "itemList", sackItemList );

		try {
			getConfig().save( configFile );
		} catch ( IOException e ) {
			e.printStackTrace();
		}

	}

	public boolean removeItemViaIndex( int index )
	{
		SackItem sackItem = sackItemList.remove( index );

		return sackItem != null;

	}

	public Inventory getInventory( boolean admin )
	{

		Inventory inv = Bukkit.createInventory( null, 45, GeneralUtils.c( "&c&l" + userName + "'s sack" ) );

		for ( SackItem sackItem : sackItemList ) {
			inv.addItem( sackItem.getDisplayItem( admin ) );
		}

		return inv;

	}

	public void addItem( ItemStack is, UUID sender )
	{
		if ( is == null || is.getType() == Material.AIR ) {
			throw new IllegalArgumentException( "You need an item in your hand!" );
		}

		for ( SackItem item : sackItemList ) {
			if ( item.getLeaver().equals( sender ) ) {
				throw new IllegalArgumentException( "You've already left a gift for " + userName );
			}
		}

		sackItemList.add( new SackItem( is, sender ) );

	}

	private YamlConfiguration getConfig()
	{
		return this.config;
	}

}
