/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import org.eclipse.swt.internal.SerializableCompatibility;



/**
 * <code>UISessionListener</code>s can be attached to a UI session to receive a notification before
 * the UI session is destroyed.
 *
 * @since 2.0
 */
public interface UISessionListener extends SerializableCompatibility {

  /**
   * Called <em>before</em> the related UI session is destroyed.
   *
   * @param event an event that provides access to the UI session
   */
  void beforeDestroy( UISessionEvent event );

}
