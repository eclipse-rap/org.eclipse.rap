/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of expand item being expanded
 * and collapsed.
 *
 * @see ExpandListener
 * @since 1.2
 */
public class ExpandEvent extends SelectionEvent {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new instance of this class based on the information in the
   * given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public ExpandEvent( Event event ) {
    super( event );
  }

  @Override
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " item="
           + item
           + " doit="
           + doit
           + " x="
           + x
           + " y="
           + y
           + " width="
           + width
           + " height="
           + height
           + "}";
  }
}
