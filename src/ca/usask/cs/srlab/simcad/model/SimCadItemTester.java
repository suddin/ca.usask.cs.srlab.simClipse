package ca.usask.cs.srlab.simcad.model;

import org.eclipse.core.expressions.PropertyTester;

/**
 * Tests whether an object is part of the Favorites collection.
 */
public class SimCadItemTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		boolean found = false;
		ISimCadItem[] favorites = SimCadManager.getManager().getSimCadItems();
		for (int i = 0; i < favorites.length; i++) {
			ISimCadItem item = favorites[i];
			found = item.isFavoriteFor(receiver);
			if (found)
				break;
		}
		
		if ("isSimCadItem".equals(property))
			return found;
		
		if ("notSimCadItem".equals(property))
			return !found;
		
		return false;
	}

}
