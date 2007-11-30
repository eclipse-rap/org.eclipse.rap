/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rwt.branding;

import java.util.Map;

/**
 * This data structure represents an HTML tag that goes into the &lt;head&gt;
 * section of the startup page and is used by the branding facility.
 * 
 * @see AbstractBranding
 * @since 1.0.1
 */
public final class Header {

  private static final String[] EMPTY_STRINGS = new String[ 0 ];
  
  private final String tagName;
  private final String[] names;
  private final String[] values;
  
  /**
   * Constructs a new instance of this class with the given 
   * <code>tagName</code>.
   * 
   * @param tagName the name of the tag, must either be <code>meta</code> or
   *          <code>link</code>
   * @param attributes the attributes for this tag or <code>null</code> if
   *          there are no attributes.
   * @throws IllegalArgumentException if <code>tagName</code> isn't either
   *           <code>meta</code> or <code>link</code>.
   */
  public Header( final String tagName, final Map attributes ) {
    if( !"link".equals( tagName ) && !"meta".equals( tagName ) ) {
      String msg 
        = "Invalid tag name. The tag name must be either 'meta' or 'link'.";
      throw new IllegalArgumentException( msg );
    }
    this.tagName = tagName;
    if( attributes == null ) {
      this.names = EMPTY_STRINGS;
      this.values = EMPTY_STRINGS;
    } else {
      int size = attributes.size();
      this.names = new String[ size ];
      attributes.keySet().toArray( names );
      this.values = new String[ size ];
      attributes.values().toArray( values );
    }
  }

  /**
   * Returns the name of the header tag.
   * 
   * @return the tag name
   */
  public String getTagName() {
    return tagName;
  }

  /**
   * Returns the array of attribute names. If no attributes are defined, an 
   * empty array is returned.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain 
   * its list of names, so modifying the array will not affect the receiver. 
   * </p>
   * 
   * @return the attribute names 
   */
  public String[] getNames() {
    String[] result = new String[ names.length ];
    System.arraycopy( names, 0, result, 0, names.length );
    return result;
  }

  /**
   * Returns the array of attribute values. If no attributes are defined, an 
   * empty array is returned.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain 
   * its list of values, so modifying the array will not affect the receiver. 
   * </p>
   * 
   * @return the attribute values 
   */
  public String[] getValues() {
    String[] result = new String[ values.length ];
    System.arraycopy( values, 0, result, 0, values.length );
    return result;
  }
}
