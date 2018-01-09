/*******************************************************************************
 * Copyright (c) 2012, 2018 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial API and implementation
 *    EclipseSource - ongoing development
 *******************************************************************************/
package org.eclipse.rap.e4;

public class E4ApplicationConfig {

	private String xmiURI;
	private boolean persistState;
	private boolean clearPersistedState;
	private String lifecycleURI;
	private String presentationURI;
	private String modelResourceHandlerURI;
	private boolean defaultPush;

	public E4ApplicationConfig(String xmiURI,
			                   String lifecycleURI,
			                   String presentationURI,
			                   String modelResourceHandlerURI,
			                   boolean persistState,
			                   boolean clearPersistedState,
			                   boolean defaultPush)
	{
		this.xmiURI = xmiURI;
		this.lifecycleURI = lifecycleURI;
		this.presentationURI = presentationURI;
		this.modelResourceHandlerURI = modelResourceHandlerURI;
		this.persistState = persistState;
		this.clearPersistedState = clearPersistedState;
		this.defaultPush = defaultPush;
	}

	public String getXmiURI() {
		return xmiURI;
	}

	public String getLifecycleURI() {
		return lifecycleURI;
	}

	public String getPresentationURI() {
		return presentationURI;
	}

	public String getModelResourceHandlerURI() {
		return modelResourceHandlerURI;
	}

	public boolean isPersistState() {
		return persistState;
	}

	public boolean isClearPersistedState() {
		return clearPersistedState;
	}

	public boolean isDefaultPush() {
		return defaultPush;
	}

	public static E4ApplicationConfig create(String xmiURI) {
		return new E4ApplicationConfig(xmiURI, null, null, null, true, false, true);
	}

	public static E4ApplicationConfig create(String xmiURI, String lifecycleURI) {
		return new E4ApplicationConfig(xmiURI, lifecycleURI, null, null, true, false, true);
	}

	public static E4ApplicationConfig create(String xmiURI, boolean defaultPush) {
		return new E4ApplicationConfig(xmiURI, null, null, null, true, false, true);
	}

	public static E4ApplicationConfig create(String xmiURI, String lifecycleURI, boolean defaultPush) {
		return new E4ApplicationConfig(xmiURI, lifecycleURI, null, null, true, false, true);
	}

}
