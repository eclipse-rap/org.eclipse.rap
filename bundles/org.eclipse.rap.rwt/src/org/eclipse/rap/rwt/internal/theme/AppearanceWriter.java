/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.internal.theme;

import java.util.List;


public final class AppearanceWriter {

  private AppearanceWriter() {
  }

  public static String createAppearanceTheme( List<String> appearances ) {
    StringBuilder code = new StringBuilder();
    appendHead( code );
    appendAppearances( code, appearances );
    appendTail( code );
    return code.toString();
  }

  private static void appendHead( StringBuilder code ) {
    code.append( "rwt.theme.AppearanceManager.getInstance().setCurrentTheme( {\n" );
    code.append( "  name : \"rwtAppearance\",\n" );
    code.append( "  appearances : {\n" );
  }

  private static void appendAppearances( StringBuilder code, List<String> appearances ) {
    boolean valueWritten = false;
    for( String appearance : appearances ) {
      if( valueWritten ) {
        code.append( ",\n" );
      }
      code.append( appearance );
      valueWritten = true;
    }
  }

  private static void appendTail( StringBuilder code ) {
    code.append( "\n" );
    code.append( "  }\n" );
    code.append( "} );\n" );
  }

}
