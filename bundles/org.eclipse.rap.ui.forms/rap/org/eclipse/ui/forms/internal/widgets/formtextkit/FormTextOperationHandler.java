/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.ui.forms.widgets.FormText;


@SuppressWarnings("restriction")
public class FormTextOperationHandler extends ControlOperationHandler<FormText> {

  public FormTextOperationHandler( FormText formText ) {
    super( formText );
  }

}
