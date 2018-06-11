package com.wrupple.batch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.batch.servicios.Actor;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogReadTransactionImpl;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Core {

    protected static final Logger logger = LogManager.getLogger(CatalogReadTransactionImpl.class);

    public final ServiceBus bus;
    public final SessionContext session;
    private final Injector injector;

    private final Module[] coreModules = new Module[]{
            new ValidationModule()
                ,
            new LegacyModule()
                ,
            new DispatcherModule()
                ,
            new ApplicationModule()};

    public Core(String prompt, Collection<Actor> miembros, Module configuracion) {

        if (logger.isInfoEnabled()) {
            logger.info("Actors in service:");
            miembros.forEach(actor->{logger.info(""+actor.getClass().getCanonicalName());});
        }

        List<Module> userModules = new ArrayList<>();
        Iterator<Module> moduleIt;
        Module next;
        for(Actor miembro: miembros){
             moduleIt = miembro.modules().iterator();
            //flat map
            while(moduleIt.hasNext()){
                next = moduleIt.next();
                userModules.add(next);
            }
        }

        
        Module[] applicationModules = Arrays.copyOf(coreModules, userModules.size() + coreModules.length+1);

        applicationModules[0] = configuracion;

        for (int i = 0; i < userModules.size(); i++) {
            applicationModules[i + coreModules.length+1] = userModules.get(i);
        }

        injector = Guice.createInjector(applicationModules);

        bus = injector.getInstance(ServiceBus.class);
        session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM))
        );


        BroadcastServiceManifest broadcastManifest = injector.getInstance(BroadcastServiceManifest.class);
        bus.getIntentInterpret().registerService(broadcastManifest, injector.getInstance(PublishEvents.class), injector.getInstance(BroadcastInterpret.class));

        ExplicitIntentInterpret greeter = injector.getInstance(ExplicitIntentInterpret.class);
        bus.registerInterpret(prompt, greeter);


        logger.debug("[Service Registration]" /*ServicioDisponibilizacionImpl*/);
        Iterator<Class<ImplicitEventResolver.Registration>> servicios;
        Class<ImplicitEventResolver.Registration> clase;
        ImplicitEventResolver.Registration instancia;
        for( Actor miembro : miembros ){
             servicios = miembro.services().iterator();
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
