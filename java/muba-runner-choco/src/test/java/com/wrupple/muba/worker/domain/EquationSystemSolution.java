package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by rarl on 10/05/17.
 */
public class EquationSystemSolution extends ContentNodeImpl {
    public static final String CATALOG = "EquationSystemSolution";
    public static final String WITNESS_FIELD ="solverWitness";
    // x in [0,5]
    @Min(0)
    @Max(5)
    //@CatalogFieldValues(defaultValueOptions = {"0","1","2","3","4","5"})
    private int x;
    // y in {2, 3, 8}
    @CatalogFieldValues(defaultValueOptions = {"2","3","8"})
    private int y;

    private String solverWitness;

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

    public String getSolverWitness() {
        return solverWitness;
    }

    public void setSolverWitness(String solverWitness) {
        this.solverWitness = solverWitness;
    }
}
