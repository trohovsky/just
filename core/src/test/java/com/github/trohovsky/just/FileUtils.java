package com.github.trohovsky.just;

import java.io.File;

import com.github.trohovsky.just.util.Validation;

public final class FileUtils {

	/**
	 * Deletes a file. If file is a directory, delete it and all sub-directories.
	 * 
	 * @param file
	 *            file to file or directory to delete, must not be null
	 */
	public static void delete(File file) {
		Validation.notNull(file, "The file cannot be null");
		if (!file.exists()) {
			return;
		}
		deleteInternal(file);
	}

	/**
	 * Deletes a file. If file is a directory, delete it and all sub-directories.
	 * 
	 * @param path
	 *            path to file or directory to delete, must not be null
	 */
	public static void delete(String path) {
		Validation.notNull(path, "The file path cannot be null");
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		deleteInternal(file);
	}

	private static void deleteInternal(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			for (File f : file.listFiles()) {
				if (f.isFile()) {
					f.delete();
				} else {
					deleteInternal(f);
				}
			}
		}
	}
}
