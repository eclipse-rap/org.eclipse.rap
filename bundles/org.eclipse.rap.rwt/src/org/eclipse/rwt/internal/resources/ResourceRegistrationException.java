/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

/**
 * <p>Thrown when the registration of a resource fails.</p>
 * @see org.eclipse.rwt.resources.IResourceManager
 */
public class ResourceRegistrationException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public ResourceRegistrationException( String message, Throwable cause ) {
    super( message, cause );
  }
}
