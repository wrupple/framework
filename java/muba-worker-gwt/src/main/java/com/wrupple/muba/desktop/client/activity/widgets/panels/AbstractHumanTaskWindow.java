package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

public abstract class AbstractHumanTaskWindow extends ResizeComposite implements
        HumanTaskWindow {

    protected Unit parseUnit(String layoutUnit) {
        if (layoutUnit != null) {
            Unit newUnit = Unit.PX;
            if (Unit.PX.toString().equals(layoutUnit)) {
                newUnit = Unit.PX;
            } else if (Unit.PCT.toString().equals(layoutUnit)) {
                newUnit = Unit.PCT;
            } else if (Unit.EM.toString().equals(layoutUnit)) {
                newUnit = Unit.EM;
            } else if (Unit.EX.toString().equals(layoutUnit)) {
                newUnit = Unit.EX;
            } else if (Unit.PT.toString().equals(layoutUnit)) {
                newUnit = Unit.PT;
            } else if (Unit.PC.toString().equals(layoutUnit)) {
                newUnit = Unit.PC;
            } else if (Unit.IN.toString().equals(layoutUnit)) {
                newUnit = Unit.IN;
            } else if (Unit.CM.toString().equals(layoutUnit)) {
                newUnit = Unit.CM;
            } else if (Unit.MM.toString().equals(layoutUnit)) {
                newUnit = Unit.MM;
            }
            return newUnit;
        }
        return null;

    }

}
