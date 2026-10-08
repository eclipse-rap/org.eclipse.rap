/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil;
import org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkLCA;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;


public class ImageHyperlinkLCA extends HyperlinkLCA {

  public static final ImageHyperlinkLCA INSTANCE = new ImageHyperlinkLCA();

  private static final String PROP_IMAGE = "image"; //$NON-NLS-1$

  @Override
  public void preserveValues( Hyperlink hyperlink ) {
    super.preserveValues( hyperlink );
    ImageHyperlink imageHyperlink = ( ImageHyperlink )hyperlink;
    WidgetLCAUtil.preserveProperty( imageHyperlink, PROP_IMAGE, imageHyperlink.getImage() );
  }

  @Override
  public void renderChanges( Hyperlink hyperlink ) throws IOException {
    super.renderChanges( hyperlink );
    ImageHyperlink imageHyperlink = ( ImageHyperlink )hyperlink;
    WidgetLCAUtil.renderProperty( imageHyperlink, PROP_IMAGE, imageHyperlink.getImage(), null );
  }

}
