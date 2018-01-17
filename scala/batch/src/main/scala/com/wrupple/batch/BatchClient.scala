package com.wrupple.batch

import com.google.inject.name.Names
import com.google.inject.{Guice, Injector, Key, Module}
import com.wrupple.batch.service.{BatchJobInterpret, BatchJobRunner}
import com.wrupple.muba.ValidationModule
import com.wrupple.muba.catalogs._
import com.wrupple.muba.catalogs.domain.{CatalogActionFilterManifest, CatalogIntentListenerManifest, CatalogServiceManifest}
import com.wrupple.muba.catalogs.server.chain.CatalogEngine
import com.wrupple.muba.catalogs.server.chain.command._
import com.wrupple.muba.event.domain.{BroadcastServiceManifest, Constraint, SessionContext}
import com.wrupple.muba.event.server.ExplicitIntentInterpret
import com.wrupple.muba.event.server.chain.PublishEvents
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret
import com.wrupple.muba.event.server.service.impl.LambdaModule
import com.wrupple.muba.event.{ApplicationModule, EventBus}

import scala.collection.JavaConverters._

class BatchClient(val userConfig: Module) {

  val injector: Injector = Guice.createInjector(Array(userConfig
    ,
    new BatchModule
    ,
    new BusinessModule
    ,
    new ConstraintSolverModule
    ,
    new SolverModule
    ,
    new SQLModule
    ,
    new HSQLDBModule
    ,
    new JDBCModule
    ,
    new ValidationModule
    ,
    new SingleUserModule
    ,
    new CatalogModule
    ,
    new LambdaModule
    ,
    new ApplicationModule).toList.asJava)

  val event = injector.getInstance(classOf[EventBus]);
  val session = injector.getInstance(Key.get(classOf[SessionContext], Names.named(SessionContext.SYSTEM)))
  val bpm: ProcessManager = injector.getInstance(classOf[ProcessManager])

  private val solver = injector.getInstance(classOf[Solver]);

  registerServices(event)

  event.registerInterpret(Constraint.EVALUATING_VARIABLE, injector.getInstance(classOf[ChocoInterpret]))
  solver.register(injector.getInstance(classOf[ChocoRunner]))

  event.registerInterpret(BATCH, injector.getInstance(classOf[BatchJobInterpret]))
  solver.register(injector.getInstance(classOf[BatchJobRunner]))


  val greeter: ExplicitIntentInterpret = injector.getInstance(classOf[ExplicitIntentInterpret])

  def setHome(home: Workflow): Unit = {
    event.registerInterpret(home.getDistinguishedName, greeter)

  }

  protected def registerServices(switchs: EventBus): Unit = {
    /*
		 Catalog
		 */
    val catalogServiceManifest = injector.getInstance(classOf[CatalogServiceManifest])
    switchs.getIntentInterpret.registerService(catalogServiceManifest, injector.getInstance(classOf[CatalogEngine]), injector.getInstance(classOf[CatalogRequestInterpret]))

    val preService = injector.getInstance(classOf[CatalogActionFilterManifest])
    switchs.getIntentInterpret.registerService(preService, injector.getInstance(classOf[CatalogActionFilterEngine]), injector.getInstance(classOf[CatalogActionFilterInterpret]))

    val listenerManifest = injector.getInstance(classOf[CatalogIntentListenerManifest])
    switchs.getIntentInterpret.registerService(listenerManifest, injector.getInstance(classOf[CatalogEventHandler]), injector.getInstance(classOf[CatalogEventInterpret]))

    /*
     Vegetate
     */

    val broadcastManifest = injector.getInstance(classOf[BroadcastServiceManifest])
    switchs.getIntentInterpret.registerService(broadcastManifest, injector.getInstance(classOf[PublishEvents]), injector.getInstance(classOf[BroadcastInterpret]))

    /*
     BPM
     */

    val bpm = injector.getInstance(classOf[BusinessServiceManifest])

    switchs.getIntentInterpret.registerService(bpm, injector.getInstance(classOf[BusinessEngine]), injector.getInstance(classOf[BusinessRequestInterpret]))

    switchs.getIntentInterpret.registerService(injector.getInstance(classOf[IntentResolverServiceManifest]), injector.getInstance(classOf[IntentResolverEngine]), injector.getInstance(classOf[IntentResolverRequestInterpret]))


    /*
      Solver
     */

    switchs.getIntentInterpret.registerService(injector.getInstance(classOf[SolverServiceManifest]), injector.getInstance(classOf[SolverEngine]), injector.getInstance(classOf[ActivityRequestInterpret]))
  }


}
