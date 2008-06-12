/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class LabelLCA extends AbstractWidgetLCA {

  private static final AbstractLabelLCADelegate SEPARATOR_LCA
    = new SeparatorLabelLCA();
  private static final AbstractLabelLCADelegate LABEL_LCA
    = new StandardLabelLCA();

  public void preserveValues( final Widget widget ) {
    getDelegate( widget ).preserveValues( ( Label )widget );
  }

  public void readData( final Widget widget ) {
    getDelegate( widget ).readData( ( Label )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getDelegate( widget ).renderInitialization( ( Label )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getDelegate( widget ).renderChanges( ( Label )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    getDelegate( widget ).renderDispose( ( Label )widget );
  }

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
    getDelegate( typePoolId ).createResetHandlerCalls( typePoolId );
  }

  public String getTypePoolId( final Widget widget ) {
    // TODO [rh] disabled pooling, see bugs prefixed with [pooling]
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=204107
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=199142
//    return getDelegate( widget ).getTypePoolId( ( Label )widget );
    return null;
  }

  private static AbstractLabelLCADelegate getDelegate( final String tpId ) {
    AbstractLabelLCADelegate result;
    if( tpId.startsWith( SeparatorLabelLCA.TYPE_POOL_ID) ) {
      result = SEPARATOR_LCA;
    } else if( tpId.startsWith( StandardLabelLCA.TYPE_POOL_ID ) ) {
      result = LABEL_LCA;
    } else {
      String txt= "The typePoolId ''{0}'' is not supported.";
      String msg = MessageFormat.format( txt, new Object[] { tpId } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }

  private static AbstractLabelLCADelegate getDelegate( final Widget widget ) {
    AbstractLabelLCADelegate result;
    if( ( widget.getStyle() & SWT.SEPARATOR ) != 0 ) {
      result = SEPARATOR_LCA;
    } else {
      result = LABEL_LCA;
    }
    return result;
  }
}
