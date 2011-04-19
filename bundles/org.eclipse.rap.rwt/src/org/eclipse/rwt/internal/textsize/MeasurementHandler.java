/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.io.IOException;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;


final class MeasurementHandler implements PhaseListener, HttpSessionBindingListener {
  private static final String KEY_SCROLLED_COMPOSITE_CONTENT_SIZE = "org.eclipse.rap.content-size";
  private static final String KEY_SCROLLED_COMPOSITE_ORIGIN = "org.eclipse.rap.sc-origin";
  private static final long serialVersionUID = 1L;

  
  private final Display display;
  private MeasurementItem[] calculationItems;
  private boolean renderDone;
  private Probe[] probes;

  MeasurementHandler() {
    this.display = RWTLifeCycle.getSessionDisplay();
  }

  //////////////////////////
  // interface PhaseListener

  public void beforePhase( PhaseEvent event ) {
  }

  public void afterPhase( PhaseEvent event ) {
    if( beforeMeasurement( event ) ) {
      writeMeasurementContent();
    }
    if( afterMeasurement( event ) ) {
      applyMeasurementResults();
      MeasurementUtil.deregister();
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  ///////////////////////////////////////
  // interface HttpSessionBindingListener

  public void valueBound( HttpSessionBindingEvent event ) {
  }

  public void valueUnbound( HttpSessionBindingEvent event ) {
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
        lifeCycle.removePhaseListener( MeasurementHandler.this );
      }
    } );
  }

  //////////////////
  // helping methods
  
  static void readProbedFonts( Probe[] probes ) {
    boolean hasProbes = probes != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasProbes && i < probes.length; i++ ) {
      Probe probe = probes[ i ];
      String name = String.valueOf( probe.getFontData().hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        Point size = getSize( value );
        TextSizeProbeResults.getInstance().createProbeResult( probe, size );
      }
    }
  }


  void readMeasuredStrings() {
    boolean hasItems = calculationItems != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasItems && i < calculationItems.length; i++ ) {
      MeasurementItem item = calculationItems[ i ];
      String name = String.valueOf( item.hashCode() );
      String value = request.getParameter( name );
      // TODO [fappel]: Workaround for background process problem
      if( value != null ) {
        Point size = getSize( value );
        TextSizeDataBase.store( item.getFontData(),
                                item.getTextToMeasure(),
                                item.getWrapWidth(),
                                size );
      }
    }
  }

  private boolean afterMeasurement( PhaseEvent event ) {
    return    requestBelongsToHandler() 
           && renderDone 
           && event.getPhaseId() == PhaseId.PROCESS_ACTION;
  }

  private boolean requestBelongsToHandler() {
    return display == RWTLifeCycle.getSessionDisplay();
  }

  private boolean beforeMeasurement( PhaseEvent event ) {
    return    requestBelongsToHandler()
           && event.getPhaseId() == PhaseId.RENDER;
  }

  private void applyMeasurementResults() {
    readProbedFonts( probes );
    readMeasuredStrings();
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      forceRecalculations( shells[ i ] );
    }
  }

  private void forceRecalculations( Shell shell ) {
    Rectangle boundsBuffer = shell.getBounds();
    bufferScrolledCompositeOrigins( shell );
    clearLayoutBuffers( shell );
    enlargeShell( shell );
    clearLayoutBuffers( shell );
    restoreShellSize( shell, boundsBuffer );
    restoreScrolledCompositeOrigins( shell );
  }

  private void restoreShellSize( Shell shell, Rectangle bufferedBounds ) {
    getShellAdapter( shell ).setBounds( bufferedBounds );
  }

  private void enlargeShell( Shell shell ) {
    Rectangle bnds = shell.getBounds();
    Rectangle bounds1000 = new Rectangle( bnds.x, bnds.y, bnds.width + 1000, bnds.height + 1000 );
    restoreShellSize( shell, bounds1000 );
  }

  private IShellAdapter getShellAdapter( Shell shell ) {
    return ( IShellAdapter )shell.getAdapter( IShellAdapter.class );
  }

  private void restoreScrolledCompositeOrigins( Shell shell ) {
    WidgetTreeVisitor.accept( shell, createRestoreSCOriginsVisitor() );
  }

  private void clearLayoutBuffers( Shell shell ) {
    WidgetTreeVisitor.accept( shell, createClearLayoutBuffersVisitor() );
  }

  private void bufferScrolledCompositeOrigins( Shell shell ) {
    WidgetTreeVisitor.accept( shell, createBufferSCOriginsVisitor() );
  }

  private Shell[] getShells() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    return displayAdapter.getShells();
  }

  private AllWidgetTreeVisitor createRestoreSCOriginsVisitor() {
    return new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        if( widget instanceof ScrolledComposite ) {
          ScrolledComposite composite = ( ScrolledComposite )widget;
          Point oldOrigin = ( Point )composite.getData( KEY_SCROLLED_COMPOSITE_ORIGIN );
          if( oldOrigin != null ) {
            composite.setOrigin( oldOrigin );
            composite.setData( KEY_SCROLLED_COMPOSITE_ORIGIN, null );

            Control content = composite.getContent();
            if( content != null ) {
              Point size = ( Point )content.getData( KEY_SCROLLED_COMPOSITE_CONTENT_SIZE );
              if( size != null ) {
                content.setSize( size );
                content.setData( KEY_SCROLLED_COMPOSITE_CONTENT_SIZE, null );
              }
            }

          }
        }
        return true;
      }
    };
  }

  private AllWidgetTreeVisitor createBufferSCOriginsVisitor() {
    return new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        if( widget instanceof ScrolledComposite ) {
          ScrolledComposite composite = ( ScrolledComposite )widget;
          composite.setData( KEY_SCROLLED_COMPOSITE_ORIGIN, composite.getOrigin() );
          Control content = composite.getContent();
          if( content != null ) {
            content.setData( KEY_SCROLLED_COMPOSITE_CONTENT_SIZE, content.getSize() );
          }
        }
        return true;
      }
    };
  }

  private AllWidgetTreeVisitor createClearLayoutBuffersVisitor() {
    AllWidgetTreeVisitor result = new AllWidgetTreeVisitor() {
      public boolean doVisit( Widget widget ) {
        if( widget instanceof Composite ) {
          Composite composite = ( Composite )widget;
          composite.changed( composite.getChildren() );
        }
        return true;
      }
    };
    return result;
  }

  private void writeMeasurementContent() {
    try {
      probes = TextSizeDeterminationFacade.writeFontProbing();
      calculationItems = TextSizeDeterminationFacade.writeStringMeasurements();
      renderDone = true;
    } catch( IOException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private static Point getSize( String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ), Integer.parseInt( split[ 1 ] ) );
  }
}