package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rarl on 26/05/17.
 */
@Singleton
public class PlainTextUserInteractionState implements UserInteractionState {
    @Override
    public boolean execute(Context context) throws Exception {
            //SearchEngineOptimizedDesktopWriterCommand

        return CONTINUE_PROCESSING;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter String");
        String s = br.
        System.out.print("Enter Integer:");
        try{
            int i = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.err.println("Invalid Format!");
        }
    }
}
