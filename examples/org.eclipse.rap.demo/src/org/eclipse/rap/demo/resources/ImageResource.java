/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.resources;

import org.eclipse.rap.ui.resources.IResource;


/*
 * Ununsed resource for testing resource extensions.
 */
public class ImageResource implements IResource {

  public ImageResource() {
  }

  public ClassLoader getLoader() {
    return ImageResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/rap/demo/resources/eclipse.svg";
  }

  public boolean isJSLibrary() {
    return false;
  }

  public boolean isExternal() {
    return false;
  }

}
