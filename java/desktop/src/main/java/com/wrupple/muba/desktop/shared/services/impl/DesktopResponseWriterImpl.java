package com.wrupple.muba.desktop.shared.services.impl;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wrupple.muba.desktop.server.chain.command.DesktopResponseWriter;
import com.wrupple.muba.desktop.server.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.domain.VegetateException;
import com.wrupple.vegetate.server.services.ObjectMapper;

/**
 * I don't particularly like how vegetate forces this whole "tokens" ordeal.
 * HTML documents may benefit from this inlining of requests into a single
 * response but by definition, HTML response are single response for a single
 * requests, so this class's pourpose is to abstract all those multi-response
 * aspects from the writing process, and delegates de actual writing to another
 * command
 * 
 * How ever, the writing of the desktop's service manifest should be similarly
 * handled accross all desktop writers, so it's implemented here
 * 
 * @author japi
 *
 */
public class DesktopResponseWriterImpl implements DesktopResponseWriter {

	protected final ObjectMapper mapper;
	private final DesktopWriterCommand delegate;

	@Inject
	public DesktopResponseWriterImpl(ObjectMapper mapper, DesktopWriterCommand delegate) {
		super();
		this.delegate = delegate;
		this.mapper = mapper;
	}

	@Override
	public void close(DesktopBuilderContext mainToken, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		delegate.execute(mainToken);
	}

	@Override
	public void writeServiceManifest(VegetateServiceManifest manifest, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// WRITE IT IN XML? AS HTML IS XML AND ALL...?
		mapper.writeValue(resp.getWriter(), manifest);
	}

	@Override
	public void writeStartToken(DesktopBuilderContext mainToken, String key, int index, int length, DesktopBuilderContext token, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

	}

	@Override
	public void writeEndToken(DesktopBuilderContext mainToken, String key, int index, int length, DesktopBuilderContext token, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

	}

	@Override
	public void writeErrorOfToken(DesktopBuilderContext mainToken, String key, DesktopBuilderContext token, HttpServletRequest req, HttpServletResponse resp,
			VegetateException e) throws IOException {

	}

	@Override
	public void writeStartResponse(DesktopBuilderContext mainToken, Map<String, DesktopBuilderContext> token, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}

	@Override
	public void writeEndResponse(DesktopBuilderContext mainToken, Map<String, DesktopBuilderContext> token, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}

	@Override
	public void writeStartFunctionWrapper(DesktopBuilderContext mainToken, String function, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		throw new IllegalArgumentException("NOT SUPPORTED");
	}

	@Override
	public void writeEndFunctionWrapper(DesktopBuilderContext mainToken, String function, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		throw new IllegalArgumentException("NOT SUPPORTED");
	}

	@Override
	public void open(DesktopBuilderContext mainToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {

	}

}
