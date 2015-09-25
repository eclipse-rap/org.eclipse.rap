/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.remote.JsonMapping.toJson;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


public class ControlRemoteAdapter extends WidgetRemoteAdapter {

  private static final int PARENT = 11;
  private static final int BOUNDS = 12;
  private static final int CHILDREN = 13;
  private static final int TOOL_TIP_TEXT = 14;
  private static final int MENU = 15;
  private static final int FOREGROUND = 16;
  private static final int BACKGROUND = 17;
  private static final int FONT = 18;
  private static final int CURSOR = 19;
  private static final int VISIBLE = 20;
  private static final int ENABLED = 21;
  private static final int BACKGROUND_IMAGE = 22;
  private static final int ACTIVE_KEYS = 23;
  private static final int CANCEL_KEYS = 24;
  private static final int TAB_INDEX = 25;
  private static final int ORIENTATION = 26;

  private static final String PROP_BOUNDS = "bounds";

  private transient Composite parent;
  private transient Control[] children;
  private transient Rectangle bounds;
  private transient int tabIndex;
  private transient String toolTipText;
  private transient Menu menu;
  private transient boolean visible;
  private transient boolean enabled;
  private transient boolean rtl;
  private transient Color foreground;
  private transient Color background;
  private transient boolean backgroundTransparency;
  private transient Image backgroundImage;
  private transient Font font;
  private transient Cursor cursor;
  private transient String[] activeKeys;
  private transient String[] cancelKeys;

  public ControlRemoteAdapter( String id ) {
    super( id );
  }

  public void preserveParent( Composite parent ) {
    markPreserved( PARENT );
    this.parent = parent;
  }

  public boolean hasPreservedParent() {
    return hasPreserved( PARENT );
  }

  public Composite getPreservedParent() {
    return parent;
  }

  public void preserveChildren( Control[] children ) {
    markPreserved( CHILDREN );
    this.children = children;
  }

  public boolean hasPreservedChildren() {
    return hasPreserved( CHILDREN );
  }

  public Control[] getPreservedChildren() {
    return children;
  }

  public void preserveBounds( Rectangle bounds ) {
    if( !hasPreserved( BOUNDS ) ) {
      markPreserved( BOUNDS );
      this.bounds = bounds;
    }
  }

  public void renderBounds( IControlAdapter controlAdapter ) {
    if( !isInitialized() || hasPreserved( BOUNDS ) ) {
      Rectangle actual = controlAdapter.getBounds();
      if( changed( actual, bounds, null ) ) {
        getRemoteObject( getId() ).set( PROP_BOUNDS, toJson( actual ) );
      }
    }
  }

  public void preserveTabIndex( int tabIndex ) {
    markPreserved( TAB_INDEX );
    this.tabIndex = tabIndex;
  }

  public boolean hasPreservedTabIndex() {
    return hasPreserved( TAB_INDEX );
  }

  public int getPreservedTabIndex() {
    return tabIndex;
  }

  public void preserveToolTipText( String toolTipText ) {
    markPreserved( TOOL_TIP_TEXT );
    this.toolTipText = toolTipText;
  }

  public boolean hasPreservedToolTipText() {
    return hasPreserved( TOOL_TIP_TEXT );
  }

  public String getPreservedToolTipText() {
    return toolTipText;
  }

  public void preserveMenu( Menu menu ) {
    markPreserved( MENU );
    this.menu = menu;
  }

  public boolean hasPreservedMenu() {
    return hasPreserved( MENU );
  }

  public Menu getPreservedMenu() {
    return menu;
  }

  public void preserveVisible( boolean visible ) {
    markPreserved( VISIBLE );
    this.visible = visible;
  }

  public boolean hasPreservedVisible() {
    return hasPreserved( VISIBLE );
  }

  public boolean getPreservedVisible() {
    return visible;
  }

  public void preserveEnabled( boolean enabled ) {
    markPreserved( ENABLED );
    this.enabled = enabled;
  }

  public boolean hasPreservedEnabled() {
    return hasPreserved( ENABLED );
  }

  public boolean getPreservedEnabled() {
    return enabled;
  }

  public void preserveOrientation( int orientation ) {
    markPreserved( ORIENTATION );
    this.rtl = orientation == SWT.RIGHT_TO_LEFT;
  }

  public boolean hasPreservedOrientation() {
    return hasPreserved( ORIENTATION );
  }

  public int getPreservedOrientation() {
    return rtl ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT;
  }

  public void preserveForeground( Color foreground ) {
    markPreserved( FOREGROUND );
    this.foreground = foreground;
  }

  public boolean hasPreservedForeground() {
    return hasPreserved( FOREGROUND );
  }

  public Color getPreservedForeground() {
    return foreground;
  }

  public void preserveBackground( Color background ) {
    markPreserved( BACKGROUND );
    this.background = background;
  }

  public boolean hasPreservedBackground() {
    return hasPreserved( BACKGROUND );
  }

  public Color getPreservedBackground() {
    return background;
  }

  public void preserveBackgroundTransparency( boolean transparency ) {
    markPreserved( BACKGROUND );
    backgroundTransparency = transparency;
  }

  public boolean getPreservedBackgroundTransparency() {
    return backgroundTransparency;
  }

  public void preserveBackgroundImage( Image backgroundImage ) {
    markPreserved( BACKGROUND_IMAGE );
    this.backgroundImage = backgroundImage;
  }

  public boolean hasPreservedBackgroundImage() {
    return hasPreserved( BACKGROUND_IMAGE );
  }

  public Image getPreservedBackgroundImage() {
    return backgroundImage;
  }

  public void preserveFont( Font font ) {
    markPreserved( FONT );
    this.font = font;
  }

  public boolean hasPreservedFont() {
    return hasPreserved( FONT );
  }

  public Font getPreservedFont() {
    return font;
  }

  public void preserveCursor( Cursor cursor ) {
    markPreserved( CURSOR );
    this.cursor = cursor;
  }

  public boolean hasPreservedCursor() {
    return hasPreserved( CURSOR );
  }

  public Cursor getPreservedCursor() {
    return cursor;
  }

  public void preserveActiveKeys( String[] activeKeys ) {
    markPreserved( ACTIVE_KEYS );
    this.activeKeys = activeKeys;
  }

  public boolean hasPreservedActiveKeys() {
    return hasPreserved( ACTIVE_KEYS );
  }

  public String[] getPreservedActiveKeys() {
    return activeKeys;
  }

  public void preserveCancelKeys( String[] cancelKeys ) {
    markPreserved( CANCEL_KEYS );
    this.cancelKeys = cancelKeys;
  }

  public boolean hasPreservedCancelKeys() {
    return hasPreserved( CANCEL_KEYS );
  }

  public String[] getPreservedCancelKeys() {
    return cancelKeys;
  }

  @Override
  public void clearPreserved() {
    super.clearPreserved();
    parent = null;
    children = null;
    bounds = null;
    tabIndex = 0;
    toolTipText = null;
    menu = null;
    visible = false;
    enabled = false;
    rtl = false;
    foreground = null;
    background = null;
    backgroundTransparency = false;
    backgroundImage = null;
    font = null;
    cursor = null;
    activeKeys = null;
    cancelKeys = null;
  }

  private Object readResolve() {
    initialize();
    return this;
  }

  private boolean changed( Object actualValue, Object preservedValue, Object defaultValue ) {
    return !equals( actualValue, isInitialized() ? preservedValue : defaultValue );
  }

  private static boolean equals( Object o1, Object o2 ) {
    return o1 == o2 || o1 != null && o1.equals( o2 );
  }

}
