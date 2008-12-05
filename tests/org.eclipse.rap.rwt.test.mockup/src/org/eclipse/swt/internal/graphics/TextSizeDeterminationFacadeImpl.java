package org.eclipse.swt.internal.graphics;
/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/


import java.io.IOException;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.graphics.TextSizeDetermination.ICalculationItem;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;


public final class TextSizeDeterminationFacadeImpl
  extends TextSizeDeterminationFacade
{

  public String createMeasureStringInternal( final String string,
                                             final boolean expandNewLines )
  {
    return string;
  }

  public ICalculationItem[] writeStringMeasurementsInternal()
    throws IOException
  {
    return new ICalculationItem[ 0 ];
  }

  public IProbe[] writeFontProbingInternal() throws IOException {
    return new IProbe[ 0 ];
  }

  public String createFontParamInternal( final Font font ) {
    return null;
  }
}
