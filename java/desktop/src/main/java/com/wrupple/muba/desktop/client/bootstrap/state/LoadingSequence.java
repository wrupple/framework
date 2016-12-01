package com.wrupple.muba.desktop.client.bootstrap.state;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.desktop.shared.Sequence;

/**
 * 
 * A loading sequence is an iterable collection of states which modify the same data
 * 
 * @author japi
 *
 * @param <T>
 */
public interface LoadingSequence<T> extends Sequence<State<T,T>>{

}
