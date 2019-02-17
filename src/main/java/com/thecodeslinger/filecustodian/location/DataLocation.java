package com.thecodeslinger.filecustodian.location;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores the source and target location of a backup and provides convenient methods to
 * easily get an absolute path of a file, e.g. to generate a target filename for a new
 * found file that shall be archived.
 * 
 * @author Robert Lohr
 * @since 2019-02-16
 */
public class DataLocation {
	private final Path source;
	private final Path destination;
	
	/**
	 * Create a new {@code DataLocation} with a source and destination path.
	 * 
	 * @param source
	 * 		Where data shall be read from.
	 * 
	 * @param destination
	 * 		Where data shall be stored to.
	 */
	public DataLocation(Path source, Path destination) {
		assert null != source && null != destination : "source and destination must not be null";
		this.source = source;
		this.destination = destination;
	}
	
	/**
	 * Create a new {@code DataLocation} with a source and destination path string.
	 * 
	 * @param source
	 * 		Where data shall be read from.
	 * 
	 * @param destination
	 * 		Where data shall be stored to.
	 */
	public DataLocation(String source, String destination) {
		this(Paths.get(source), Paths.get(destination));
	}
	
	/**
	 * Calculate a path in the destination folder for the given source file.
	 * 
	 * @param sourceFile
	 * 		A file that is located somewhere in {@link DataLocation#source}.
	 * 
	 * @return
	 * 		Returns an absolute path that puts {@code sourceFile} in the destination
	 * 		folder at the same relative location as it was in {@link DataLocation#source}.
	 * 
	 * @throws IllegalArgumentException
	 * 		{@code sourceFile} is not in a subfolder of {@link DataLocation#source}.
	 */
	Path getDestinationPath(Path sourceFile) {
		if (!sourceFile.startsWith(source)) {
			var message = String.format(
					"File %s is not in source directory %s", 
					sourceFile, source);
			throw new IllegalArgumentException(message);
		}
		
		Path relativeSource = source.relativize(sourceFile);
		return destination.resolve(relativeSource);
	}
}
