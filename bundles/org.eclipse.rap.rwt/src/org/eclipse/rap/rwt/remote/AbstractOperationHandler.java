/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.remote;

import java.io.Serializable;
import java.util.Map;


/**
 * This class provides an empty implementation of the <code>OperationHandler</code> interface, to
 * minimize the effort required to implement this interface.
 * <p>
 * Subclasses only need to override those methods that are needed to handle the expected operations
 * for the corresponding remote type. Methods that are not overridden will throw an
 * {@link UnsupportedOperationException}.
 * </p>
 * <p>
 * It is recommended to extend this base class rather than to implement the OperationHandler
 * interface itself.
 * </p>
 * <p>
 * <strong>Note:</strong> The classes and interfaces in the package
 * <em>org.eclipse.rap.rwt.remote</em> are still considered <strong>provisional</strong>. They are
 * expected to evolve over the next releases, which may lead to slight changes. We make the package
 * available to enable the development of custom components with the new API.
 * </p>
 *
 * @since 2.0
 */
public abstract class AbstractOperationHandler implements OperationHandler, Serializable {

  public void handleSet( Map<String, Object> properties ) {
    throw new UnsupportedOperationException( "set operations not supported by this handler" );
  }

  public void handleCall( String method, Map<String, Object> parameters ) {
    throw new UnsupportedOperationException( "call operations not supported by this handler" );
  }

  public void handleNotify( String event, Map<String, Object> properties ) {
    throw new UnsupportedOperationException( "notify operations not supported by this handler" );
  }

}
