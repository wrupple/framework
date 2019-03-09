package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.*;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.server.domain.impl.EvaluationContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ListIterator;

@Singleton
public class NaturalLanguageInterpretImpl implements NaturalLanguageInterpret{
    protected Logger log = LogManager.getLogger(NaturalLanguageInterpretImpl.class);

    public static final String ASSIGNATION = "=";

    private final FieldAccessStrategy access;
    @Inject
    public NaturalLanguageInterpretImpl(FieldAccessStrategy access) {
        this.access = access;
    }

    @Override
    public void resolve(ListIterator<String> sentence, Context t, String interpretGivenName) throws Exception {
        if(sentence.hasNext()){
            String rawValue = sentence.next();
            if(log.isDebugEnabled()){
                log.debug("   parameter: "+rawValue);
            }



            int endTokenIndex = rawValue.indexOf(':');
            Class<?> parameterType;
            HasResult<Object> holder = (HasResult<Object>) t;
            if(endTokenIndex>0){
                String accessor = rawValue.substring(0,endTokenIndex);
                rawValue = rawValue.substring(endTokenIndex+1);
                log.trace("      parsing parameter {} with accesor {}",rawValue,accessor);
                Object r ;
                if(accessor.equals("ctx")){
                    r= t.get(rawValue);
                    if(r==null){
                        log.info("      No Context key {}",rawValue);
                    }else{
                        log.trace("      Context Value: {}",r);
                    }
                }else if(accessor.equals("int")){
                    r= Integer.parseInt(rawValue);
                    log.trace("      Explicit conversion output: {}",r);
                }else if(accessor.equals("boolean")){
                    r= Boolean.parseBoolean(rawValue);
                    log.trace("      Explicit conversion output: {}",r);
                }else if(accessor.contains(".")){
                    parameterType = Class.forName(accessor);
                    r= ConvertUtils.convert(rawValue,parameterType);
                    log.trace("      Explicit conversion output: {}",r);
                }else{
                    String cannonicalName = "java.lang."+accessor;
                    parameterType = Class.forName(cannonicalName);
                    r= ConvertUtils.convert(rawValue,parameterType);
                    log.trace("      Implicit conversion output: {}",r);
                }

                holder.setResult(r);
            }else if(rawValue.equals("null")){
                holder.setResult(null);
            }else{
                if(t instanceof EvaluationContext){
                    pathEvaluation(sentence, (EvaluationContext) t, rawValue);
                }else{
                    log.info("no interpretarion for token {} on current context",rawValue);
                }

           /* if(t instanceof JavaNativeInterfaceContext){

            }

            if(t instanceof EvaluationContext){

            }

            if(t instanceof ApplicationContext){

            }*/

            }


        }
    }



    private void pathEvaluation(ListIterator<String> sentence, EvaluationContext context, String rawValue) throws Exception {
        ContractDescriptor catalog =context.getCatalogValue();
        if(catalog==null){
               context.setResult(rawValue);
        }else{
            FieldDescriptor targetField = catalog.getFieldDescriptor(rawValue);
            if(targetField==null){
                if (context.getResult() instanceof  CatalogOperand){
                    //prepare context for foward propagation into what may resolve as an operation
                    if(context.getParent()!=null && context.getParent() instanceof EvaluationContext){
                        context.setAssignation( ((EvaluationContext)context.getParent()).getAssignation());
                    }
                    context.setResult(new BinaryOperation(context.getResult(),context.getEvaluate(),rawValue));
                }
            }else{
                CatalogEntry targetEntry = context.getEntryValue();
                if(targetEntry==null||!catalog.getDistinguishedName().equals(targetEntry.getCatalogType())){
                    //resolve posible entries
                    CatalogQueryRequestImpl request = new CatalogQueryRequestImpl(FilterDataUtils.newFilterData(),catalog.getDistinguishedName());
                    context.setResult(new CatalogOperand(request,targetField, context.getAssignation()));
                }else{
                    if(targetEntry.getCatalogType().equals(catalog.getDistinguishedName())){
                        Object targetFieldValue = access.getPropertyValue(targetField, targetEntry, null, context.getIntro());
                        if(targetFieldValue==null){
                            getFieldValue(sentence, context, targetField,targetEntry);
                        }else{
                            if(context.getResult()!=null && context.getResult() instanceof  BinaryOperation){
                                BinaryOperation operation = (BinaryOperation) context.getResult();
                                operation.setOperand_2(targetFieldValue);
                            }else{
                                context.setResult(targetFieldValue);
                            }
                        }
                    }else{
                        getFieldValue(sentence, context, targetField,targetEntry);
                    }
                }
            }
            if(sentence.hasNext()){
                pathEvaluation(sentence,context,sentence.next());
            }
        }
    }

    private void getFieldValue(ListIterator<String> sentence, EvaluationContext context, FieldDescriptor targetField, CatalogEntry targetEntry) throws Exception {
        Object obtainedData =  access.getPropertyValue(targetField,targetEntry,null,context.getIntro());
        if(obtainedData!=null){
            if(context.getResult()==null){
                context.setResult(obtainedData);
            }else if (context.getResult() instanceof Operation){
                Operation operation = (Operation) context.getResult();
                operation.appendOperand(obtainedData);
            }
        }else{
            if(sentence.hasNext()&&(targetField.isKey()||context.isNestedPath() )){
                RuntimeContext runtime = context.getRuntimeContext();
                Contract actionRequest= new CatalogReadRequestImpl(targetField.getCatalog(), CatalogDescriptor.CATALOG_ID);
                CatalogDescriptor foreignCatalog  = runtime.getServiceBus().fireEvent(actionRequest, runtime,null);

                EvaluationContext child = new EvaluationContext(context,new PathToken(foreignCatalog,targetField),context.getEvaluate(),context.getEntryValue());
                pathEvaluation(sentence,child,sentence.next());
                context.setResult(child.getResult());
            }else{
                // location , but my current type does not correspond so i just retun null instead
                if(log.isDebugEnabled()){
                    log.debug("unable to generate data for field {}",targetField.getDistinguishedName());
                }
                context.setResult(null);
            }
        }
    }


}
