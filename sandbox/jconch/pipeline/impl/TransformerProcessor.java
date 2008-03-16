package jconch.pipeline.impl;

import jconch.pipeline.PipeLink;
import jconch.pipeline.Processor;
import jconch.pipeline.ThreadingModel;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;

/**
 * A {@link Transformer}-based implementation of a {@link Processor}.
 * <p/>
 * <b>Implementation Note:</b> Due to <a
 * href="http://enfranchisedmind.com/blog/archive/2007/05/17/232" target="v">a
 * failing of generics</a>, there is no type-safety available for this
 * implementation.
 *
 * @author Robert Fischer
 */
public abstract class TransformerProcessor extends Processor {

    private final Transformer moreThanMeetsTheEye;

    /**
     * Constructor.
     *
     * @param trans The implementation of the processing.
     * @throws NullArgumentException If any argument is <code>null</code>.
     */
    protected TransformerProcessor(final Transformer trans, final ThreadingModel threading, final PipeLink inLink,
                                   final PipeLink outLink) {
        super(threading, inLink, outLink);
        if (trans == null) {
            throw new NullArgumentException("trans");
        }
        this.moreThanMeetsTheEye = trans;
    }

    /**
     * Delegates the implemenation to {@link Transformer#transform(Object)}.
     */
    @Override
    public Object process(final Object item) {
        return moreThanMeetsTheEye.transform(item);
    }

}
