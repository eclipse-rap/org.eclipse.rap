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
  private ThemeableWidget widget;
  private String primaryElement;

  public AbstractThemeAdapter() {
    matcher = new WidgetMatcher();
  }

  /**
   * Called by the framework to initialize the theme adapter. <strong>Note: This
   * method is not part of the API.</strong>
   */
  public void init( final ThemeableWidget widget ) {
    if( this.widget != null ) {
      throw new IllegalStateException( "Theme adapter already initialized" );
    }
    this.widget = widget;
    if( widget.elements != null && widget.elements.length > 0 ) {
      primaryElement = widget.elements[ 0 ].getName();
    } else {
      String className = widget.widget.getName();
      int last = className.lastIndexOf( '.' );
      primaryElement = className.substring( last + 1 );
    }
    configureMatcher( matcher );
  }

  /**
   * Returns the name of the main CSS element for this widget.
   */
  public String getPrimaryElement() {
    return primaryElement;
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
