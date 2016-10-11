package com.wrupple.muba.bootstrap.domain.reserved;

//FIXME - package names should always make grammatical sense
import java.util.Collection;

/**
 * A tree structure is a way of representing the hierarchical nature of related
 * entities. It is named a "tree structure" because the classic representation
 * resembles a tree, even though the chart is generally upside down compared to
 * an actual tree, with the "root" at the top and the "leaves" at the bottom
 * 
 * see? Wikipedia is super useful.
 * 
 * @author japi (in close colaboration with The Wikimedia Foundation)
 * 
 * @param <T>
 */
public interface HasChildren<T> {
	
	final String FIELD = "children";

	Collection<T> getChildren();
}
