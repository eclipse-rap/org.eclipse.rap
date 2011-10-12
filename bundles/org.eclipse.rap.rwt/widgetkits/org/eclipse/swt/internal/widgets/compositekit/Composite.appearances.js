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

  "composite" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.backgroundColor = tv.getCssColor( "Composite", "background-color" );
      result.backgroundImage = tv.getCssImage( "Composite", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Composite", "background-image" );
      result.border = tv.getCssBorder( "Composite", "border" );
      result.opacity = tv.getCssFloat( "Composite", "opacity" );
      result.shadow = tv.getCssShadow( "Composite", "box-shadow" );
      return result;
    }
  }

// END TEMPLATE //
};