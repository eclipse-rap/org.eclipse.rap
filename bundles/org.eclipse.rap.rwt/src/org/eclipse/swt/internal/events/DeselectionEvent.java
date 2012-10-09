/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/*
 * This class was introduced to distinguish between selection and de-selection
 * events. This is a fix for bug 273769: Radio selection events work wrong
 * (https://bugs.eclipse.org/bugs/show_bug.cgi?id=273769)
 * See also event order in TypedEvent.
 */
public final class DeselectionEvent extends SelectionEvent {

  public static final int WIDGET_DESELECTED = EventTypes.WIDGET_DESELECTED;

  public DeselectionEvent( Widget widget ) {
    super( widget, null, WIDGET_DESELECTED, emptyRectangle(), 0, null, true, SWT.NONE );
  }

  public DeselectionEvent( Event e ) {
    super( e );
  }

  private static Rectangle emptyRectangle() {
    return new Rectangle( 0, 0, 0, 0 );
  }
}
