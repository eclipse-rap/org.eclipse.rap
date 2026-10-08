/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.swt.custom.CLabel;


public class CLabelOperationHandler extends ControlOperationHandler<CLabel> {

  private static final String PROP_TEXT = "text";

  public CLabelOperationHandler( CLabel clabel ) {
    super( clabel );
  }

  @Override
  public void handleSet( CLabel clabel, JsonObject properties ) {
    super.handleSet( clabel, properties );
    handleSetText( clabel, properties );
  }

  /*
   * PROTOCOL SET text
   *
   * @param text (String) the new label text
   */
  public void handleSetText( CLabel clabel, JsonObject properties ) {
    JsonValue text = properties.get( PROP_TEXT );
    if( text != null ) {
      clabel.setText( text.asString() );
    }
  }

}
