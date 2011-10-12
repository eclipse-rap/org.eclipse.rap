/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "widget-tool-tip" : {
    include : "popup",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Widget-ToolTip", "border" );
      result.animation = tv.getCssAnimation( "Widget-ToolTip", "animation" );
      result.padding = tv.getCssBoxDimensions( "Widget-ToolTip", "padding" );
      result.textColor = tv.getCssColor( "Widget-ToolTip", "color" );
      result.font = tv.getCssFont( "Widget-ToolTip", "font" );
      result.backgroundColor = tv.getCssColor( "Widget-ToolTip", "background-color" );
      result.backgroundImage = tv.getCssImage( "Widget-ToolTip", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Widget-ToolTip", "background-image" );
      result.opacity = tv.getCssFloat( "Widget-ToolTip", "opacity" );
      result.shadow = tv.getCssShadow( "Widget-ToolTip", "box-shadow" );
      return result;
    }
  }

// END TEMPLATE //
};