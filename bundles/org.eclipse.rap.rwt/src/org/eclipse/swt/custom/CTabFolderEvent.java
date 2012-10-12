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
package org.eclipse.swt.custom;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * This event is sent when an event is generated in the <code>CTabFolder</code>.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @since 1.0
 */
public class CTabFolderEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int CLOSE = EventTypes.CTAB_FOLDER_CLOSE;
  public static final int MINIMIZE = EventTypes.CTAB_FOLDER_MINIMIZE;
  public static final int MAXIMIZE = EventTypes.CTAB_FOLDER_MAXIMIZE;
  public static final int RESTORE = EventTypes.CTAB_FOLDER_RESTORE;
  public static final int SHOW_LIST = EventTypes.CTAB_FOLDER_SHOW_LIST;

  private static final int[] EVENT_TYPES = { CLOSE, MINIMIZE, MAXIMIZE, RESTORE, SHOW_LIST };

  /**
   * The tab item for the operation.
   */
  public Widget item;

  /**
   * A flag indicating whether the operation should be allowed.
   * Setting this field to <code>false</code> will cancel the operation.
   * Applies to the close and showList events.
   */
  public boolean doit;

  /**
   * The widget-relative, x coordinate of the chevron button
   * at the time of the event.  Applies to the showList event.
   */
  public int x;

  /**
   * The widget-relative, y coordinate of the chevron button
   * at the time of the event.  Applies to the showList event.
   */
  public int y;

  /**
   * The width of the chevron button at the time of the event.
   * Applies to the showList event.
   */
  public int width;

  /**
   * The height of the chevron button at the time of the event.
   * Applies to the showList event.
   */
  public int height;

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public CTabFolderEvent( Event event ) {
    super( event );
    x = event.x;
    y = event.y;
    width = event.width;
    height = event.height;
    item = event.item;
    doit = event.doit;
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
  public static void addListener( Adaptable adaptable, CTabFolder2Listener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, CTabFolder2Listener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
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
