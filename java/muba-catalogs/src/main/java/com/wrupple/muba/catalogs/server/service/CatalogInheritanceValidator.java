package com.wrupple.muba.catalogs.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.catalogs.domain.annotations.InheritanceTree;

/**
 * checks parent hierarchu for duplicates, and cycles and such
 * 
 * @author japi
 *
 */
public interface CatalogInheritanceValidator  extends ConstraintValidator<InheritanceTree, Long>  {

}
