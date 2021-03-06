/*******************************************************************************
 * Copyright (c) 2017, 2018 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.java.requestmapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ide.vscode.boot.java.handlers.RunningAppProvider;
import org.springframework.ide.vscode.commons.boot.app.cli.SpringBootApp;
import org.springframework.ide.vscode.commons.boot.app.cli.requestmappings.RequestMapping;

/**
 * @author Martin Lippert
 */
public class LiveAppURLSymbolProvider {

	private static final Logger log = LoggerFactory.getLogger(LiveAppURLSymbolProvider.class);

	private final RunningAppProvider runningAppProvider;

	public LiveAppURLSymbolProvider(RunningAppProvider runningAppProvider) {
		this.runningAppProvider = runningAppProvider;
	}

	public List<? extends SymbolInformation> getSymbols(String query) {
		List<SymbolInformation> result = new ArrayList<>();

		try {
			SpringBootApp[] runningApps = runningAppProvider.getAllRunningSpringApps().toArray(new SpringBootApp[0]);
			for (SpringBootApp app : runningApps) {
				try {
					String urlScheme = app.getUrlScheme();
					String host = app.getHost();
					String port = app.getPort();
					String contextPath = app.getContextPath();
					for (RequestMapping rm : app.getRequestMappings()) {
						String[] paths = rm.getSplitPath();
						if (paths==null || paths.length==0) {
							//Technically, this means the path 'predicate' is unconstrained, meaning any path matches.
							//So this is not quite the same as the case where path=""... but...
							//It is better for us to show one link where any path is allowed, versus showing no links where any link is allowed.
							//So we'll pretend this is the same as path="" as that gives a working link.
							paths = new String[] {""};
						}
						for (String path : paths) {
							String url = UrlUtil.createUrl(urlScheme, host, port, path, contextPath);
							result.add(new SymbolInformation(url, SymbolKind.Method, new Location(url, new Range(new Position(0, 0), new Position(0, 1)))));
						}
					}
				}
				catch (Exception e) {
					log.error("", e);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return result;
	}

}
