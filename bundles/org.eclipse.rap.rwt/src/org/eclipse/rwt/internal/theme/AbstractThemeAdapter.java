/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

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

  ////////////////////
  // Delegator methods

  protected Color getCssColor( final String cssElement,
                               final String cssProperty,
                               final Widget widget )
  {
    QxType cssValue
      = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxColor.createColor( ( QxColor )cssValue );
  }

  protected Font getCssFont( final String cssElement,
                             final String cssProperty,
                             final Widget widget )
  {
    QxType cssValue
      = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxFont.createFont( ( QxFont )cssValue );
  }

  protected int getCssBorderWidth( final String cssElement,
                                   final String cssProperty,
                                   final Widget widget )
  {
    QxType cssValue
      = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return ( ( QxBorder )cssValue ).width;
  }

  protected int getCssDimension( final String cssElement,
                                 final String cssProperty,
                                 final Widget widget )
  {
    QxType cssValue
      = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return ( ( QxDimension )cssValue ).value;
  }

  protected Rectangle getCssBoxDimensions( final String cssElement,
                                           final String cssProperty,
                                           final Widget widget )
  {
    QxType cssValue
      = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxBoxDimensions.createRectangle( ( QxBoxDimensions )cssValue );
  }
}
