/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.internal.FacadesInitializer;

abstract class TextSizeUtilFacade {

  private final static TextSizeUtilFacade FACADE_IMPL
    = ( TextSizeUtilFacade )FacadesInitializer.load( TextSizeUtilFacade.class );

  static String createMeasurementString( String string, boolean expandLineDelimitors ) {
    return FACADE_IMPL.createMeasurementStringInternal( string, expandLineDelimitors );
  }

  static void writeStringMeasurements() {
    FACADE_IMPL.writeStringMeasurementsInternal();
  }

  static void writeFontProbing() {
    FACADE_IMPL.writeFontProbingInternal();
  }

  static Object getStartupProbeObject() {
    return FACADE_IMPL.getStartupProbeObjectInternal();
  }

  public abstract String createMeasurementStringInternal( String string, boolean expandNewLines );

  public abstract void writeStringMeasurementsInternal();

  public abstract void writeFontProbingInternal();

  public abstract Object getStartupProbeObjectInternal();
}
