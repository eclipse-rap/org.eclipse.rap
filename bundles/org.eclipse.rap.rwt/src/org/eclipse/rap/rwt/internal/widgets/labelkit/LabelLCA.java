/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.labelkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.widgets.Label;
import org.eclipse.rap.rwt.widgets.Widget;


public class LabelLCA extends AbstractWidgetLCA {
  
  private static final AbstractLabelLCADelegate SEPARATOR_LCA 
    = new SeparatorLabelLCA();
  private static final AbstractLabelLCADelegate LABEL_LCA 
    = new StandardLabelLCA();
  
  public void preserveValues( final Widget widget ) {
    if( ( widget.getStyle() & RWT.SEPARATOR ) != 0 ) {
      SEPARATOR_LCA.preserveValues( ( Label )widget );
    } else {
      LABEL_LCA.preserveValues( ( Label )widget );
    }
  }
  
  public void readData( final Widget widget ) {
    if( ( widget.getStyle() & RWT.SEPARATOR ) != 0 ) {
      SEPARATOR_LCA.readData( ( Label )widget );
    } else {
      LABEL_LCA.readData( ( Label )widget );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    if( ( widget.getStyle() & RWT.SEPARATOR ) != 0 ) {
      SEPARATOR_LCA.renderInitialization( ( Label )widget );
    } else {
      LABEL_LCA.renderInitialization( ( Label )widget );
    }
  }

  public void renderChanges( final Widget widget ) throws IOException {
    if( ( widget.getStyle() & RWT.SEPARATOR ) != 0 ) {
      SEPARATOR_LCA.renderChanges( ( Label )widget );
    } else {
      LABEL_LCA.renderChanges( ( Label )widget );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    if( ( widget.getStyle() & RWT.SEPARATOR ) != 0 ) {
      SEPARATOR_LCA.renderDispose( ( Label )widget );
    } else {
      LABEL_LCA.renderDispose( ( Label )widget );
    }
  }
}
