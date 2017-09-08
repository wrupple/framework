package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Arrays;
import java.util.List;
   /*
    Av ejercito del trabajo s/n san pedro barrientos, tlalnepantla de baz edo mex. 5to piso. Maricela Escalón.
     */

/** Hay gente biligue y luego esta la que puede cambiar entre eclipse e intelliJ sin confudir los shortcuts
 * Created by rarl on 26/05/17.
 */
@Singleton
public class PlainTextUserInteractionState implements UserInteractionState {

    private final SolverCatalogPlugin plugin;

    @Inject
    public PlainTextUserInteractionState(SolverCatalogPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
            //SearchEngineOptimizedDesktopWriterCommand
        ApplicationContext context = (ApplicationContext) ctx;
        List<VariableDescriptor> variables = context.getSolutionVariables();
        PrintWriter out = context.getRuntimeContext().getEventBus().getOutputWriter();
        InputStream input = context.getRuntimeContext().getEventBus().getInput();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        CatalogDescriptor catalog = context.getSolutionDescriptor();
        out.println(catalog.getName());
        FieldDescriptor variableField;
        String userInput;
        for(VariableDescriptor variable : variables){
            variableField = variable.getField();
            out.print(variableField.getName()==null? variableField.getFieldId() : variableField.getName());
            out.println(':');
            userInput = in.readLine();
            plugin.getSolver().assignVariableValue(variable,userInput);
        }

        return CONTINUE_PROCESSING;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter String");
        String s = Arrays.toString(br.lines().toArray());
        System.out.print("Enter Integer:");
        try{
            int i = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.err.println("Invalid Format!");
        }
    }
}
