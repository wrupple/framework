package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.BinaryOperation;
import com.wrupple.muba.event.domain.impl.CatalogOperand;
import com.wrupple.muba.event.domain.impl.CatalogQueryRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

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
                    catalogEvaluation(sentence, (EvaluationContext) t, rawValue);
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



    private void catalogEvaluation(ListIterator<String> sentence, EvaluationContext context, String rawValue) throws Exception {
        ContractDescriptor catalog =context.getCatalog();
        if(catalog==null){
               context.setResult(rawValue);
        }else{
            FieldDescriptor targetField = catalog.getFieldDescriptor(rawValue);
            if(targetField==null){
                //operator ????

                context.setResult(new BinaryOperation(context.getResult(),targetField,rawValue));
            }else{
                CatalogEntry targetEntry = context.getEntryValue();
                if(targetEntry==null&& context.getResult() instanceof CatalogOperand){
                    CatalogOperand operand  = (CatalogOperand) context.getResult();

                    //resolve posible entries
                    CatalogQueryRequestImpl request = new CatalogQueryRequestImpl(FilterDataUtils.newFilterData(),operand.getTargetField().getCatalog());
                    context.setResult(new CatalogOperand(request,targetField));
                }else{
                    if(targetEntry.getCatalogType().equals(catalog.getDistinguishedName())){
                        Object targetFieldValue = access.getPropertyValue(targetField, targetEntry, null, context.getIntro());
                        if(targetFieldValue==null){
                            evaluateCatalogField(sentence, context, targetField,targetEntry);
                        }else{
                            if(context.getResult()!=null && context.getResult() instanceof  BinaryOperation){
                                BinaryOperation operation = (BinaryOperation) context.getResult();
                                operation.setOperand_2(targetFieldValue);
                            }else{
                                context.setResult(targetFieldValue);
                            }
                        }
                    }else{
                        // TODO my subject's data type does not correspond to this iteration's working type... soo......?
                        evaluateCatalogField(sentence, context, targetField,targetEntry);


                    }
                }
            }
            if(sentence.hasNext()){
                catalogEvaluation(sentence,context,sentence.next());
            }
        }
    }

    private void evaluateCatalogField(ListIterator<String> sentence, EvaluationContext context, FieldDescriptor targetField,CatalogEntry targetEntry) throws Exception {
        Object obtainedData =  access.getPropertyValue(targetField,targetEntry,null,context.getIntro());
        if(obtainedData!=null){
            context.setResult(obtainedData);
        }else{
            if(sentence.hasNext()&&(targetField.isKey()||context.isNestedPath() )){
                RuntimeContext runtime = context.getRuntimeContext();
                Contract actionRequest= new CatalogReadRequestImpl(targetField.getCatalog(), CatalogDescriptor.CATALOG_ID);
                CatalogDescriptor foreignCatalog  = runtime.getServiceBus().fireEvent(actionRequest, runtime,null);

                EvaluationContext child = new EvaluationContext(context,foreignCatalog,targetField,context.getEntryValue());
                catalogEvaluation(sentence,child,sentence.next());
            }else{
                // location , but my current type does not correspond so i just retun null instead
                if(log.isDebugEnabled()){
                    log.debug("unable to generate data for field {}",targetField.getDistinguishedName());
                }
                context.setResult(null);
            }
        }
    }

    private Object getFieldValue(EvaluationContext context, CatalogEntry targetEntry) throws ReflectiveOperationException {
        FieldDescriptor workingField = context.getEvaluate();
        Object returnValue = targetEntry;
        if(context.getParent()==null){
            returnValue= access.getPropertyValue(workingField,targetEntry,null,context.getIntro());
        }else {
            Stack<EvaluationContext> xpath = new Stack<>();
            xpath.push(context);
            EvaluationContext ancestor =context;

                while (ancestor.getParent() != null && ancestor.getParent() instanceof EvaluationContext) {
                    ancestor = (EvaluationContext) ancestor.getParent();
                    xpath.push(ancestor);

                }

                for(EvaluationContext pathElement : xpath){
                    workingField = pathElement.getEvaluate();
                    returnValue = access.getPropertyValue(workingField, (CatalogEntry) returnValue,null,context.getIntro());

                }


        }

        return returnValue;

    }


}
