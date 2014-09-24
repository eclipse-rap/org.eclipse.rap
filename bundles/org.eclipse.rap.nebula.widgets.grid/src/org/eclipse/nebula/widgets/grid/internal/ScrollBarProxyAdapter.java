package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Adapts a normal scrollbar to the IScrollBar proxy.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class ScrollBarProxyAdapter implements IScrollBarProxy
{
    /**
     * Delegates to this scrollbar.
     */
    private ScrollBar scrollBar;

// RAP [if] Replacement for missing ScrollBar increment and pageIncrement fields
    private int increment;
    private int pageIncrement;


    /**
     * Contructs this adapter by delegating to the given scroll bar.
     *
     * @param scrollBar delegate
     */
    public ScrollBarProxyAdapter(ScrollBar scrollBar)
    {
        super();
        this.scrollBar = scrollBar;
    }

    /**
     * {@inheritDoc}
     */
    public int getIncrement()
    {
// RAP [if]: ScrollBar#.getIncrement() is missing
//        return scrollBar.getIncrement();
        return increment;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaximum()
    {
        return scrollBar.getMaximum();
    }

    /**
     * {@inheritDoc}
     */
    public int getMinimum()
    {
        return scrollBar.getMinimum();
    }

    /**
     * {@inheritDoc}
     */
    public int getPageIncrement()
    {
// RAP [if]: ScrollBar#.getPageIncrement() is missing
//        return scrollBar.getPageIncrement();
        return pageIncrement;
    }

    /**
     * {@inheritDoc}
     */
    public int getSelection()
    {
        return scrollBar.getSelection();
    }

    /**
     * {@inheritDoc}
     */
    public int getThumb()
    {
        return scrollBar.getThumb();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getVisible()
    {
        return scrollBar.getVisible();
    }

    /**
     * {@inheritDoc}
     */
    public void setIncrement(int value)
    {
// RAP [if]: ScrollBar#.setIncrement() is missing
//        scrollBar.setIncrement(value);
        increment = value;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaximum(int value)
    {
        scrollBar.setMaximum(value);
    }

    /**
     * {@inheritDoc}
     */
    public void setMinimum(int value)
    {
        scrollBar.setMinimum(value);
    }

    /**
     * {@inheritDoc}
     */
    public void setPageIncrement(int value)
    {
// RAP [if]: ScrollBar#.setPageIncrement() is missing
//        scrollBar.setPageIncrement(value);
        pageIncrement = value;
    }

    /**
     * {@inheritDoc}
     */
    public void setSelection(int selection)
    {
// RAP [if]: ScrollBar#setSelection() does not limit the value
//        scrollBar.setSelection(selection);
        int minimum = scrollBar.getMinimum();
        int maximum = scrollBar.getMaximum();
        int thumb = scrollBar.getThumb();
        if( selection < minimum ) {
          scrollBar.setSelection( minimum );
        } else if ( selection > maximum - thumb ) {
          scrollBar.setSelection( maximum - thumb );
        } else {
          scrollBar.setSelection( selection );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setThumb(int value)
    {
        scrollBar.setThumb(value);
    }

    /**
     * {@inheritDoc}
     */
    public void setValues(int selection, int minimum, int maximum, int thumb, int increment,
                          int pageIncrement)
    {
// RAP [if]: ScrollBar#.setValues() is missing. Use setValue implementation from Slider.
//        scrollBar.setValues(selection, minimum, maximum, thumb, increment, pageIncrement);
        if( selection >= minimum && selection <= maximum ) {
          scrollBar.setSelection( selection );
        }
        if( 0 <= minimum && minimum < maximum ) {
          scrollBar.setMinimum( minimum );
          if( selection < minimum ) {
            scrollBar.setSelection( minimum );
          }
        }
        if( 0 <= minimum && minimum < maximum ) {
          scrollBar.setMaximum( maximum );
          if( selection > maximum - thumb ) {
            scrollBar.setSelection( maximum - thumb );
          }
        }
        if( thumb >= 1 ) {
          scrollBar.setThumb( thumb );
        }
        if( increment >= 1 && increment <= ( maximum - minimum ) ) {
          this.increment = increment;
        }
        if( pageIncrement >= 1 && pageIncrement <= ( maximum - minimum ) ) {
          this.pageIncrement = pageIncrement;
        }
        if( thumb >= maximum - minimum ) {
          scrollBar.setThumb( maximum - minimum );
          scrollBar.setSelection( minimum );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible)
    {
        scrollBar.setVisible(visible);
    }

    /**
     * {@inheritDoc}
     */
    public void handleMouseWheel(Event e)
    {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionListener(SelectionListener listener)
    {
        scrollBar.addSelectionListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectionListener(SelectionListener listener)
    {
        scrollBar.removeSelectionListener(listener);
    }
}
