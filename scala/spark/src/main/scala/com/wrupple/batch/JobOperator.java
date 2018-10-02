package com.wrupple.batch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.batch.servicios.Job;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.LegacyModule;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.service.ImplicitEventResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkContext;
import scala.collection.Iterator;

import java.util.*;

public class JobOperator {

    protected static final Logger logger = LogManager.getLogger(JobOperator.class);

    public final ServiceBus bus;
    public final SessionContext session;
    private final Injector injector;


    public JobOperator(String prompt, Collection<Job> miembros, Module configuracion) {

        if (logger.isInfoEnabled()) {
            logger.info("Available jobs:");
            miembros.forEach(job ->{logger.info("\t"+ job.getClass().getCanonicalName());});
        }

        List<Module> userModules = new ArrayList<>();
        Set<Class<? extends Module>> classes= new HashSet<>();
        Iterator<Module> moduleIt;
        Module next;
        for(Job miembro: miembros){
             moduleIt = miembro.modules().iterator();
            //flat map
            while(moduleIt.hasNext()){
                next = moduleIt.next();
                if(classes.contains(next.getClass())){
                    logger.warn("excluding module "+next.getClass().getCanonicalName()+" from "+miembro.getClass().getCanonicalName());
                }else{
                    userModules.add(next);
                    classes.add(next.getClass());
                }
            }
        }

        Module[] coreModules = new Module[]{
                configuracion
                ,
                new ValidationModule()
                ,
                new LegacyModule()
                ,
                new DispatcherModule()
                ,
                new ApplicationModule()
        };


        Module[] applicationModules = Arrays.copyOf(coreModules, userModules.size() + coreModules.length);

        for (int i = 0; i < userModules.size(); i++) {
            applicationModules[i + coreModules.length] = userModules.get(i);
        }

        logger.info("Wiring core dependencies... "+Arrays.toString(applicationModules) /*ServicioDisponibilizacionImpl*/);

        injector = Guice.createInjector(applicationModules);

        bus = injector.getInstance(ServiceBus.class);
        session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM))
        );
        logger.debug("Registering services..." /*ServicioDisponibilizacionImpl*/);

        BroadcastServiceManifest broadcastManifest = injector.getInstance(BroadcastServiceManifest.class);
        bus.getIntentInterpret().registerService(broadcastManifest, injector.getInstance(PublishEvents.class), injector.getInstance(BroadcastInterpret.class));

        ExplicitIntentInterpret greeter = injector.getInstance(ExplicitIntentInterpret.class);
        bus.registerInterpret(prompt, greeter);


        Iterator<Class<ImplicitEventResolver.Registration>> servicios;
        Class<ImplicitEventResolver.Registration> clase;
        ImplicitEventResolver.Registration instancia;
        for( Job miembro : miembros ){
             servicios = miembro.jobs().iterator();
            while(servicios.hasNext()){
                clase = servicios.next();
                instancia = construir(clase);
                registrarServicio(instancia);
            }
        }

    }

    public void registrarServicio(ImplicitEventResolver.Registration resgitro) {
        bus.getIntentInterpret().registerService(resgitro);
    }

    public void close(){
        injector.getInstance(SparkContext.class).stop();
    }

    public <T> T construir(Class<T> clase) {
        return injector.getInstance(clase);
    }

}
