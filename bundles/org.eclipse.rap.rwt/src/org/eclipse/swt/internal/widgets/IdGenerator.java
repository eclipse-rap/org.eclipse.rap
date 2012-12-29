/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;


/**
 * An instance of this interface is used to generate IDs for objects that are used in the remote
 * protocol. An implementation must ensure that the generated IDs are unique, i.e. no ID must ever
 * be created twice.
 * <p>
 * The framework will call <code>createId(Object)</code> once for every object before it creates the
 * corresponding remote object.
 * </p>
 * <p>
 * A custom ID generator may be registered for the purpose of UI testing by setting the system
 * property <em>org.eclipse.rap.idGenerator</em> to the class name of the IdGenerator
 * implementation. The class will be load with the same class loader as RWT, hence a fragment must
 * be used in OSGi.
 * </p>
 *
 * @since 2.0
 */
public interface IdGenerator {

  String createId( Object object );

}
