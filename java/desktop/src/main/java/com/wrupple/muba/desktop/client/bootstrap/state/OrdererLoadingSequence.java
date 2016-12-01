package com.wrupple.muba.desktop.client.bootstrap.state;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.desktop.shared.OrdinalIndexedSequence;

public class OrdererLoadingSequence<T> extends OrdinalIndexedSequence<State<T,T>> implements LoadingSequence<T> {

}
