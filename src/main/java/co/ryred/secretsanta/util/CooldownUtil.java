package co.ryred.secretsanta.util;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Cory Redmond
 *         Created by Cory on 01/07/2015.
 */
public class CooldownUtil
{

	private static CooldownUtil __INSTANCE__ = null;

	private static long cooldownTime = TimeUnit.SECONDS.toMillis( 50 );

	private final ConcurrentHashMap<UUID, Long> cooldownMap;
	private long lastPurged = 0;

	private CooldownUtil()
	{
		this.cooldownMap = new ConcurrentHashMap<UUID, Long>();
	}

	public static CooldownUtil getCooldown()
	{
		return __INSTANCE__ == null ? ( __INSTANCE__ = new CooldownUtil() ) : __INSTANCE__;
	}

	public static void setCooldownTime( TimeUnit timeUnit, long cooldownTime )
	{
		CooldownUtil.cooldownTime = timeUnit.toMillis( cooldownTime );
	}

	public void chillBabes( UUID uuid )
	{

		long time = System.currentTimeMillis() + cooldownTime;
		cooldownMap.put( uuid, time );

	}

	public void uCoolBrah( UUID uuid )
	{
		this.cooldownMap.remove( uuid );
	}

	public boolean isChilling( UUID uuid )
	{

		long time = System.currentTimeMillis();
		long utime = 0;
		Long userTime = cooldownMap.get( uuid );
		utime = userTime == null ? time - 50 : userTime;

		return ( time - utime ) < 0;

	}

	public long getCooldownTime( TimeUnit tu, UUID uuid )
	{

		long time = System.currentTimeMillis();
		long utime = 0;
		Long userTime = cooldownMap.get( uuid );
		utime = userTime == null ? time - 50 : userTime;

		return tu.convert( utime - time, TimeUnit.MILLISECONDS );

	}

	public synchronized void purge()
	{

		Iterator<Map.Entry<UUID, Long>> iterator = cooldownMap.entrySet().iterator();
		long time = System.currentTimeMillis();

		while ( iterator.hasNext() ) { if ( !isChilling( iterator.next().getKey() ) ) iterator.remove(); }

	}

	public void clear()
	{
		cooldownMap.clear();
	}

}
