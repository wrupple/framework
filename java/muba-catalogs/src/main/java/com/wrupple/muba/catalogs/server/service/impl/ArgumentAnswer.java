package com.wrupple.muba.catalogs.server.service.impl;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * 
 * 
 * FROM http://stackoverflow.com/questions/2667172/how-can-i-mock-a-method-in-easymock-that-shall-return-one-of-its-parameters
 * 
 * Enable a Captured argument to be answered to an Expectation. For example, the
 * Factory interface defines the following
 * 
 * <pre>
 * CharSequence encode(final CharSequence data);
 * </pre>
 * 
 * For test purpose, we don't need to implement this method, thus it should be
 * mocked.
 * 
 * <pre>
 * final Factory factory = mocks.createMock("factory", Factory.class);
 * final ArgumentAnswer<CharSequence> parrot = new ArgumentAnswer<CharSequence>();
 * EasyMock.expect(factory.encode(EasyMock.capture(new Capture<CharSequence>()))).andAnswer(parrot).anyTimes();
 * </pre>
 * 
 * Created on 22 juin 2010.
 * 
 * @author Remi Fouilloux
 *
 */
public class ArgumentAnswer<T> implements IAnswer<T> {

	private final int argumentOffset;

	public ArgumentAnswer() {
		this(0);
	}

	public ArgumentAnswer(int offset) {
		this.argumentOffset = offset;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public T answer() throws Throwable {
		final Object[] args = EasyMock.getCurrentArguments();
		if (args.length < (argumentOffset + 1)) {
			throw new IllegalArgumentException("There is no argument at offset " + argumentOffset);
		}
		return (T) args[argumentOffset];
	}

}
