package com.wrupple.muba.cms.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.cms.domain.Document;
import com.wrupple.muba.cms.server.services.ContentManagerManifest;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

@Singleton
public class WriteFormatedDocumentImpl implements WriteFormatedDocument {
	protected static final Logger log = LoggerFactory.getLogger(WriteFormatedDocumentImpl.class);
	private final CatalogPropertyAccesor accessor;
	private final ContentManagerManifest cmsm;
	private final Pattern pattern;
	private final String contentType;
	private final String characterEncoding;
	
	@Inject
	public WriteFormatedDocumentImpl(String characterEncoding,String contentType,ContentManagerManifest cmsm, CatalogPropertyAccesor accessor,@Named("cms.tokenRegEx")String pattern /**"\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}"*/) {
		super();
		this.characterEncoding=characterEncoding;
		this.cmsm=cmsm;
		this.accessor=accessor;
		this.contentType=contentType;
		log.info("[CMS TOKEN REGEX] {}",pattern);
		
		this.pattern = Pattern.compile(pattern);
	}
	
	
	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext ccontext = (CatalogExcecutionContext) c;
		List<Document> list = ccontext.getResults();
		if (list != null && !list.isEmpty()) {
			HttpServletResponse resp = ccontext.getRequest().getServletContext().getResponse();
			resp.setContentType(contentType);
			resp.setCharacterEncoding(characterEncoding);
			PrintWriter out = resp.getWriter();
			String value;
			for (Document sheet : list) {
				value = sheet.getValue();
				writeDocument(value, out,  ccontext.getLocale(), ccontext);
			}

			out.close();
		} else {
			throw new IllegalArgumentException("no html sources collected with the given Id");
		}
		return CONTINUE_PROCESSING;
	}

	private void writeDocument(String template, PrintWriter out, String language,
			CatalogExcecutionContext context) {
		log.info("[WRITE DOCUMENT]");
		// TODO fallback laguage in case token isnt found on required language
		
		Matcher matcher = pattern.matcher(template);
		if (matcher.find()) {
			matcher.reset();
			int start;
			int end;
			int currentIndex = 0;
			String rawToken;
			while (matcher.find()) {
				start = matcher.start();
				if(start>0 && template.charAt(start)!='\\'){
					end = matcher.end();
					out.println(template.substring(currentIndex, start));
					rawToken = matcher.group();
				//token = digestToken(rawToken);
					try {
						processToken( out,  rawToken, language, context);
					} catch (Exception e) {
						out.println("Error processing token : " + rawToken);
					}
					currentIndex = end;
				}
			}
			if (currentIndex < template.length()) {
				out.println(template.substring(currentIndex, template.length()));
			}
		} else {
			out.println(template);
		}
	}

	private void processToken( PrintWriter out,  String rawToken, String language, CatalogExcecutionContext context)
			throws Exception {
		log.trace("[DOCUMENT TOKEN] {}",rawToken);
		//ask catalog to deduce value
		accessor.synthethizeFieldValue(rawToken, context.getEntryValue(), null, context.getCatalogDescriptor(), context, session, field);
		//maybe cms ai can deduce if failed?
		cmsm.createExcecutionContext(context, token.getTokenValues(), token.getSerializedContextSeed());
		//ask other part of cms to format it into the document ( see catalog property accessor)
		cmsm.createExcecutionContext(context, token.getTokenValues(), token.getSerializedContextSeed());
	}



}
