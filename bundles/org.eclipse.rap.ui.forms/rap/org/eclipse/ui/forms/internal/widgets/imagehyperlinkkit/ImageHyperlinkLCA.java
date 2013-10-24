/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkLCA;
import org.eclipse.ui.forms.widgets.ImageHyperlink;


public class ImageHyperlinkLCA extends HyperlinkLCA {

  private static final String PROP_IMAGE = "image"; //$NON-NLS-1$

  @Override
  public void preserveValues( Widget widget ) {
    super.preserveValues( widget );
    ImageHyperlink imageHyperlink = ( ImageHyperlink )widget;
    WidgetLCAUtil.preserveProperty( imageHyperlink, PROP_IMAGE, imageHyperlink.getImage() );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    super.renderChanges( widget );
    ImageHyperlink imageHyperlink = ( ImageHyperlink )widget;
    WidgetLCAUtil.renderProperty( imageHyperlink, PROP_IMAGE, imageHyperlink.getImage(), null );
  }

}
