/**
 * 
 */
package titli.controller.interfaces.entry;


/**
 * This is a collection of records from different tables, related through foreign keeys.
 * This is the information entered at one time, typically throgh a UI.
 * This is a tree of records that starts at the root record.
 * There can be multiple records at each level other than the root level
 * @author Juber Patel
 *
 */
public interface Entry
{
	/**
	 * Get the root SubEntry which is the root of the entry.
	 * This SubEntry has refrences to other SubEntries
	 * @return the root SubEntry
	 */
	SubEntry getRootSubEntry();
	
	
	
}
