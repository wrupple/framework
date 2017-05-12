package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.liveconnect.ArgumentHelper;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

/**
 * Created by rarl on 11/05/17.
 */
public class DetermineSolutionFieldsDomainImpl implements DetermineSolutionFieldsDomain {

    protected Logger log = LoggerFactory.getLogger(DetermineSolutionFieldsDomainImpl.class);

    private final TaskRunnerPlugin plugin;
    private final SystemCatalogPlugin catalogPlugin;

    @Inject
    public DetermineSolutionFieldsDomainImpl(TaskRunnerPlugin plugin,SystemCatalogPlugin catalogPlugin) {
        this.plugin = plugin;
        this.catalogPlugin=catalogPlugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ExcecutionContext requestContext = (ExcecutionContext) ctx;
        ActivityContext context = requestContext.getServiceContext();
        ProcessTaskDescriptor request = context.getTaskDescriptorValue();
        log.info("Resolving problem model");
        Model model = plugin.getSolver().resolveProblemContext(context);
        log.debug("Resolving Solution Type");
        String solutionType =(String) request.getCatalog();
        CatalogActionContext catalogContext= catalogPlugin.spawn(context.getExcecutionContext());
        CatalogDescriptor solutionDescriptor = catalogPlugin.getDescriptorForName(solutionType,catalogContext);

        //by default, all fields are eligible for solving
        log.debug("Resolving problem variable names");
        solutionDescriptor.getFieldsValues().stream().
                filter(field -> isEligible(field)).
                forEach(field -> makeIntegerVariable(field,model));

        return CONTINUE_PROCESSING;
    }

    private Variable makeIntegerVariable(FieldDescriptor field, Model model ) {
        log.debug("Assigning solution domain for:");
        String fieldId = field.getFieldId();
        log.debug(fieldId);
        IntVar variable;
        //TODO each of this cases should be named and mapped to easily add more variable types
        if(field.getDefaultValueOptions()!=null && !field.getDefaultValueOptions().isEmpty()){
            log.trace("Enumerated domain");
            variable = model.intVar(fieldId, resolveEnumeratedDomain(field.getDefaultValueOptions())); // y in {2, 3, 8}
        }else if(field.getConstraintsValues()!=null && !field.getConstraintsValues().isEmpty()){
            log.trace("Bounded domain");
            List<Constraint> constraints = field.getConstraintsValues();
            variable = model.intVar(fieldId, resolveDomainBound(constraints,fieldId,false),resolveDomainBound(constraints,fieldId,true)); // x in [0,5]
        }else{
            throw new IllegalArgumentException("unable to make a variable out of field "+fieldId);
        }
        return variable;

    }

    private int resolveDomainBound(List<Constraint> constraints,String fieldId,boolean upperBound) {
        String constriantName ;
        if(upperBound){
            log.trace("resolving variable upper bound");
            constriantName = Max.class.getSimpleName();
        }else{
            log.trace("resolving variable lower bound");
            constriantName = Min.class.getSimpleName();
        }
        Optional<Constraint> match = constraints.stream().filter(constraint -> constriantName.equals(constraint.getDistinguishedName())).findAny();
        if(match.isPresent()){
            return constraintValue(match.get().getProperties(),fieldId);
        }else{
            throw new IllegalArgumentException("No Max or Min bounds defined by field "+fieldId);
        }
    }

    private int constraintValue(List<String> properties,String fieldId) {
        Optional<String> match = properties.stream().filter(s -> s.startsWith("value=")).findAny();
        if(match.isPresent()){
            String property = match.get();
            int split = property.indexOf('=');
            String value = property.substring(split + 1, property.length() - 1);
            if(log.isDebugEnabled()){
                log.debug("bounded value :"+value);
            }
            return Integer.parseInt(value);
        }else{
            throw new IllegalArgumentException("No Max or Min bounds defined by field "+fieldId);
        }
    }


    private int[] resolveEnumeratedDomain(List<String> domain) {
        int[] regreso = new int[domain.size()];
        for(int i = 0 ; i < regreso.length; i++){
            regreso[i] = Integer.parseInt(domain.get(i));
        }
        return regreso;
    }

    private boolean isEligible(FieldDescriptor field) {
        //only integer fields with constraints or defined domains are eligible
        boolean eligibility = field.getDataType()== CatalogEntry.INTEGER_DATA_TYPE && ((field.getDefaultValueOptions()!=null && !field.getDefaultValueOptions().isEmpty())
                || (field.getConstraintsValues()!=null && !field.getConstraintsValues().isEmpty()));

        return eligibility;
    }
}
