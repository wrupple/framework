package com.wrupple.muba.catalogs.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.vegetate.server.domain.annotations.InheritanceTree;

/**
 * checks parent hierarchu for duplicates
 * 
 * @author japi
 *
 */
public interface CatalogInheritanceValidator  extends ConstraintValidator<InheritanceTree, Object>  {

}
