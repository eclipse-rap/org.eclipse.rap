/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;


public final class RichTextUtil {

  public interface IImageSizeProvider {
    Rectangle getImageSize( String imageName );
  }

  public static int getTextWidth( String text, Font font, IImageSizeProvider imageSizeProvider ) {
    int result = 0;
    if( RichTextParser.isRichText( text ) ) {
      TextWidthComputer textWidthComputer = new TextWidthComputer( imageSizeProvider, font );
      RichTextParser parser = new RichTextParser( textWidthComputer );
      parser.parse( text );
      result = textWidthComputer.getMaxLineWidth();
    } else {
      if( text.length() > 0 ) {
        result = Graphics.stringExtent( font, text ).x;
      }
    }
    return result;
  }
  
  private RichTextUtil() {
    // prevent instantiation
  }

  private static class TextWidthComputer implements IRichTextParserCallback {
    
    private final IImageSizeProvider imageSizeProvider;
    private final Font defaultFont;
    private Font currentFont;
    private int currentLineWidth;
    private int maxLineWidth;
    
    TextWidthComputer( IImageSizeProvider imageSizeProvider, Font font ) {
      this.imageSizeProvider = imageSizeProvider;
      this.defaultFont = font;
    }
    
    int getMaxLineWidth() {
      return maxLineWidth;
    }

    public void beginHtml() {
      currentFont = defaultFont;
    }

    public void endHtml() {
      updateMaxLineWidth();
    }

    public void beginFont( String name, int height ) {
      currentFont = new Font( defaultFont.getDevice(), name, height, SWT.NORMAL );
    }

    public void endFont() {
      currentFont = defaultFont;
    }

    public void text( String text ) {
      currentLineWidth += Graphics.stringExtent( currentFont, text ).x;
    }
  
    public void lineBreak() {
      updateMaxLineWidth();
      currentLineWidth = 0;
    }

    public void image( String src ) {
      currentLineWidth += imageSizeProvider.getImageSize( src ).width;
    }
  
    private void updateMaxLineWidth() {
      if( currentLineWidth > maxLineWidth ) {
        maxLineWidth = currentLineWidth;
      }
    }
  }
}
