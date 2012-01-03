package ca.usask.cs.srlab.simcad.model;

import org.eclipse.core.runtime.IAdaptable;

public interface ISimCadItem extends IAdaptable {
	String getName();

	void setName(String newName);

	String getLocation();

	boolean isFavoriteFor(Object obj);

	SimCadItemType getType();

	String getInfo();

	static ISimCadItem[] NONE = new ISimCadItem[] {};
}