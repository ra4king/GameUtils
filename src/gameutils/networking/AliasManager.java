package gameutils.networking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles class aliases.
 * @author Roi Atalla
 */
public class AliasManager {
	private static Map<String,Class<?>> alias = Collections.synchronizedMap(new HashMap<String,Class<?>>());
	
	/**
	 * Returns the Class object associated with the specified alias.
	 * @param name The alias associated with the Class object.
	 * @return The Class object associated with the specified alias.
	 */
	public static Class<?> getClass(String name) {
		return alias.get(name.intern());
	}
	
	/**
	 * Returns the alias that associates with the specified Class object.
	 * @param clazz The Class object associated by the alias.
	 * @return The alias that associates with the specified Class object.
	 */
	public static String getAlias(Class<?> clazz) {
		String name = null;
		Set<String> set = alias.keySet();
		for(String s : set) {
			if(alias.get(s) == clazz) {
				name = s;
				break;
			}
		}
		
		return name;
	}
	
	/**
	 * Registers the specified Serializable object with the specified alias.
	 * @param s The object who's Class instance is associated by the specified alias.
	 * @param aliasName The alias to associated with the Class instance.
	 * @return The alias that was set.
	 */
	public static String register(Serializable s, String aliasName) {
		alias.put(aliasName,s.getClass());
		return aliasName;
	}
	
	/**
	 * Registers the specified Serializable object.
	 * @param s The Serializable object.
	 * @return The alias associated with the specified object's Class instance.
	 */
	public static String register(Serializable s) {
		String name = getSimpleName(s);
		alias.put(name,s.getClass());
		return name;
	}
	
	private static String getSimpleName(Object o) {
		return o.getClass().getName().substring(o.getClass().getName().lastIndexOf('.')+1);
	}
}