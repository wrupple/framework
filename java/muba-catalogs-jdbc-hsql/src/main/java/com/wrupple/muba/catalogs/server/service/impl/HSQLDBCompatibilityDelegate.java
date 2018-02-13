package com.wrupple.muba.catalogs.server.service.impl;

import java.sql.SQLException;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class HSQLDBCompatibilityDelegate implements SQLCompatibilityDelegate {

	
	private static final String LAST_ID = "CALL IDENTITY()";
	private final String path;
    private final Character delimiter;

    @Inject
	public HSQLDBCompatibilityDelegate(@Named("catalog.hsqldb.path") String path,@Named("catalog.sql.delimiter") Character delimiter) {
		this.path = path;
		this.delimiter=delimiter;
	}

	@Override
	public void alterInsertStatement(CatalogActionContext context, StringBuilder builder) {

	}


	@Override
	public Object getLastInsertedId(CatalogActionContext context, QueryRunner runner,
			JDBCSingleLongKeyResultHandler keyHandler) throws SQLException {
		return runner.query(LAST_ID, keyHandler);
	}

	@Override
	public boolean isSequential() {
		return true;
	}

	@Override
	public String buildTableConfigurationStatement(JDBCMappingDelegateImpl jdbcMappingDelegate, String mainTable, CatalogDescriptor catalog, StringBuilder builder, SQLCompatibilityDelegate compatibility, CatalogActionContext context) {
// <tablename> SOURCE <quoted_filename_and_options> [DESC]
		builder.append("SET TABLE ");
        builder.append(delimiter);
		if(mainTable==null){
			jdbcMappingDelegate.getTableNameForCatalog(catalog,context,builder);
		}else{
			builder.append(mainTable);
        }
        builder.append(delimiter);
        builder.append(" SOURCE \"");
        if(mainTable==null){
            jdbcMappingDelegate.getTableNameForCatalog(catalog,context,builder);
        }else{
            builder.append(mainTable);
        }
        builder.append(".csv");
        builder.append(";ignore_first=true;all_quoted=true;encoding=UTF-8");
        builder.append('\"');
		return builder.toString();
	}

	@Override
	public boolean requiresPostCreationConfig() {
		return path!=null;
	}

}
