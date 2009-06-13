/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.resources;



/**
 * This interface describes a factory that provides instances of 
 * {@link IResourceManager}.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 * @see IResourceManager
 */
public interface IResourceManagerFactory {

  IResourceManager create();
}
