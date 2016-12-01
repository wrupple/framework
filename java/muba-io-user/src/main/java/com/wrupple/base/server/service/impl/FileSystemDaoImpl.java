package com.wrupple.base.server.service.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wrupple.base.domain.FileSystemEntry;
import com.wrupple.base.server.service.FileSystemDao;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.server.service.impl.AbstractUndoHistoryTransactionalDAO;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.FilterData;

public class FileSystemDaoImpl extends AbstractUndoHistoryTransactionalDAO<FileSystemEntry> implements FileSystemDao {

	protected final Path path;
	private Long domain;
	private CatalogDescriptor catalog;
	private final Charset charset;
	
	public FileSystemDaoImpl(String path,CatalogDescriptor catalog,String characterSet/* "US-ASCII" */) {
		this.charset = characterSet == null ? Charset.defaultCharset() : Charset.forName(characterSet);
		this.path = Paths.get(path);
		if (!this.path.isAbsolute()) {
			throw new IllegalArgumentException("supplied path must be absolute: " + path);
		}
	}

	@Override
	public List<FileSystemEntry> read(FilterData filterData) throws Exception {
		Path parent = resolvePath(filterData);

		DirectoryStream<Path> stream = Files.newDirectoryStream(parent);
		int index = 0;
		List<FileSystemEntry> regreso = new ArrayList<FileSystemEntry>(filterData.isConstrained()? filterData.getEnd()-filterData.getStart():10);
		
		for (Path file : stream) {
			if(index >= filterData.getStart() && index<filterData.getEnd()){
				regreso.add(createEntity(file));
			}
			index++;
		}
		
		stream.close();
		return regreso;
	}


	@Override
	public FileSystemEntry read(String targetEntryId) throws Exception {
		Path arg = resolvePath(targetEntryId);

		return createEntity(arg);
	}


	@Override
	public FileSystemEntry update(FileSystemEntry originalEntry, FileSystemEntry updatedEntry) throws Exception {
		// TODO https://docs.oracle.com/javase/tutorial/essential/io/file.html
		return originalEntry;
	}

	@Override
	public FileSystemEntry create(FileSystemEntry o) throws Exception {
		Path arg=resolvePath(o.getIdAsString());
		if(o.isDirectory()){
			Files.createDirectory(arg);
		}else{
			createContentFromString(o.getValue(),arg);
		}
		return createEntity(arg);
	}

	@Override
	public FileSystemEntry delete(FileSystemEntry o) throws Exception {
		String path = o.getIdAsString();
		Files.delete(Paths.get(path));
		return o;
	}

	@Override
	public void setDomain(Long domain) {
		this.domain=domain;
	}

	@Override
	public CatalogDescriptor getCatalog() {
		return catalog;
	}

	
	private FileSystemEntry createEntity(Path arg) throws IOException {
		String id = arg.toString();
		String name = arg.getFileName().toString();
		BasicFileAttributes attr = Files.readAttributes(arg, BasicFileAttributes.class);

		long size = attr.size();
		String mime = Files.probeContentType(arg);
		boolean directory = attr.isDirectory();
		Date creationTime = new Date(attr.creationTime().toMillis());
		boolean symbolicLink = attr.isSymbolicLink();
		Date lastAccessTime = new Date(attr.lastAccessTime().toMillis());
		Date lastModifiedTime = new Date(attr.lastModifiedTime().toMillis());
		String parent;
		if (arg.getNameCount() <= 1) {
			parent = null;
		} else {
			parent = arg.getParent().subpath(path.getNameCount(), arg.getNameCount() - 1).toString();
		}
		/*
		 * Charset charset = Charset.forName("US-ASCII"); try (BufferedReader
		 * reader = Files.newBufferedReader(file, charset)) { String line =
		 * null; while ((line = reader.readLine()) != null) {
		 * System.out.println(line); } } catch (IOException x) {
		 * System.err.format("IOException: %s%n", x); }
		 * 
		 * Path file = ...; try (InputStream in = Files.newInputStream(file);
		 * BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(in))) { String line = null; while ((line =
		 * reader.readLine()) != null) { System.out.println(line); } } catch
		 * (IOException x) { System.err.println(x); }
		 */
		return null;
	}
	
	private Path resolvePath(FilterData filterData) {
		FilterCriteria parentCriteria = filterData.fetchCriteria(ContentNode.PARENT_FIELD);
		if (parentCriteria == null) {
			return path;
		} else {
			String parentPath = (String) parentCriteria.getValue();
			return resolvePath(parentPath);
		}
	}

	private Path resolvePath(String parentPath) {
		Path parent = Paths.get(parentPath);
		if (parent.isAbsolute()) {
			return path;
		} else {
			return path.resolve(parent);
		}
	}

	private Iterable<Path> getFileSystemRoots() {
		return FileSystems.getDefault().getRootDirectories();
	}

	private void move(Path source, Path target) throws IOException {
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	private void createContentFromString( String content, Path file) throws IOException {
		/*
		 * try (BufferedWriter writer = Files.newBufferedWriter(file, charset))
		 * { writer.write(content, 0, content.length()); } catch (IOException x)
		 * { System.err.format("IOException: %s%n", x); }
		 */
		BufferedWriter writer = Files.newBufferedWriter(file, charset);
		writer.write(content, 0, content.length());
	}
}
