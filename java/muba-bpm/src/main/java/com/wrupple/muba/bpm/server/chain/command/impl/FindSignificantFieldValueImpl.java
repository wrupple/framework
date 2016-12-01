package com.wrupple.muba.bpm.server.chain.command.impl;

import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.bpm.server.chain.command.AdjustError;
import com.wrupple.muba.bpm.server.chain.command.DefinePosibilitySpace;
import com.wrupple.muba.bpm.server.chain.command.DetermineCriteria;
import com.wrupple.muba.bpm.server.chain.command.FieldHandlingCommand;
import com.wrupple.muba.bpm.server.chain.command.FindSignificantFieldValue;
import com.wrupple.muba.bpm.server.chain.command.PickBestValue;
import com.wrupple.muba.bpm.server.chain.command.WriteFieldValue;

public class FindSignificantFieldValueImpl extends ChainBase implements
		FindSignificantFieldValue {
	
//humans learn by  (criteria?)
	//by heuristics (ill go directly to the asian section to find soy sauce)
	//and trial and error (observation)
	public FindSignificantFieldValueImpl(DetermineCriteria criteria,DefinePosibilitySpace posibilities,AdjustError error,PickBestValue create,WriteFieldValue write) {
		super(new FieldHandlingCommand[]{criteria,posibilities,error,create,write});
	}
	
	
	public FindSignificantFieldValueImpl(DefinePosibilitySpace posibilities,AdjustError error,PickBestValue create,WriteFieldValue write) {
		super(new FieldHandlingCommand[]{posibilities,error,create,write});
	}
	

}
