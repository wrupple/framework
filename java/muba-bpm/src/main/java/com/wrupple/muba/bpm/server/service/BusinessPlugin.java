package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bootstrap.domain.ExplicitIntent;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;

public interface BusinessPlugin extends CatalogPlugin {

    ExplicitIntent resolveApplicatioItem(String inputType, String outputType);
}
