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

package org.eclipse.rwt.internal.theme.css;

import org.w3c.css.sac.*;

/**
 * ConditionFactory implementation for parsing RAP theme files. All returned
 * conditions implement the interface {@link ConditionExt}.
 */
public class ConditionFactoryImpl implements ConditionFactory {

  private final CssFileReader reader;

  public ConditionFactoryImpl( final CssFileReader reader ) {
    this.reader = reader;
  }

  public AttributeCondition createClassCondition( final String namespaceURI,
                                                  final String value )
    throws CSSException
  {
    return new ClassConditionImpl( value );
  }

  public AttributeCondition createPseudoClassCondition( final String namespaceURI,
                                                        final String value )
    throws CSSException
  {
    return new PseudoClassConditionImpl( value );
  }

  public AttributeCondition createAttributeCondition( final String localName,
                                                      final String namespaceURI,
                                                      final boolean specified,
                                                      final String value )
    throws CSSException
  {
    return new AttributeConditionImpl( localName, value, specified );
  }

  public AttributeCondition createOneOfAttributeCondition( final String localName,
                                                           final String namespaceURI,
                                                           final boolean specified,
                                                           final String value )
    throws CSSException
  {
    return new OneOfAttributeCondition( localName, value, specified );
  }

  public CombinatorCondition createAndCondition( final Condition first,
                                                 final Condition second )
    throws CSSException
  {
    return new AndConditionImpl( first, second );
  }

  // ==========================================================================
  // Not supported by RAP

  public LangCondition createLangCondition( final String lang )
    throws CSSException
  {
    String mesg = "Lang conditions not supported by RAP - ignored";
    reader.addProblem( new CSSException( mesg ) );
    return new NullLangCondition();
  }

  public AttributeCondition createIdCondition( final String value )
    throws CSSException
  {
    String mesg = "Id conditions not supported by RAP - ignored";
    reader.addProblem( new CSSException( mesg ) );
    return new NullAttributeCondition();
  }

  public AttributeCondition createBeginHyphenAttributeCondition( final String localName,
                                                                 final String namespaceURI,
                                                                 final boolean specified,
                                                                 final String value )
    throws CSSException
  {
    String mesg = "Begin hyphen attribute conditions not supported by RAP - ignored";
    reader.addProblem( new CSSException( mesg ) );
    return new NullAttributeCondition();
  }

  // ==========================================================================
  // Not supported by CSS 2

  public CombinatorCondition createOrCondition( final Condition first,
                                                final Condition second )
    throws CSSException
  {
    throw new CSSException( "Or conditions not supported by CSS2" );
  }

  public NegativeCondition createNegativeCondition( final Condition condition )
    throws CSSException
  {
    throw new CSSException( "Negative conditions not supported by CSS2" );
  }

  public PositionalCondition createPositionalCondition( final int position,
                                                        final boolean typeNode,
                                                        final boolean type )
    throws CSSException
  {
    throw new CSSException( "Positional conditions not supported by CSS2" );
  }

  public Condition createOnlyChildCondition() throws CSSException {
    throw new CSSException( "Only-one-child conditions not supported by CSS2" );
  }

  public Condition createOnlyTypeCondition() throws CSSException {
    throw new CSSException( "Only-one-type conditions not supported by CSS2" );
  }

  public ContentCondition createContentCondition( final String data )
    throws CSSException
  {
    throw new CSSException( "Content conditions not supported by CSS2" );
  }
}
