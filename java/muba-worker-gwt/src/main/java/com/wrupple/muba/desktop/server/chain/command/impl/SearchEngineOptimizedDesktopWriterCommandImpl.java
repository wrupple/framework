package com.wrupple.muba.desktop.server.chain.command.impl;

import com.google.gwt.http.client.UrlBuilder;
import com.wrupple.muba.bpm.domain.DomainSystemProperties;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.server.domain.imp.ProcessTaskDescriptorImpl;
import com.wrupple.muba.desktop.server.chain.command.SearchEngineOptimizedDesktopWriterCommand;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.muba.desktop.server.service.impl.AbstractDataDrivenServerModule;
import com.wrupple.muba.desktop.shared.services.UrlParser;
import com.wrupple.muba.desktop.shared.services.UserTaskWriterDictionary;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.PersistentImageMetadata;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.impl.VegetateUrlServiceBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SearchEngineOptimizedDesktopWriterCommandImpl implements SearchEngineOptimizedDesktopWriterCommand {

	private static final Logger log = LoggerFactory.getLogger(SearchEngineOptimizedDesktopWriterCommandImpl.class);

	private final CatalogServiceManifest manifest;
	private final ObjectMapper mapper;
	private final UserTaskWriterDictionary transactionDictionary;
	private final UrlParser urlParser;
	private final ProcessTaskDescriptorImpl REDIRECTING_VIRTUAL_TASK;
	private final DataStoreManager dsm;

	@Inject
	public SearchEngineOptimizedDesktopWriterCommandImpl(DataStoreManager dsm, UrlParser urlParser, CatalogServiceManifest manifest, ObjectMapper mapper,
			UserTaskWriterDictionary transactionDictionary) {
		super();
		this.dsm = dsm;
		this.urlParser = urlParser;
		this.transactionDictionary = transactionDictionary;
		this.manifest = manifest;
		this.mapper = mapper;
		REDIRECTING_VIRTUAL_TASK = new ProcessTaskDescriptorImpl();
	}

	@Override
	public boolean execute(Context c) throws Exception {
		DesktopBuilderContext context = (DesktopBuilderContext) c;
		DomainSystemProperties systemProps = (DomainSystemProperties) context.getCatalogContext().getDomainContext().getSystemSettings();
		HttpServletResponse resp = context.getResponse();
		HttpServletRequest req = context.getRequest();
		resp.setCharacterEncoding(context.getCharacterEncoding());
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		ApplicationItem main = pickMainApplicationItem(context);

		DataStoreManager dsm = context.getDataStoreManager();
		CatalogDataAccessObject<ProcessDescriptor> processDao = dsm.getOrAssembleDataSource(ProcessDescriptor.CATALOG, context.getCatalogContext(),
				ProcessDescriptor.class);
		String processId = String.valueOf(main.getProcess());
		List<ProcessTaskDescriptor> tasks = null;
		ProcessDescriptor process = processDao.read(processId);
		boolean processClosingTask = false;
		ProcessTaskDescriptor task = null;
		String[] pathTokens = context.getPathTokens();
		int currentTokenIndex = context.getNextPathToken();
		int taskTokenIndex = currentTokenIndex;
		if (process != null) {
			List<? extends Object> steps = process.getProcessSteps();
			CatalogDataAccessObject<ProcessTaskDescriptor> taskDao = dsm.getOrAssembleDataSource(ProcessTaskDescriptor.CATALOG, context.getCatalogContext(),
					ProcessTaskDescriptor.class);
			tasks = taskDao.read(FilterDataUtils.createSingleKeyFieldFilter(CatalogEntry.ID_FIELD, steps));
			int processLength = tasks.size();
			if (currentTokenIndex < pathTokens.length) {
				String taskToken = pathTokens[currentTokenIndex];
				String currentTaskToken;
				boolean found = false;
				for (int i = 0; i < processLength; i++) {
					task = tasks.get(i);
					// the url may either match the vanity id or the actual Id
					currentTaskToken = task.getVanityId();
					processClosingTask = i == (processLength - 1);
					if (taskToken.equalsIgnoreCase(currentTaskToken)) {
						setNextTaskUrl(context, main, tasks, processLength, task, i);
						context.setNextPathToken(currentTokenIndex++);
						found = true;
						break;
					} else if (task.getIdAsString().equalsIgnoreCase(currentTaskToken)) {
						setNextTaskUrl(context, main, tasks, processLength, task, i);
						context.setNextPathToken(currentTokenIndex++);
						found = true;
						break;
					}
				}
				if (!found) {
					// no mathching task, use the first task
					processClosingTask = processLength == 1;
					setNextTaskUrl(context, main, tasks, processLength, tasks.get(0), 0);
					task = tasks.get(0);
				}
			} else {
				throw new IndexOutOfBoundsException("Invalid Path Token Index");
			}
		}

		// DOCTYPE
		writer.print("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n\r");
		// html
		writer.print("<html>\n\r");
		// head
		writer.print("<head>\n\r");
		writeHead(writer, context, systemProps, req, main, pathTokens, taskTokenIndex, task);
		writer.print("</head>\n\r");
		// body
		writer.print("<body>\n\r");
		writeBody(writer, context, main, process, task, processClosingTask);
		writer.print("</body>\n\r");

		// close html
		writer.print("</html>\n\r");
		writer.close();
		return CONTINUE_PROCESSING;

	}

	private ApplicationItem pickMainApplicationItem(DesktopBuilderContext context) {

		ApplicationItem dpss = context.getDesktopPlaceHierarchy();
		String[] path = context.getPathTokens();
		if (path == null || path.length == 0) {
			return dpss;
		} else {
			List<? extends ApplicationItem> children = dpss.getChildItemsValues();
			int index = context.getNextPathToken();

			ApplicationItem main = recursivelyPickMainItem(context, index, path, children, dpss);
			context.getBreadCrumbs().remove(dpss);
			context.getBreadCrumbs().remove(main);
			context.setNextPathToken(index);
			return main;
		}

	}

	private ApplicationItem recursivelyPickMainItem(DesktopBuilderContext context, int index, String[] path, List<? extends ApplicationItem> children,
			ApplicationItem parent) {
		if (children == null) {
			return parent;
		}
		context.pushBreadCrumb(parent);
		String activity;
		String token = path[index];
		for (ApplicationItem item : children) {
			activity = item.getActivity().toLowerCase();
			if (token.equalsIgnoreCase(activity)) {
				// match token
				if ((index + 1) < path.length && item.getChildItemsValues() != null) {
					// there is more tokens
					index++;
					return recursivelyPickMainItem(context, index, path, item.getChildItemsValues(), item);

				} else {
					return item;
				}

			}

		}

		return parent;
	}

	private void writeHead(PrintWriter writer, DesktopBuilderContext context, DomainSystemProperties systemProps, HttpServletRequest req, ApplicationItem main,
			String[] path, int taskTokenIndex, ProcessTaskDescriptor task) {

		// Content Encoding
		writer.print("<meta http-equiv=\"content-type\" content=\"text/html; charset=");
		writer.print(context.getCharacterEncoding());
		writer.print("\" />\n");
		writer.println();
		// title
		writer.print("<title>");

		// TODO app item defines title writing params ${app.activity}
		// ${catalog.name} etc...
		writer.print(main.getName());
		writer.print("</title>");
		writer.println();
		writer.print("<link rel=\"canonical\" href=\"/");
		// https://WRUPPLE.COM/tree/place/[task by non-vanity-id]
		for (int i = 0; i < path.length; i++) {
			writer.print('/');
			if (taskTokenIndex == i) {
				// TODO should i check if we are actually handling the task by
				// the
				// vanity Id to print the regular id and viceversa? and not just
				// assume
				// we always use the vanity Id ?
				writer.print(task.getIdAsString());
			} else {
				writer.print(path[i]);

			}
		}
		writer.print("\" />");
		// TODO if this is the first task, consider writing the application
		// item's url as a cannonical link

		/*
		 * FIXME pagination <link rel="canonical"
		 * href="http://www.example.com/article?story=abc&page=2”/> <link
		 * rel="prev"
		 * href="http://www.example.com/article?story=abc&page=1&sessionid=123"
		 * />
		 */
		writer.println();
		writer.print("<meta name=\"description\" content=\"");
		writer.print(main.getDescription());
		writer.print("\" />\n");
		writer.println();

	}

	private void writeBody(PrintWriter writer, DesktopBuilderContext context, ApplicationItem main, ProcessDescriptor process, ProcessTaskDescriptor task,
			boolean processClosingTask) throws Exception {

		/*
		 * Find task to print
		 */

		if (process == null || task == null) {
			// 404
			writeNavigation(writer, context, main);
		} else {
			writeTaskHtml(writer, context, main, process, task, processClosingTask);
		}

	}

	private void setNextTaskUrl(DesktopBuilderContext context, ApplicationItem main, List<ProcessTaskDescriptor> process, int processLength,
			ProcessTaskDescriptor task, int taskIndex) throws Exception {
		int capacity = 50;
		boolean isLastTask = taskIndex == (processLength - 1);

		String[] tokens = context.getPathTokens();
		for (String t : tokens) {
			capacity += t.length();
			capacity += 1;
		}
		StringBuilder nextTaskUrl = new StringBuilder(capacity);

		ProcessTaskDescriptor nextTask;
		if (isLastTask) {
			String outputHandler = main.getOutputHandler();
			ApplicationItem dpss = context.getDesktopPlaceHierarchy();
			ApplicationItem nexyActivity;
			if (outputHandler == null || UrlParser.NEXT_APPLICATION_ITEM.equals(outputHandler)) {
				nexyActivity = urlParser.findNextTreeNode(main, dpss);
				if (nexyActivity == null) {
					throw new IllegalArgumentException("unable to determine next activity");
				} else {
					urlParser.getItemActivity(tokens, context.getFirstTokenIndex(), nexyActivity, dpss, nextTaskUrl);
					nextTask = determineNextTask(nexyActivity, context, nextTaskUrl);
				}

			} else if (UrlParser.EXPLICIT_APPLICATION_ITEM.equals(outputHandler)) {
				List<String> properties = main.getProperties();
				String activity;
				if (properties == null) {
					activity = null;
				} else {
					activity = null;
					for (String property : properties) {
						if (property.startsWith("activity")) {
							activity = property.substring(property.indexOf('='), property.length() - 1);
						}
					}
				}

				if (activity == null) {
					// expect output to either be an application item or a
					// controlNode with a foreign key poiting to an application
					// item
					nextTaskUrl.append(DESKTOP_APPLICATION_REDIRECT);
					nextTask = REDIRECTING_VIRTUAL_TASK;
				} else {
					nexyActivity = urlParser.getActivityItem(activity, dpss);
					if (nexyActivity == null) {
						throw new IllegalArgumentException("no such activity: " + activity);
					} else {
						nextTaskUrl.append('/');
						nextTaskUrl.append(activity);
						nextTaskUrl.append('/');
						nextTask = determineNextTask(nexyActivity, context, nextTaskUrl);
					}
				}

			} else {
				nextTaskUrl.append(DESKTOP_APPLICATION_REDIRECT);
				nextTask = REDIRECTING_VIRTUAL_TASK;
			}

		} else {
			nextTask = process.get(taskIndex + 1);
			for (String t : tokens) {
				nextTaskUrl.append('/');
				nextTaskUrl.append(t);
			}
			nextTaskUrl.append('/');
			nextTaskUrl.append(nextTask.getVanityId() == null ? nextTask.getId() : nextTask.getVanityId());

		}
		context.setSubmitTask(nextTask);
		context.setSubmitUrl(nextTaskUrl.toString());
	}

	private ProcessTaskDescriptor determineNextTask(ApplicationItem nexyActivity, DesktopBuilderContext context, StringBuilder nextTaskUrl) throws Exception {

		Number nextProcessId = nexyActivity.getProcess();
		CatalogDataAccessObject<ProcessDescriptor> processDao = dsm.getOrAssembleDataSource(ProcessDescriptor.CATALOG, context.getCatalogContext(),
				ProcessDescriptor.class);
		ProcessDescriptor nextProcess = processDao.read(nextProcessId.toString());
		List<? extends Object> tasks = nextProcess.getProcessSteps();

		CatalogDataAccessObject<ProcessTaskDescriptor> taskDao = dsm.getOrAssembleDataSource(ProcessTaskDescriptor.CATALOG, context.getCatalogContext(),
				ProcessTaskDescriptor.class);
		ProcessTaskDescriptor nextTask = taskDao.read(tasks.get(0).toString());
		nextTaskUrl.append(nextTask.getVanityId() == null ? nextTask.getIdAsString() : nextTask.getVanityId());
		return nextTask;
	}

	private void writeTaskHtml(PrintWriter writer, DesktopBuilderContext context, ApplicationItem main, ProcessDescriptor process, ProcessTaskDescriptor task,
			boolean processClosingTask) throws Exception {
		VegetateUrlServiceBuilder catalogUrlBuilder = new VegetateUrlServiceBuilder(null, null, UrlBuilder.PORT_UNSPECIFIED,
				AbstractDataDrivenServerModule.CATALOG_CHANNEL_ID, manifest, mapper);
		context.put(VegetateUrlServiceBuilder.class.getSimpleName(), catalogUrlBuilder);
		List<ApplicationItem> breadCrumbs = context.getBreadCrumbs();
		if (breadCrumbs != null) {
			writer.println("<nav>");
			for (int i = 0; i < breadCrumbs.size(); i++) {

				writeAnchor(breadCrumbs.subList(0, i), writer, "prev");

				writer.print("&gt;");
			}
			writer.println("</nav>");

		}
		String image = main.getImage();
		String name = main.getName();

		writer.println("<h2>");
		if (image != null) {
			writeFileLink(catalogUrlBuilder, writer, PersistentImageMetadata.CATALOG, image, String.valueOf(main.getDomain()), name);
		}
		writer.print(name);
		writer.println("</h2>");

		/*
		 * Print task
		 */

		String transaction = task.getTransactionType();

		if (ProcessTaskDescriptor.NAVIGATE_COMMAND.equals(transaction)) {
			writeNavigation(writer, context, main);
		} else {
			writeUserTask(writer, context, main, task, transaction, processClosingTask);
			writeNavigation(writer, context, main);
		}

	}

	public static void writeFileLink(VegetateUrlServiceBuilder catalogUrlBuilder, PrintWriter writer, String fileType, String fileKey, String domain,
			String alt) {
		CatalogActionRequestImpl actionRequst = new CatalogActionRequestImpl(domain, fileType, CatalogActionRequest.READ_ACTION, fileKey, "0", null, null);
		if (PersistentImageMetadata.CATALOG.equals(fileType)) {
			writer.print("<img src=\"");
			catalogUrlBuilder.writeRelativeUrl(actionRequst, writer);
			writer.print("\" alt=\"");
			writer.print(alt);
			writer.print("\" />");
		} else {
			writer.print("<a href=\"");
			catalogUrlBuilder.writeRelativeUrl(actionRequst, writer);
			writer.print("\" >");
			writer.print(alt);
			writer.print("</a>");
		}

	}

	private void writeNavigation(PrintWriter writer, DesktopBuilderContext context, ApplicationItem main) {
		writer.println("<hr/>");

		List<? extends ApplicationItem> children = main.getChildItemsValues();
		if (children != null) {
			writer.println("<nav>");

			ArrayList<ApplicationItem> pathing = new ArrayList<ApplicationItem>(context.getBreadCrumbs() == null ? 2 : context.getBreadCrumbs().size() + 2);
			if (context.getBreadCrumbs() != null) {
				pathing.addAll(context.getBreadCrumbs());
			}
			pathing.add(main);

			int lastIndex = pathing.size() + 1;
			for (ApplicationItem child : children) {
				pathing.set(lastIndex, child);
				writeAnchor(pathing, writer, "next");
			}

			writer.println("</nav>");
		}

	}

	private void writeAnchor(List<ApplicationItem> subList, PrintWriter writer, String rel) {
		if (subList == null || subList.isEmpty()) {
			return;
		}
		writer.print("<a href=\"/");
		ApplicationItem item = null;
		int size = subList.size();
		for (int i = 0; i < subList.size(); i++) {
			item = subList.get(i);
			writer.print(item.getActivity());
			if (i != size - 1) {
				writer.print('/');
			}
		}
		// next
		writer.print("\" rel=\"");
		writer.print(rel);
		writer.print("\" >");
		if (item == null) {
			writer.print("last");
		} else {
			writer.print(item.getName());
		}

		writer.print("</a>");
	}

	private void writeUserTask(PrintWriter writer, DesktopBuilderContext context, ApplicationItem main, ProcessTaskDescriptor task, String transaction,
			boolean processClosingTask) throws Exception {
		writer.println("<hr/>");
		writer.println("<h2>");
		writer.println(task.getName());
		writer.println("</h2>");
		writer.println("<main>");

		Command transactionWriter = transactionDictionary.getCommand(transaction);
		if (transactionWriter != null) {
			context.setActivity(main);
			context.setTask(task);
			transactionWriter.execute(context);
		}

		writer.println("</main>");
	}

}
