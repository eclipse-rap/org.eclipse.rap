/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Widget;


/**
 * Base class for theme adapters.
 */
public abstract class AbstractThemeAdapter implements IThemeAdapter {

  private WidgetMatcher matcher;

  public AbstractThemeAdapter() {
    matcher = new WidgetMatcher();
    configureMatcher( matcher );
  }

  /**
   * Returns the name of the main CSS element for a given widget.
   */
  public static String getPrimaryElement( final Widget widget ) {
    String result;
    Class widgetClass = widget.getClass();
    ThemeManager manager = ThemeManager.getInstance();
    ThemeableWidget thWidget = manager.getThemeableWidget( widgetClass );
    if( thWidget != null
        && thWidget.elements != null
        && thWidget.elements.length > 0 )
    {
      result = thWidget.elements[ 0 ].getName();
    } else {
      String className = widgetClass.getName();
      int last = className.lastIndexOf( '.' );
      result = className.substring( last + 1 );
    }
    return result;
  }

  /**
   * Configures the widget matcher to be able to match widgets. Subclasses need
   * to implement.
   */
  protected abstract void configureMatcher( final WidgetMatcher matcher );

  protected Color getCssColor( final String cssElement,
                               final String cssProperty,
                               final Widget widget )
  {
    QxColor color = ( QxColor )getCssValue( cssElement, cssProperty, widget );
    return QxColor.createColor( color );
  }

  protected Font getCssFont( final String cssElement,
                             final String cssProperty,
                             final Widget widget )
  {
    QxFont font = ( QxFont )getCssValue( cssElement, cssProperty, widget );
    return QxFont.createFont( font );
  }

  protected int getCssBorderWidth( final String cssElement,
                                   final String cssProperty,
                                   final Widget widget )
  {
    QxBorder border = ( QxBorder )getCssValue( cssElement, cssProperty, widget );
    return border.width;
  }

  protected int getCssDimension( final String cssElement,
                                 final String cssProperty,
                                 final Widget widget )
  {
    QxDimension dim
      = ( QxDimension )getCssValue( cssElement, cssProperty, widget );
    return dim.value;
  }

  protected Rectangle getCssBoxDimensions( final String cssElement,
                                           final String cssProperty,
                                           final Widget widget )
  {
    QxBoxDimensions boxdim
      = ( QxBoxDimensions )getCssValue( cssElement, cssProperty, widget );
    return QxBoxDimensions.createRectangle( boxdim );
  }

  private QxType getCssValue( final String cssElement,
                              final String cssProperty,
                              final Widget widget )
  {
    Theme theme = ThemeUtil.getTheme();
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    ConditionalValue[] values = valuesMap.getValues( cssElement, cssProperty );
    QxType result = matcher.select( values, widget );
    if( result == null ) {
      theme = ThemeUtil.getDefaultTheme();
      valuesMap = theme.getValuesMap();
      values = valuesMap.getValues( cssElement, cssProperty );
      result = matcher.select( values, widget );
    }
    return result;
  }
}
