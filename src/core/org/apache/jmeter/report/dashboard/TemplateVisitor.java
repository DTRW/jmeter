/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter.report.dashboard;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jmeter.report.core.DataContext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The class TemplateVisitor visits files in a template directory to copy
 * regular files and process templated ones.
 *
 * @since 2.14
 */
public class TemplateVisitor extends SimpleFileVisitor<Path> {

    public static final String TEMPLATED_FILE_EXT = "fmkr";

    private final Path source;
    private final Path target;
    private final Configuration configuration;
    private final DataContext data;

    /**
     * Instantiates a new template visitor.
     *
     * @param source
     *            the source directory
     * @param target
     *            the target directory
     * @param configuration
     *            the freemarker configuration
     * @param data
     *            the data to inject
     */
    public TemplateVisitor(Path source, Path target,
	    Configuration configuration, DataContext data) {
	this.source = source;
	this.target = target;
	this.configuration = configuration;
	this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object,
     * java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
	    throws IOException {
	// Copy directory
	Path newDir = target.resolve(source.relativize(arg0));
	try {
	    Files.copy(arg0, newDir);
	} catch (FileAlreadyExistsException ex) {
	    // Set directory empty
	    FileUtils.cleanDirectory(newDir.toFile());
	}
	return FileVisitResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object,
     * java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1)
	    throws IOException {

	// Depending on file extension, copy or process file
	String extension = FilenameUtils.getExtension(arg0.toString());
	if (TEMPLATED_FILE_EXT.equalsIgnoreCase(extension)) {
	    // Process template file
	    String templatePath = source.relativize(arg0).toString();
	    Template template = configuration.getTemplate(templatePath);
	    Path newPath = target.resolve(FilenameUtils
		    .removeExtension(templatePath));
	    Writer file = new FileWriter(newPath.toString());
	    try {
		template.process(data, file);
	    } catch (TemplateException ex) {
		throw new IOException(ex);
	    } finally {
		file.close();
	    }

	} else {
	    // Copy regular file
	    Path newFile = target.resolve(source.relativize(arg0));
	    Files.copy(arg0, newFile, StandardCopyOption.REPLACE_EXISTING);
	}
	return FileVisitResult.CONTINUE;
    }
}
