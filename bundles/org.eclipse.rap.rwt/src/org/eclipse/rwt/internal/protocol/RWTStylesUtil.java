/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution", "and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.*;


public final class RWTStylesUtil {

  private enum AllowedStyles {

    // TODO[hs]: We need to apply our code format to this list after it's completed.
    BUTTON( Button.class.getName(), new String[] { "CHECK", "PUSH", "RADIO", "TOGGLE", "FLAT", "WRAP", "LEFT", "RIGHT", "CENTER", "BORDER", "LEFT_TO_RIGHT" } ),
    COLOR_DIALOG( ColorDialog.class.getName(), new String[] { "APPLICATION_MODAL", "PRIMARY_MODAL", "SYSTEM_MODAL" } ),
    COMBO( Combo.class.getName(), new String[] { "DROP_DOWN", "READ_ONLY", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    CCOMBO( CCombo.class.getName(), new String[] { "FLAT", "READ_ONLY", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    COMPOSITE( Composite.class.getName(), new String[] { "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    CONTROL( Control.class.getName(), new String[] { "BORDER", "LEFT_TO_RIGHT" } ),
    COOL_BAR( CoolBar.class.getName(), new String[] { "FLAT", "HORIZONTAL", "VERTICAL", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    COOL_ITEM( CoolItem.class.getName(), new String[] { "DROP_DOWN" } ),
    DATE_TIME( DateTime.class.getName(), new String[] { "DATE", "TIME", "CALENDAR", "SHORT", "MEDIUM", "LONG", "DROP_DOWN", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    DECORATIONS( Decorations.class.getName(), new String[] { "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    DIALOG( Dialog.class.getName(), new String[] { "APPLICATION_MODAL", "PRIMARY_MODAL", "SYSTEM_MODAL" } ),
    EXPAND_BAR( ExpandBar.class.getName(), new String[] { "V_SCROLL", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    FONT_DIALOG( FontDialog.class.getName(), new String[] { "APPLICATION_MODAL", "PRIMARY_MODAL", "SYSTEM_MODAL" } ),
    GROUP( Group.class.getName(), new String[] { "SHADOW_ETCHED_IN", "SHADOW_ETCHED_OUT", "SHADOW_IN", "SHADOW_OUT", "SHADOW_NONE", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    LABEL( Label.class.getName(), new String[] { "SEPARATOR", "HORIZONTAL", "VERTICAL", "SHADOW_IN", "SHADOW_OUT", "SHADOW_NONE", "CENTER", "LEFT", "RIGHT", "WRAP", "BORDER", "LEFT_TO_RIGHT" } ),
    CLABEL( CLabel.class.getName(), new String[] { "SHADOW_IN", "SHADOW_OUT", "SHADOW_NONE", "CENTER", "LEFT", "RIGHT", "BORDER", "LEFT_TO_RIGHT" } ),
    LINK( Link.class.getName(), new String[] { "BORDER", "LEFT_TO_RIGHT" } ),
    LIST( List.class.getName(), new String[] { "SINGLE", "MULTI", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    MENU( Menu.class.getName(), new String[] { "BAR", "DROP_DOWN", "POP_UP", "NO_RADIO_GROUP", "LEFT_TO_RIGHT" } ),
    MENU_ITEM( MenuItem.class.getName(), new String[] { "CHECK", "CASCADE", "PUSH", "RADIO", "SEPARATOR" } ),
    MESSAGE_BOX( MessageBox.class.getName(), new String[] { "ICON_ERROR", "ICON_INFORMATION", "ICON_QUESTION", "ICON_WARNING", "ICON_WORKING", "OK", "CANCEL", "NO", "RETRY", "YES", "ABORT", "IGNORE", "APPLICATION_MODAL", "PRIMARY_MODAL", "SYSTEM_MODAL" } ),
    PROGRESS_BAR( ProgressBar.class.getName(), new String[] { "SMOOTH", "HORIZONTAL", "VERTICAL", "INDETERMINATE", "BORDER", "LEFT_TO_RIGHT" } ),
    SASH( Sash.class.getName(), new String[] { "HORIZONTAL", "VERTICAL", "SMOOTH", "BORDER", "LEFT_TO_RIGHT" } ),
    SCALE( Scale.class.getName(), new String[] { "HORIZONTAL", "VERTICAL", "BORDER", "LEFT_TO_RIGHT" } ),
    SCROLLABLE( Scrollable.class.getName(), new String[] { "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    SCROLL_BAR( ScrollBar.class.getName(), new String[] { "HORIZONTAL", "VERTICAL" } ),
    SHELL( Shell.class.getName(), new String[] { "BORDER", "CLOSE", "MIN", "MAX", "NO_TRIM", "RESIZE", "TITLE", "ON_TOP", "TOOL", "SHEET", "APPLICATION_MODAL", "MODELESS", "PRIMARY_MODAL", "SYSTEM_MODAL" } ),
    SLIDER( Slider.class.getName(), new String[] { "HORIZONTAL", "VERTICAL", "BORDER", "LEFT_TO_RIGHT" } ),
    SPINNER( Spinner.class.getName(), new String[] { "READ_ONLY", "WRAP", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    TAB_FOLDER( TabFolder.class.getName(), new String[] { "TOP", "BOTTOM", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    CTAB_FOLDER( CTabFolder.class.getName(), new String[] { "TOP", "BOTTOM", "CLOSE", "FLAT", "SINGLE", "MULTI", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    CTAB_ITEM( CTabItem.class.getName(), new String[] { "CLOSE" } ),
    TABLE( Table.class.getName(), new String[] { "SINGLE", "MULTI", "CHECK", "FULL_SELECTION", "HIDE_SELECTION", "VIRTUAL", "NO_SCROLL", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    TABLE_COLUMN( TableColumn.class.getName(), new String[] { "LEFT", "RIGHT", "CENTER" } ),
    TEXT( Text.class.getName(), new String[] { "CENTER", "LEFT", "MULTI", "PASSWORD", "SEARCH", "SINGLE", "RIGHT", "READ_ONLY", "WRAP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    TOOL_BAR( ToolBar.class.getName(), new String[] { "FLAT", "HORIZONTAL", "VERTICAL", "NO_FOCUS", "NO_RADIO_GROUP", "BORDER", "LEFT_TO_RIGHT" } ),
    TOOL_ITEM( ToolItem.class.getName(), new String[] { "PUSH", "CHECK", "RADIO", "SEPARATOR", "DROP_DOWN" } ),
    TOOL_TIP( ToolTip.class.getName(), new String[] { "BALLOON", "ICON_ERROR", "ICON_INFORMATION", "ICON_WARNING" } ),
    TREE( Tree.class.getName(), new String[] { "SINGLE", "MULTI", "CHECK", "FULL_SELECTION", "VIRTUAL", "NO_SCROLL", "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    TREE_COLUMN( TreeColumn.class.getName(), new String[] { "LEFT", "RIGHT", "CENTER" } ),
    CANVAS( Canvas.class.getName(), new String[] { "NO_FOCUS", "NO_RADIO_GROUP", "H_SCROLL", "V_SCROLL", "BORDER", "LEFT_TO_RIGHT" } ),
    WIDGET( Widget.class.getName(), new String[] {} );

    private String[] styles;
    private String widgetType;

    AllowedStyles( String widgetType, String[] styles ) {
      this.widgetType = widgetType;
      this.styles = styles;
    }

    public String getWidgetType() {
      return widgetType;
    }

    public String[] getStyles() {
      return styles;
    }
  }

  public static String[] getAllowedStylesForWidget( Widget widget ) {
    Class widgetClass = widget.getClass();
    String[] result = getAllowedStylesByClass( widgetClass );
    while( result == null ) {
      widgetClass = widgetClass.getSuperclass();
      result = getAllowedStylesByClass( widgetClass );
    }
    return result;
  }

  private static String[] getAllowedStylesByClass( Class clazz ) {
    String[] result = null;
    String widgetType = clazz.getName();
    for( AllowedStyles allowedStyles : AllowedStyles.values() ) {
      if( allowedStyles.getWidgetType().equals( widgetType ) ) {
        result = allowedStyles.getStyles();
      }
    }
    return result;
  }

  private RWTStylesUtil() {
    // prevent instantiation
  }
}
