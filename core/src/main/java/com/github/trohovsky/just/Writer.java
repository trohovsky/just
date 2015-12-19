/*
 * Copyright 2014 Tomas Rohovsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.trohovsky.just;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.github.trohovsky.just.util.Validation;

/**
 * Writer of classes.
 * 
 * @author Tomas Rohovsky
 */
public class Writer {

	private String[] sourcePaths;
	private String[] includes;
	private String[] excludes;

	private Writer(String... sourcePaths) {
		this.sourcePaths = sourcePaths;
	}

	/**
	 * Creates a Writer that reads from the specified paths that could refer to directories or JARs.
	 * 
	 * @param paths
	 *            the paths referring to the directories or JARs
	 * @return the same instance of Reader
	 */
	public static Writer from(String... sourcePaths) {
		Validation.noNullValues(sourcePaths, "Paths cannot contain null values");
		return new Writer(sourcePaths);
	}

	/**
	 * Sets including prefixes. Files matched by the prefixes will be included for writing. All files are included by
	 * default.
	 * 
	 * @param includes
	 *            the including prefixes
	 * @return the same instance of Writer
	 */
	public Writer includes(String... includes) {
		this.includes = includes;
		return this;
	}

	/**
	 * Sets excluding prefixes. Files matched by the prefixes will be excluded from writing. No files are excluded by
	 * default.
	 * 
	 * @param excludes
	 *            the excluding prefixes
	 * @return the same instance of Writer
	 */
	public Writer excludes(String... excludes) {
		this.excludes = excludes;
		return this;
	}

	/**
	 * Copies one or more directories/JARs filtered by includes/excludes to a directory/JARs. The destination directory
	 * is created if it does not exist. These combinations are supported:
	 * <ul>
	 * <li>N srcDirs -> 1 dstDir (merges srcDirs to dstDir)</li>
	 * <li>N srcDirs -> N dstDirs</li>
	 * <li>N srcJars -> 1 dstDir</li>
	 * <li>N srcJars -> N dstJars</li>
	 * </ul>
	 * 
	 * @param destPaths
	 *            the destination paths
	 * @throws IOException
	 */
	public void copyTo(String... destPaths) throws IOException {
		Validation.noNullValues(destPaths, "Paths cannot contain null values");
		if (destPaths.length != sourcePaths.length && destPaths.length != 1) {
			throw new IllegalArgumentException(
					"The number of source paths has to match the number of destination paths");
		}

		int destPathIndex = 0;
		File destFile = null;
		if (destPaths.length == 1) {
			destFile = new File(destPaths[0]);
		}
		for (String sourcePath : sourcePaths) {
			File sourceFile = new File(sourcePath);
			if (destPaths.length > 1) {
				destFile = new File(destPaths[destPathIndex]);
				destPathIndex++;
			}
			if (sourceFile.isDirectory()) {
				copyDirectory(sourceFile, destFile);
			} else {
				copyJar(sourceFile, destFile);
			}
		}
	}

	private void copyDirectory(File srcDir, File destDir) throws IOException {
		// check if source directory exists
		if (!srcDir.exists()) {
			throw new IllegalArgumentException("Source directory " + srcDir.getPath() + " does not exist");
		}
		// create parent directories if do not exist
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		copyDirectoryInternal(srcDir, "", destDir);
	}

	private void copyDirectoryInternal(File srcDir, String path, File destDir) throws IOException {
		if (srcDir.isDirectory()) {
			if (!destDir.exists()) {
				destDir.mkdir();
			}
			for (String fileName : srcDir.list()) {
				File sourceFile = new File(srcDir, fileName);
				File destFile = new File(destDir, fileName);
				copyDirectoryInternal(sourceFile, path + fileName + File.separatorChar, destFile);
			}
		} else {
			if ((includes == null || matches(path, includes)) && (excludes == null || !matches(path, excludes))) {
				copyFile(srcDir, destDir);
			}
		}
	}

	private void copyFile(File srcFile, File destFile) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	private void copyJar(File srcFile, File destFile) throws IOException {
		// check if source JAR exists
		if (!srcFile.exists()) {
			throw new IllegalArgumentException("Source directory " + srcFile.getPath() + " does not exist");
		}
		// set destination JAR file
		File destJarFile = null;
		if (destFile.getName().endsWith(".jar")) {
			destJarFile = destFile;
		} else {
			destJarFile = new File(destFile, srcFile.getName());
		}
		// create parent directories if do not exist
		File destDirFile = destJarFile.getParentFile();
		if (!destDirFile.exists()) {
			destDirFile.mkdirs();
		}

		if (includes != null || excludes != null) {
			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			FileInputStream fis = null;
			ZipInputStream zis = null;
			try {
				fis = new FileInputStream(srcFile);
				fos = new FileOutputStream(destJarFile);
				zis = new ZipInputStream(fis);
				zos = new ZipOutputStream(fos);
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if ((includes == null || matches(entry.getName(), includes))
							&& (excludes == null || !matches(entry.getName(), excludes))) {
						final ZipEntry newEntry = new ZipEntry(entry);
						zos.putNextEntry(newEntry);
						byte[] buffer = new byte[1024];
						int length;
						while ((length = zis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
						zis.closeEntry();
						zos.closeEntry();
					}
				}
			} finally {
				try {
					if (zis != null) {
						zis.close();
					} else if (fis != null) {
						fis.close();
					}
				} finally {
					if (zos != null) {
						zos.close();
					} else if (fos != null) {
						fos.close();
					}
				}
			}
		} else {
			copyFile(srcFile, destJarFile);
		}
	}

	private static boolean matches(String string, String[] patterns) {
		if (patterns == null) {
			return false;
		}
		for (String pattern : patterns) {
			if (string.startsWith(pattern)) {
				return true;
			}
		}
		return false;
	}
}
