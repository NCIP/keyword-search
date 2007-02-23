/**
 * 
 */
package titli.controller.interfaces.entry;

import java.util.List;

import titli.controller.interfaces.record.Record;

/**
 * Part of the Entry.
 * Many SubEntries make up an Entry in a tree manner
 * @author Juber Patel
 *
 */
public interface SubEntry extends Record
{
	/**
	 * Get the list of SubEntries that are at the next immediate level of the tree
	 * @return the list of SubEntries, null if this is a leaf SubEntry
	 */
	List<SubEntry> getNextLevelSubEntries();
	
	
}
