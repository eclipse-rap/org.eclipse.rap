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
package org.eclipse.rwt.internal.theme;


public final class QxAppearanceWriter {

  private final StringBuilder code;
  private boolean tailWritten;
  private boolean valueWritten;

  public QxAppearanceWriter() {
    code = new StringBuilder();
    valueWritten = false;
    tailWritten = false;
    writeHead();
  }

  public void appendAppearances( String values ) {
    beforeWriteValue();
    code.append( values );
    afterWriteValue();
  }

  public String getJsCode() {
    if( !tailWritten ) {
      writeTail();
    }
    return code.toString();
  }

  private void beforeWriteValue() {
    if( tailWritten ) {
      throw new IllegalStateException( "Tail already written" );
    }
    if( valueWritten ) {
      code.append( ",\n" );
    }
  }

  private void afterWriteValue() {
    valueWritten = true;
  }

  private void writeHead() {
    code.append( "qx.theme.manager.Appearance.getInstance().setCurrentTheme( {\n" );
    code.append( "  name : \"rwtAppearance\",\n" );
    code.append( "  appearances : {\n" );
  }

  private void writeTail() {
    code.append( "\n" );
    code.append( "  }\n" );
    code.append( "} );\n" );
    tailWritten = true;
  }

}
