package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import org.apache.commons.chain.Context;

public class ReadWorkerMetadataImpl implements ReadWorkerMetadata {
    @Override
    public boolean execute(Context context) throws Exception {
        return CONTINUE_PROCESSING;
    }
}
