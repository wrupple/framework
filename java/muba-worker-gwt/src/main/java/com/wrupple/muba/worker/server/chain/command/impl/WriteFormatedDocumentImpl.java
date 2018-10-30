package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.Document;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.List;

@Singleton
public class WriteFormatedDocumentImpl implements WriteFormatedDocument {
    protected static final Logger log = LogManager.getLogger(WriteFormatedDocumentImpl.class);
    private final CatalogEvaluationDelegate accessor;

    @Inject
    public WriteFormatedDocumentImpl(CatalogEvaluationDelegate accessor) {
        super();
        this.accessor = accessor;
    }


    @Override
    public boolean execute(Context c) throws Exception {
        CatalogActionContext ccontext = (CatalogActionContext) c;
        List<Document> list = ccontext.getResults();
        if (list != null && !list.isEmpty()) {
            PrintWriter out = ccontext.getRuntimeContext().getScopedWriter(ccontext);
            String value;
            for (Document sheet : list) {
                value = sheet.getValue();
                accessor.evalTemplate(value, out, ccontext.getLocale(), ccontext);
            }
            out.close();
        } else {
            throw new IllegalArgumentException("no html sources collected setRuntimeContext the given Id");
        }

        return CONTINUE_PROCESSING;
    }

}
