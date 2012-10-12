/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Event;


/**
 * A <code>LocationEvent</code> is sent by a {@link Browser} to
 * {@link LocationListener}'s when the <code>Browser</code>
 * navigates to a different URL. This notification typically
 * occurs when the application navigates to a new location with
 * {@link Browser#setUrl(String)} or when the user activates a
 * hyperlink.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @since 1.0
 */
public class LocationEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int CHANGING = EventTypes.LOCALTION_CHANGING;
  public static final int CHANGED = EventTypes.LOCALTION_CHANGED;

  private static final int[] EVENT_TYPES = { CHANGING, CHANGED };

  /** current location */
  public String location;

  /**
   * A flag indicating whether the location opens in the top frame
   * or not.
   */
  public boolean top;

  /**
   * A flag indicating whether the location loading should be allowed.
   * Setting this field to <code>false</code> will cancel the operation.
   */
  public boolean doit = true;

  
  LocationEvent( Event event ) {
    super( event );
    location = event.text;
    top = event.detail == SWT.TOP;
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, EVENT_TYPES );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void addListener( Adaptable adaptable, LocationListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, LocationListener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
  }

}
