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
package org.eclipse.rap.e4.apache.jxpath.ri.compiler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.rap.e4.apache.jxpath.Pointer;
import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.InfoSetUtil;
import org.eclipse.rap.e4.apache.jxpath.ri.axes.InitialContext;
import org.eclipse.rap.e4.apache.jxpath.ri.axes.SelfContext;

/**
 * Common superclass for the implementations of Expression for the operations
 * "=" and "!=".
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public abstract class CoreOperationCompare extends CoreOperation {
    private boolean invert;

    /**
     * Create a new CoreOperationCompare.
     * @param arg1 left operand
     * @param arg2 right operand
     */
    public CoreOperationCompare(Expression arg1, Expression arg2) {
        this(arg1, arg2, false);
    }

    /**
     * Create a new CoreOperationCompare.
     * @param arg1 left operand
     * @param arg2 right operand
     * @param invert whether to invert (not) the comparison
     */
    protected CoreOperationCompare(Expression arg1, Expression arg2, boolean invert) {
        super(new Expression[] { arg1, arg2 });
        this.invert = invert;
    }

    public Object computeValue(EvalContext context) {
        return equal(context, args[0], args[1]) ? Boolean.TRUE : Boolean.FALSE;
    }

    protected int getPrecedence() {
        return COMPARE_PRECEDENCE;
    }

    protected boolean isSymmetric() {
        return true;
    }

    /**
     * Compares two values.
     * @param context evaluation context
     * @param left operand
     * @param right operand
     * @return whether left = right in XPath terms
     */
    protected boolean equal(EvalContext context, Expression left,
            Expression right) {
        Object l = left.compute(context);
        Object r = right.compute(context);

        if (l instanceof InitialContext) {
            ((EvalContext) l).reset();
        }

        if (l instanceof SelfContext) {
            l = ((EvalContext) l).getSingleNodePointer();
        }

        if (r instanceof InitialContext) {
            ((EvalContext) r).reset();
        }

        if (r instanceof SelfContext) {
            r = ((EvalContext) r).getSingleNodePointer();
        }

        if (l instanceof Collection) {
            l = ((Collection) l).iterator();
        }

        if (r instanceof Collection) {
            r = ((Collection) r).iterator();
        }

        if (l instanceof Iterator && r instanceof Iterator) {
            return findMatch((Iterator) l, (Iterator) r);
        }
        if (l instanceof Iterator) {
            return contains((Iterator) l, r);
        }
        if (r instanceof Iterator) {
            return contains((Iterator) r, l);
        }
        return equal(l, r);
    }

    /**
     * Learn whether it contains value.
     * @param it Iterator to check
     * @param value for which to look
     * @return whether value was found
     */
    protected boolean contains(Iterator it, Object value) {
        while (it.hasNext()) {
            Object element = it.next();
            if (equal(element, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Learn whether lit intersects rit.
     * @param lit left Iterator
     * @param rit right Iterator
     * @return boolean
     */
    protected boolean findMatch(Iterator lit, Iterator rit) {
        HashSet left = new HashSet();
        while (lit.hasNext()) {
            left.add(lit.next());
        }
        while (rit.hasNext()) {
            if (contains(left.iterator(), rit.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Learn whether l equals r in XPath terms.
     * @param l left operand
     * @param r right operand
     * @return whether l = r
     */
    protected boolean equal(Object l, Object r) {
        if (l instanceof Pointer) {
            l = ((Pointer) l).getValue();
        }

        if (r instanceof Pointer) {
            r = ((Pointer) r).getValue();
        }

        boolean result;
        if (l instanceof Boolean || r instanceof Boolean) {
            result = l == r || InfoSetUtil.booleanValue(l) == InfoSetUtil.booleanValue(r);
        }
        else if (l instanceof Number || r instanceof Number) {
            //if either side is NaN, no comparison returns true:
            double ld = InfoSetUtil.doubleValue(l);
            if (Double.isNaN(ld)) {
                return false;
            }
            double rd = InfoSetUtil.doubleValue(r);
            if (Double.isNaN(rd)) {
                return false;
            }
            result = ld == rd;
        }
        else {
            if (l instanceof String || r instanceof String) {
                l = InfoSetUtil.stringValue(l);
                r = InfoSetUtil.stringValue(r);
            }
            result = l == r || l != null && l.equals(r);
        }
        return result ^ invert;
    }

}
