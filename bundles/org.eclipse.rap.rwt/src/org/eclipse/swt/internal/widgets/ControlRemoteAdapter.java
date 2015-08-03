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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;


public class ControlRemoteAdapter extends WidgetRemoteAdapter {

  private static final int PARENT = 1;
  private static final int BOUNDS = 2;
  private static final int CHILDREN = 3;
  private static final int TOOL_TIP_TEXT = 4;
  private static final int MENU = 5;
  private static final int FOREGROUND = 6;
  private static final int BACKGROUND = 7;
  private static final int FONT = 8;
  private static final int CURSOR = 9;

  private transient int preserved;
  private transient Composite parent;
  private transient Control[] children;
  private transient Rectangle bounds;
  private transient int tabIndex;
  private transient String toolTipText;
  private transient Menu menu;
  private transient boolean visible;
  private transient boolean enabled;
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
    markPreserved( BOUNDS );
    this.bounds = bounds;
  }

  public boolean hasPreservedBounds() {
    return hasPreserved( BOUNDS );
  }

  public Rectangle getPreservedBounds() {
    return bounds;
  }

  public void preserveTabIndex( int tabIndex ) {
    this.tabIndex = tabIndex;
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
    this.visible = visible;
  }

  public boolean getPreservedVisible() {
    return visible;
  }

  public void preserveEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  public boolean getPreservedEnabled() {
    return enabled;
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
    this.backgroundImage = backgroundImage;
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
    this.activeKeys = activeKeys;
  }

  public String[] getPreservedActiveKeys() {
    return activeKeys;
  }

  public void preserveCancelKeys( String[] cancelKeys ) {
    this.cancelKeys = cancelKeys;
  }

  public String[] getPreservedCancelKeys() {
    return cancelKeys;
  }

  @Override
  public void clearPreserved() {
    super.clearPreserved();
    preserved = 0;
    parent = null;
    children = null;
    bounds = null;
    tabIndex = 0;
    toolTipText = null;
    menu = null;
    visible = false;
    enabled = false;
    foreground = null;
    background = null;
    backgroundTransparency = false;
    backgroundImage = null;
    font = null;
    cursor = null;
    activeKeys = null;
    cancelKeys = null;
  }

  private void markPreserved( int index ) {
    preserved |= ( 1 << index );
  }

  private boolean hasPreserved( int index ) {
    return ( preserved & ( 1 << index ) ) != 0;
  }

  private Object readResolve() {
    initialize();
    return this;
  }

}
