package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.catalogs.domain.ContentNodeImpl;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by rarl on 10/05/17.
 */
public class EquationSystemSolution extends ContentNodeImpl  {
    public static final String CATALOG = "EquationSystemSolution";
    // x in [0,5]
    @Min(0)
    @Max(5)
    //@CatalogFieldValues(defaultValueOptions = {"0","1","2","3","4","5"})
    private int x;
    // y in {2, 3, 8}
    @CatalogFieldValues(defaultValueOptions = {"2","3","8"})
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }



    @Override
    public String getCatalogType() {
        return CATALOG;
    }

}
