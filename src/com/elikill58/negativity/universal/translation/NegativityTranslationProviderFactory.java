package com.elikill58.negativity.universal.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PropertyResourceBundle;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;

public class NegativityTranslationProviderFactory implements TranslationProviderFactory {

	private static final String FALLBACK_LANGUAGE = "en_US";

	private final Path messagesDir;
	private final String[] bundles;

	public NegativityTranslationProviderFactory(Path messagesDir, String... bundles) {
		this.messagesDir = messagesDir;
		this.bundles = bundles;
	}

	@Nullable
	@Override
	public TranslationProvider createTranslationProvider(String language) {
		Adapter adapter = Adapter.getAdapter();

		StringBuilder concatenatedBundles = new StringBuilder(8000);
		for (String bundle : this.bundles) {
			String fileName = bundle + "_" + language + ".properties";
			try {
				Path file = adapter.copyBundledFile(fileName, messagesDir.resolve(fileName));
				if (Files.notExists(file)) {
					continue;
				}

				for (String line : Files.readAllLines(file)) {
					concatenatedBundles.append(line);
					// Make sure we have a new line between concatenated files
					concatenatedBundles.append(System.lineSeparator());
				}
			} catch (IOException e) {
				adapter.error("Failed to read language file " + fileName);
				e.printStackTrace();
			}
		}

		try (StringReader reader = new StringReader(concatenatedBundles.toString())) {
			PropertyResourceBundle bundle = new PropertyResourceBundle(reader);
			return new CachingTranslationProvider(new ResourceBundleTranslationProvider(bundle));
		} catch (IOException e) {
			adapter.error("Failed to load translation file.");
			e.printStackTrace();
			return null;
		}
	}

	@Nullable
	@Override
	public TranslationProvider createFallbackTranslationProvider() {
		Adapter adapter = Adapter.getAdapter();
		StringBuilder concatenatedBundles = new StringBuilder(8000);
		for (String bundle : this.bundles) {
			String fileName = bundle + "_" + FALLBACK_LANGUAGE + ".properties";
			InputStream input = getClass().getResourceAsStream("/assets/negativity/" + fileName);
			if (input == null) {
				adapter.error("Fallback language file " + fileName + " does not exist.");
				continue;
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					concatenatedBundles.append(line);
					// Make sure we have a new line between concatenated files
					concatenatedBundles.append(System.lineSeparator());
				}
			} catch (IOException e) {
				adapter.error("Failed to read fallback message file " + bundle);
				e.printStackTrace();
			}
		}

		try (StringReader reader = new StringReader(concatenatedBundles.toString())) {
			PropertyResourceBundle bundle = new PropertyResourceBundle(reader);
			return new CachingTranslationProvider(new ResourceBundleTranslationProvider(bundle));
		} catch (IOException e) {
			adapter.error("Failed to load fallback translation resource.");
			e.printStackTrace();
			return null;
		}
	}
}
