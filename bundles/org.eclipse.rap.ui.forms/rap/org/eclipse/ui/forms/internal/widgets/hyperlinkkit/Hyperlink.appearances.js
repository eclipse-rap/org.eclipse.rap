/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "hyperlink" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        font: tv.getCssFont( "*", "font" ),
        textColor : states.disabled ? tv.getCssColor( "*", "color" ) : "undefined",
        cursor : states.disabled ? "default" : "pointer",
        spacing : 4,
        width : "auto",
        height : "auto",
        horizontalChildrenAlign : "left",
        verticalChildrenAlign : "middle"
      }
    }
  }

// END TEMPLATE //
};
