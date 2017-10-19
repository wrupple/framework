package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasParent;
import com.wrupple.muba.event.domain.reserved.HasParentValue;

import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
public abstract class CatalogEntryImpl implements CatalogEntry {

	
	private Long id;
	private Object image;
	private String  name;
	@NotNull
	private Long domain;
	private boolean anonymouslyVisible;
	

	public final Long getId() {
		return id;
	}

	public final void setId(Long catalogId) {
		this.id = catalogId;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public final Object getImage() {
		return image;
	}

	public final void setImage(Object image) {
		this.image = image;
	}

	public final Long getDomain() {
		return domain;
	}

	public final void setDomain(Long domain) {
		this.domain = domain;
	}

	public final boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}

	public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}


	public  static final <T extends HasParent<T>> T getRootAncestor(HasParent<T> parent){
		T ancestor =  parent.getParent();
		if (ancestor == null) {
			ancestor = (T) parent;
		} else {
			while (ancestor.getParent() != null) {
				ancestor = ancestor.getParent();
			}
		}

		return ancestor;
	}

    public  static final <T extends HasParentValue<?,T>> T getRootAncestor(HasParentValue<?,T> parent){
        T ancestor =  parent.getParentValue();
        if (ancestor == null) {
            ancestor = (T) parent;
        } else {
            while (ancestor.getParentValue() != null) {
                ancestor = ancestor.getParentValue();
            }
        }

        return ancestor;
    }
}
