/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.axes;

import java.util.Iterator;

import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.InfoSetUtil;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.Expression;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NameAttributeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;
import org.eclipse.rap.e4.apache.jxpath.ri.model.beans.PropertyOwnerPointer;
import org.eclipse.rap.e4.apache.jxpath.ri.model.beans.PropertyPointer;

/**
 * EvalContext that checks predicates.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class PredicateContext extends EvalContext {
    private Expression expression;
    private boolean done = false;
    private Expression nameTestExpression;
    private PropertyPointer dynamicPropertyPointer;

    /**
     * Create a new PredicateContext.
     * @param parentContext parent context
     * @param expression compiled Expression
     */
    public PredicateContext(EvalContext parentContext, Expression expression) {
        super(parentContext);
        this.expression = expression;
        if (expression instanceof NameAttributeTest) {
            nameTestExpression =
                ((NameAttributeTest) expression).getNameTestExpression();
        }
    }

    public boolean nextNode() {
        if (done) {
            return false;
        }
        while (parentContext.nextNode()) {
            if (setupDynamicPropertyPointer()) {
                Object pred = nameTestExpression.computeValue(parentContext);
                String propertyName = InfoSetUtil.stringValue(pred);

                // At this point it would be nice to say:
                // dynamicPropertyPointer.setPropertyName(propertyName)
                // and then: dynamicPropertyPointer.isActual().
                // However some PropertyPointers, e.g. DynamicPropertyPointer
                // will declare that any property you ask for is actual.
                // That's not acceptable for us: we really need to know
                // if the property is currently declared. Thus,
                // we'll need to perform a search.
                boolean ok = false;
                String[] names = dynamicPropertyPointer.getPropertyNames();
                for (int i = 0; i < names.length; i++) {
                    if (names[i].equals(propertyName)) {
                        ok = true;
                        break;
                    }
                }
                if (ok) {
                    dynamicPropertyPointer.setPropertyName(propertyName);
                    position++;
                    return true;
                }
            }
            else {
                Object pred = expression.computeValue(parentContext);
                if (pred instanceof Iterator) {
                    if (!((Iterator) pred).hasNext()) {
                        return false;
                    }
                    pred = ((Iterator) pred).next();
                }

                if (pred instanceof NodePointer) {
                    pred = ((NodePointer) pred).getNode();
                }

                if (pred instanceof Number) {
                    int pos = (int) InfoSetUtil.doubleValue(pred);
                    position++;
                    done = true;
                    return parentContext.setPosition(pos);
                }
                if (InfoSetUtil.booleanValue(pred)) {
                    position++;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Used for an optimized access to dynamic properties using the
     * "map[@name = 'name']" syntax
     * @return whether valid
     */
    private boolean setupDynamicPropertyPointer() {
        if (nameTestExpression == null) {
            return false;
        }

        NodePointer parent = parentContext.getCurrentNodePointer();
        if (parent == null) {
            return false;
        }
        parent = parent.getValuePointer();
        if (!(parent instanceof PropertyOwnerPointer)) {
            return false;
        }
        dynamicPropertyPointer =
            (PropertyPointer) ((PropertyOwnerPointer) parent)
                .getPropertyPointer()
                .clone();
        return true;
    }

    public boolean setPosition(int position) {
        if (nameTestExpression == null) {
            return setPositionStandard(position);
        }
        else {
            if (dynamicPropertyPointer == null && !setupDynamicPropertyPointer()) {
                return setPositionStandard(position);
            }
            if (position < 1
                || position > dynamicPropertyPointer.getLength()) {
                return false;
            }
            dynamicPropertyPointer.setIndex(position - 1);
            return true;
        }
    }

    public NodePointer getCurrentNodePointer() {
        if (position == 0 && !setPosition(1)) {
            return null;
        }
        if (dynamicPropertyPointer != null) {
            return dynamicPropertyPointer.getValuePointer();
        }
        return parentContext.getCurrentNodePointer();
    }

    public void reset() {
        super.reset();
        parentContext.reset();
        done = false;
    }

    public boolean nextSet() {
        reset();
        return parentContext.nextSet();
    }

    /**
     * Basic setPosition
     * @param position to set
     * @return whether valid
     */
    private boolean setPositionStandard(int position) {
        if (this.position > position) {
            reset();
        }

        while (this.position < position) {
            if (!nextNode()) {
                return false;
            }
        }
        return true;
    }
}
