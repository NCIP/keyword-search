/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/catissue-keyword-search/LICENSE.txt for details.
 */

package titli.controller.interfaces;


/**
 * Interface that represents object metadata like tableName and identifier
 * @author pooja_deshpande
 *
 */
public interface ObjectMetadataInterface
{
	public String getTableName(Object obj);
	public String getUniqueIdentifier(Object obj);
}
