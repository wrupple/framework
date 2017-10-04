package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.annotations.InheritanceTree;

/**
 * checks parent hierarchu for duplicates, and cycles and such
 * 
 * @author japi
 *
 */
public interface CatalogInheritanceValidator  extends ConstraintValidator<InheritanceTree, Long>  {

}
