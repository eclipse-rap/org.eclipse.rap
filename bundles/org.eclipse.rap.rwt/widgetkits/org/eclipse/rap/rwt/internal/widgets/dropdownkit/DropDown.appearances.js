/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
var appearances = {
// BEGIN TEMPLATE //

  "dropdown" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "DropDown", "border" ),
        shadow : tv.getCssShadow( "DropDown", "box-shadow" )
      };
    }
  }

// END TEMPLATE //
};
