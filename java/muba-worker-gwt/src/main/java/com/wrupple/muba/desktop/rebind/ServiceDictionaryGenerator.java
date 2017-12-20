package com.wrupple.muba.desktop.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

public class ServiceDictionaryGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName)
			throws UnableToCompleteException {

		ServiceDictionaryBinder binder = new ServiceDictionaryBinder(logger, context, typeName);
		try {
			return  binder.createServicer();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			if(e.getCause()!=null){
				Throwable t = e.getCause();
				System.out.println(t.getMessage());
				if(t.getCause()!=null){
					t = t.getCause();
					System.out.println(t.getMessage());
				}
			}
			throw new UnableToCompleteException();
		}
	}

}
