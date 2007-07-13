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

package org.eclipse.swt.internal.graphics;

import java.io.IOException;

import javax.servlet.http.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontSizeCalculator.ICalculationItem;
import org.eclipse.swt.internal.graphics.FontSizeProbeStore.IProbe;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.HtmlResponseWriter;
import com.w4t.W4TContext;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.service.*;

final class FontSizeCalculationHandler
  implements PhaseListener, HttpSessionBindingListener
{
  private static final long serialVersionUID = 1L;
  private static final String CALCULATION_HANDLER 
    = FontSizeCalculationHandler.class.getName() + ".CalculationHandler";
  
  private final Display display;
  private ICalculationItem[] calculationItems;
  private IProbe[] probes;
  private boolean renderDone;
  
  
  static void register() {
    final Display display = Display.getCurrent();
    if( display != null && display.getThread() == Thread.currentThread() ) {
      ISessionStore session = ContextProvider.getSession();
      if( session.getAttribute( CALCULATION_HANDLER ) == null ) {
        FontSizeCalculationHandler handler 
          = new FontSizeCalculationHandler( display );
        session.setAttribute( CALCULATION_HANDLER, handler );
        W4TContext.getLifeCycle().addPhaseListener( handler );
      }
    }
  }
  
  private FontSizeCalculationHandler( final Display display ) {
    this.display = display;
  }
  
  //////////////////////////
  // interface PhaseListener
  
  public void beforePhase( final PhaseEvent event ) {
  }
  
  public void afterPhase( final PhaseEvent event ) {
    if( display == Display.getCurrent() ) {
      try {
        if( renderDone && event.getPhaseId() == PhaseId.PROCESS_ACTION ) {
          readProbedFonts();
          readMeasuredStrings();
          Shell[] shells = display.getShells();
          for( int i = 0; i < shells.length; i++ ) {
            // TODO [fappel]: Think about a lighter recalculation trigger.
            Point buffer = shells[ i ].getSize();
            AllWidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
              public boolean doVisit( final Widget widget ) {
                if( widget instanceof Composite ) {
                  Composite composite = ( Composite )widget;
                  composite.changed( composite.getChildren() );
                }
                return true;
              }
            };
            WidgetTreeVisitor.accept( shells[ i ], visitor );
            shells[ i ].setSize( buffer.x + 1000, buffer.y + 1000 );
            WidgetTreeVisitor.accept( shells[ i ], visitor );
            shells[ i ].setSize( buffer );
          }
        }
        if( event.getPhaseId() == PhaseId.RENDER ) {
          probes = writeFontProbing();
          calculationItems = writeStringMeasurements();
          renderDone = true;
        }
      } catch( final IOException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        if( renderDone && event.getPhaseId() == PhaseId.PROCESS_ACTION ) {
          W4TContext.getLifeCycle().removePhaseListener( this );
          ISessionStore session = ContextProvider.getSession();
          session.removeAttribute( CALCULATION_HANDLER );
        }
      }
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }
  

  public static void readProbedFonts( final IProbe[] probes ) {
    boolean hasProbes = probes != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasProbes && i < probes.length; i++ ) {
      IProbe probe = probes[ i ];
      String name = String.valueOf( probe.getFont().hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        Point size = getSize( value );
        FontSizeProbeStore.getInstance().createProbeResult( probe, size );
      }
    }
  }
  
  
  ///////////////////////////////////////
  // interface HttpSessionBindingListener
  
  public void valueBound( final HttpSessionBindingEvent event ) {
  }
  
  public void valueUnbound( final HttpSessionBindingEvent event ) {
    UICallBackUtil.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        ILifeCycle lifeCycle = W4TContext.getLifeCycle();
        lifeCycle.removePhaseListener( FontSizeCalculationHandler.this );
      }
    } );
  }
  
  //////////////////
  // helping methods
  
  private ICalculationItem[] writeStringMeasurements()
    throws IOException 
  {
    ICalculationItem[] items = FontSizeCalculator.getCalculationItems();
    if( items.length > 0 ) {
      JSWriter writer = JSWriter.getWriterForResetHandler();
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < items.length; i++ ) {
        param.append( "[ " );
        ICalculationItem item = items[ i ];
        param.append( item.hashCode() );
        param.append( ", " );
        param.append( "\"" );
        param.append( item.getString() );
        param.append( "\", " );
        param.append( createFontParam( item.getFont() ) );
        param.append( ", " );
        param.append( item.getWrapWidth() );
        param.append( " ]" );
        if( i < items.length - 1 ) {
          param.append( ", " );
        }
      }
      param.append( " ]" );
      String funcName = "org.eclipse.swt.FontSizeCalculation.measureStrings";
      writer.callStatic( funcName, 
                         new Object[] { new JSVar( param.toString() ) } );      
    }
    return items;
  }
  
  private IProbe[] writeFontProbing() throws IOException {
    IProbe[] requests = FontSizeProbeStore.getProbeRequests();
    if( requests.length > 0 ) {
      JSWriter writer = JSWriter.getWriterForResetHandler();
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < requests.length; i++ ) {
        IProbe probe = requests[ i ];
        param.append( probe.getJSProbeParam() );
        if( i < requests.length - 1 ) {
          param.append( ", " );
        }
      }
      param.append( " ]" );
      String funcName = "org.eclipse.swt.FontSizeCalculation.probe";
      writer.callStatic( funcName, 
                         new Object[] { new JSVar( param.toString() ) } );
    }
    return requests;
  }
  
  private void readProbedFonts() {
    readProbedFonts( probes );
  }
  
  static String createFontParam( final Font font ) {
    // TODO [fappel]: For convenience I reused the the WidgetLCAUtil#writeFont
    //                method. This may have performance problems since a lot
    //                of buffering and some additional string operations are 
    //                used. So revise this...
    StringBuffer result = new StringBuffer();
    Shell shell = new Shell( Display.getCurrent(), SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter buffer = stateInfo.getResponseWriter(); 
    try {
      HtmlResponseWriter htmlWriter = new HtmlResponseWriter();
      stateInfo.setResponseWriter( htmlWriter );
      WidgetLCAUtil.writeFont( label, font );
      StringBuffer js = new StringBuffer();
      for( int j = 0; j < htmlWriter.getBodySize(); j++ ) {
        js.append( htmlWriter.getBodyToken( j ) );
      }
      String fontString = js.toString();
      String[] split = fontString.split( "\\[" );
      int offset = split[ 1 ].length() - 3;
      result.append( "[" );
      result.append( split[ 1 ].substring( 0, offset ) );
    } catch( final IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      shell.dispose();
      stateInfo.setResponseWriter( buffer );
    }
    return result.toString();
  }
  
  private void readMeasuredStrings() {
    boolean hasItems = calculationItems != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasItems && i < calculationItems.length; i++ ) {
      ICalculationItem item = calculationItems[ i ];
      String name = String.valueOf( item.hashCode() );
      String value = request.getParameter( name );
      // TODO [fappel]: Workaround for background process problem
      if( value != null ) {
        Point size = getSize( value );
        FontSizeDataBase.store( item.getFont(), 
                                item.getString(), 
                                item.getWrapWidth(),
                                size );
      }
    }
  }
  
  private static Point getSize( final String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ), 
                      Integer.parseInt( split[ 1 ] ) );
  }
}