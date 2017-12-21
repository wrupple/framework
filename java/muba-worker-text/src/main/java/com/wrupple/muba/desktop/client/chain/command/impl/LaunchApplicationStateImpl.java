package com.wrupple.muba.desktop.client.chain.command.impl;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Singleton
public class LaunchApplicationStateImpl implements LaunchApplicationState {

    protected final PlaceController pc;
    protected final DesktopManager dm;
    private final ProcessManager pm;
    private final TransactionalActivityAssembly assembly;
    private final ActivityVegetateEventHandler vegetateHandler;
    protected JsArray<JsProcessTaskDescriptor> overridenProcessSteps;
    /*
        * SERVICES
        */
    private EventBus eventBus;

    // cachuky tuku
    @com.google.inject.Inject
    public TransactionalActivity(ProcessManager pm, DesktopManager dm, PlaceController pc, TransactionalActivityAssembly assembly,
                                 ActivityVegetateEventHandler vegetateHandler) {
        super(dm, pm, pc);
        this.vegetateHandler = vegetateHandler;
        this.assembly = assembly;

    }

    @Inject
    public LaunchApplicationStateImpl(ProcessManager bpm) {
        this.bpm = bpm;
    }

    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {

        HelloView helloView = clientFactory.getHelloView();
        helloView.setName(name);
        helloView.setPresenter(this);
        containerWidget.setWidget(helloView.asWidget());


        DesktopPlace place = (DesktopPlace) pc.getWhere();
        if (!dm.isDesktopyConfigured()) {
            if (recoverFromMissconfiguredDesktop(place)) {
                return;
            }

        }

        JavaScriptObject o = dm.getApplicationItem(place);

        JsApplicationItem applicationItem;
        if (o == null) {
            applicationItem = null;
        } else {
            applicationItem = o.cast();
        }
        getActivityProcess(place, applicationItem, new SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));
    }

    protected boolean recoverFromMissconfiguredDesktop(DesktopPlace place) {
        DesktopPlace newPlace = new DesktopPlace(DesktopManager.RECOVERY_ACTIVITY);
        newPlace.setFoward(place);
        pc.goTo(newPlace);
        return true;
    }

    @Override
    public void getActivityProcess(final DesktopPlace input, JsApplicationItem actd, DataCallback<ActivityProcess> callback) {
        callback.hook(vegetateHandler);
        eventBus.addHandler(VegetateEvent.TYPE, vegetateHandler);
        /*
         * Load transaction data
		 */
        final JsApplicationItem applicationItem;
        if (actd == null) {
            applicationItem = null;
        } else {
            applicationItem = actd.cast();

            JsArrayString scripts = applicationItem.getRequiredScriptsArray();
            JsArrayString sheets = applicationItem.getRequiredStyleSheetsArray();

            if ((scripts != null && scripts.length() > 0) || (sheets != null && sheets.length() > 0)) {
                callback = new ResourceLoadingCallback(dm, callback, scripts, sheets, assembly.getSm(), eventBus);
            }

            final String welcomeProcessId = applicationItem.getWelcomeProcess();
            if (welcomeProcessId != null) {

                callback.hook(new DataCallback<ActivityProcess>() {
                    @Override
                    public void execute() {
                        final StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback = new DataCallback<Process<JavaScriptObject, JavaScriptObject>>() {

                            @Override
                            public void execute() {
                                JsTransactionApplicationContext i = JsTransactionApplicationContext.createObject().cast();
                                StateTransition<JavaScriptObject> o = DataCallback.nullCallback();
                                pm.processSwitch(result, applicationItem.getName(), i, o, result.getContext());

                            }
                        };
                        assembly.loadAndAssembleProcess(welcomeProcessId, transactionInfoCallback);
                    }
                });
            }

        }
        assembly.setApplicationItem(applicationItem);

        if (overridenProcessSteps == null) {
            StateTransition transactionInfoCallback = new ProcessDescriptorCallback(callback);
            String processId = applicationItem.getProcessAsId();
            assembly.loadProcess(processId, transactionInfoCallback);
        } else {
            assembly.assembleActivityProcess(overridenProcessSteps, callback);
        }

    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        ContainerContext context = (ContainerContext) ctx;


        EventBus eventBus = clientFactory.getEventBus();
        PlaceController placeController = clientFactory.getPlaceController();

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        RootPanel.get().add(appWidget);
        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();


        if (first != null) {
            //remove splash screen
            RootLayoutPanel.get().remove(first);
            first = null;
        }

        final ActivityManager activityManager = new ActivityManager(
                activityMapper, eventBus);

        NestedActivityPresenter main = new NestedActivityPresenter(dm);
        main.setStyleName("desktop");

        activityManager.setDisplay(main.getRootTaskPresenter());
        eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeListener(main, toolbar, dm));
        eventBus.addHandler(VegetateEvent.TYPE, toolbar);
        eventBus.addHandler(DesktopProcessEvent.TYPE, toolbar);
        RootLayoutPanel.get().add(main);
        // Fire Event
        eventBus.fireEvent(new DesktopInitializationDoneEvent());
        // Goes to the place represented on URL else default place
        final PlaceHistoryHandler paceHistoryHandler = new PlaceHistoryHandler(historyMapper);
        paceHistoryHandler.register(placeController, eventBus, dm.getDefaultPlace());
        GWT.log("Desktop Loading finished, handling current url history state");
        paceHistoryHandler.handleCurrentHistory();


        Workflow currentPlaceActivityDefinition;


        ApplicationState applicationState = pm.acquireContext(currentPlace, context.getRuntimeContext().getSession());


        postSolutionToApplication(applicationState);


        // when thread returns statistics should be updated (assertion)

        Statistics statistics = (Statistics) applicationState.getEntryValue();


        assertTrue("statistics dont exist", statistics != null);
        assertTrue("statistics not created", statistics.getId() != null);

        //if state changed, next problem should be unsolvable and throw an exception
        List results = wrupple.fireEvent(applicationState, session, null);
/*    if problem where solvable
         statistics= (Statistics) results.get(0);


        assertTrue("statistics not updated",statistics.getCount()!=null);
        assertTrue("statistics not updated",statistics.getCount().longValue()>0);*/



        /*
        In this forth tutorial we will finally look at creating a multi-window text GUI, all based on text. Just like
        the Screen-layer in the previous tutorial was based on the lower-level Terminal layer, the GUI classes we will
        use here are all build upon the Screen interface. Because of this, if you use these classes, you should never
        interact with the underlying Screen that backs the GUI directly, as it might modify the screen in a way the
        GUI isn't aware of.
        The GUI system is designed around a background surface that is usually static, but can have components, and
        multiple windows. The recommended approach it to make all windows modal and not let the user switch between
        windows, but the latter can also be done. Components are added to windows by using a layout manager that
        determines the position of each component.
         */
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = null;

        try {
            /*
            The DefaultTerminalFactory class doesn't provide any helper method for creating a Text GUI, you'll need to
             get a Screen like we did in the previous tutorial and start it so it puts the terminal in private mode.
             */
            screen = terminalFactory.createScreen();
            screen.startScreen();

            /*
            There are a couple of different constructors to MultiWindowTextGUI, we are going to go with the defaults for
            most of these values. The one thing to consider is threading; with the default options, lanterna will use
            the calling thread for all UI operations which mean that you are basically letting the calling thread block
            until the GUI is shut down. There is a separate TextGUIThread implementaiton you can use if you'd like
            Lanterna to create a dedicated UI thread and not lock the caller. Just like with AWT and Swing, you should
            be scheduling any kind of UI operation to always run on the UI thread but lanterna tries to be best-effort
            if you attempt to mutate the GUI from another thread. Another default setting that will be applied is that
            the background of the GUI will be solid blue.
             */
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            /*
            Creating a new window is relatively uncomplicated, you can optionally supply a title for the window
             */
            final Window window = new BasicWindow("My Root Window");

            /*
            The window has no content initially, you need to call setComponent to populate it with something. In this
            case, and quite often in fact, you'll want to use more than one component so we'll create a composite
            'Panel' component that can hold multiple sub-components. This is where we decide what the layout manager
            should be.
             */
            Panel contentPanel = new Panel(new GridLayout(2));

            /**
             * Lanterna contains a number of built-in layout managers, the simplest one being LinearLayout that simply
             * arranges components in either a horizontal or a vertical line. In this tutorial, we'll use the GridLayout
             * which is based on the layout manager with the same name in SWT. In the constructor above we have
             * specified that we want to have a grid with two columns, below we customize the layout further by adding
             * some spacing between the columns.
             */
            GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
            gridLayout.setHorizontalSpacing(3);

            /*
            One of the most basic components is the Label, which simply displays a static text. In the example below,
            we use the layout data field attached to each component to give the layout manager extra hints about how it
            should be placed. Obviously the layout data has to be created from the same layout manager as the container
            is using, otherwise it will be ignored.
             */
            Label title = new Label("This is a label that spans two columns");
            title.setLayoutData(GridLayout.createLayoutData(
                    GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
                    GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
                    true,       // Give the component extra horizontal space if available
                    false,        // Give the component extra vertical space if available
                    2,                  // Horizontal span
                    1));                  // Vertical span
            contentPanel.addComponent(title);

            /*
            Since the grid has two columns, we can do something like this to add components when we don't need to
            customize them any further.
             */
            contentPanel.addComponent(new Label("Text Box (aligned)"));
            contentPanel.addComponent(
                    new TextBox()
                            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

            /*
            Here is an example of customizing the regular text box component so it masks the content and can work for
            password input.
             */
            contentPanel.addComponent(new Label("Password Box (right aligned)"));
            contentPanel.addComponent(
                    new TextBox()
                            .setMask('*')
                            .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER)));

            /*
            While we are not going to demonstrate all components here, here is an example of combo-boxes, one that is
            read-only and one that is editable.
             */
            contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
            List<String> timezonesAsStrings = new ArrayList<String>();
            for (String id : TimeZone.getAvailableIDs()) {
                timezonesAsStrings.add(id);
            }
            ComboBox<String> readOnlyComboBox = new ComboBox<String>(timezonesAsStrings);
            readOnlyComboBox.setReadOnly(true);
            readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
            contentPanel.addComponent(readOnlyComboBox);

            contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
            contentPanel.addComponent(
                    new ComboBox<String>("Item #1", "Item #2", "Item #3", "Item #4")
                            .setReadOnly(false)
                            .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

            /*
            Some user interactions, like buttons, work by registering callback methods. In this example here, we're
            using one of the pre-defined dialogs when the button is triggered.
             */
            contentPanel.addComponent(new Label("Button (centered)"));
            contentPanel.addComponent(new Button("Button", new Runnable() {
                @Override
                public void run() {
                    MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK);
                }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

            /*
            Close off with an empty row and a separator, then a button to close the window
             */
            contentPanel.addComponent(
                    new EmptySpace()
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Separator(Direction.HORIZONTAL)
                            .setLayoutData(
                                    GridLayout.createHorizontallyFilledLayoutData(2)));
            contentPanel.addComponent(
                    new Button("Close", new Runnable() {
                        @Override
                        public void run() {
                            window.close();
                        }
                    }).setLayoutData(
                            GridLayout.createHorizontallyEndAlignedLayoutData(2)));

            /*
            We now have the content panel fully populated with components. A common mistake is to forget to attach it to
            the window, so let's make sure to do that.
             */
            window.setComponent(contentPanel);

            /*
            Now the window is created and fully populated. As discussed above regarding the threading model, we have the
            option to fire off the GUI here and then later on decide when we want to stop it. In order for this to work,
            you need a dedicated UI thread to run all the GUI operations, usually done by passing in a
            SeparateTextGUIThread object when you create the TextGUI. In this tutorial, we are using the conceptually
            simpler SameTextGUIThread, which essentially hijacks the caller thread and uses it as the GUI thread until
            some stop condition is met. The absolutely simplest way to do this is to simply ask lanterna to display the
            window and wait for it to be closed. This will initiate the event loop and make the GUI functional. In the
            "Close" button above, we tied a call to the close() method on the Window object when the button is
            triggered, this will then break the even loop and our call finally returns.
             */
            textGUI.addWindowAndWait(window);

            /*
            When our call has returned, the window is closed and no longer visible. The screen still contains the last
            state the TextGUI left it in, so we can easily add and display another window without any flickering. In
            this case, we want to shut down the whole thing and return to the ordinary prompt. We just need to stop the
            underlying Screen for this, the TextGUI system does not require any additional disassembly.
             */

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return CONTINUE_PROCESSING;
    }

    public static class SetApplicationStateAndContext extends DataCallback<ActivityProcess> {
        ProcessManager pm;
        AcceptsOneWidget panel;
        EventBus eventBus;
        JsApplicationItem applicationItem;

        public SetApplicationStateAndContext(ProcessManager pm, AcceptsOneWidget panel, EventBus eventBus, JsApplicationItem applicationItem) {
            super();
            this.pm = pm;
            this.panel = panel;
            this.eventBus = eventBus;
            this.applicationItem = applicationItem;
        }

        @Override
        public void execute() {
            pm.setCurrentProcess(applicationItem.getProcessAsId());
            pm.contextSwitch(result, applicationItem, panel, eventBus);
        }

    }

    static class ResourceLoadingCallback extends DataCallback<ActivityProcess> {
        final DataCallback<ActivityProcess> wrapped;

        boolean activityProcess = false;
        boolean loadedstyles = false;
        boolean loadedscripts = false;

        public ResourceLoadingCallback(final DesktopManager dm, DataCallback<ActivityProcess> wrapped, final JsArrayString scripts, JsArrayString sheets,
                                       final StorageManager sm, final EventBus eventBus) {
            super();
            this.wrapped = wrapped;
            final RemoteStorageUnit remoteStorage = sm.getRemoteStorageUnit(dm.getCurrentActivityHost());
			/*
			 * LOAD JAVASCRIPT FILES
			 */
            if (scripts == null || scripts.length() == 0) {
                loadedscripts = true;
            } else {
                final ParallelProcess<String, String> loadScripts = new ParallelProcess<String, String>(new State<String, String>() {

                    @Override
                    public void start(final String parameter, final StateTransition<String> onDone, EventBus bus) {
                        JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(dm.getCurrentActivityDomain(), CatalogActionRequest.LOCALE,
                                WruppleDomainJavascript.CATALOG, CatalogActionRequest.READ_ACTION, parameter, "0", null, null);
                        String scriptUrl = remoteStorage.buildServiceUrl(request);

                        ScriptInjector.fromUrl(scriptUrl).setWindow(ScriptInjector.TOP_WINDOW).setCallback(new Callback<Void, Exception>() {
                            public void onFailure(Exception reason) {
                                Window.alert("Script load failed.");
                                onDone.setResultAndFinish(parameter);
                            }

                            public void onSuccess(Void result) {
                                onDone.setResultAndFinish(parameter);
                            }
                        }).inject();
                    }
                }, false, false);

                remoteStorage.assertManifest(new DataCallback<Void>() {

                    @Override
                    public void execute() {
                        loadScripts.start(GWTUtils.asStringList(scripts), new DataCallback<List<String>>() {

                            @Override
                            public void execute() {
                                loadedscripts = true;
                                testAndFinish();
                            }
                        }, eventBus);
                    }
                });

            }
            if (sheets == null || sheets.length() == 0) {
                loadedstyles = true;
            } else {
                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), WruppleDomainStyleSheet.CATALOG, GWTUtils.asStringList(sheets),
                        new DataCallback<List<JsCatalogEntry>>() {

                            @Override
                            public void execute() {
                                loadedstyles = true;
                                testAndFinish();
                                for (JsCatalogEntry js : result) {
                                    StyleInjector.inject(js.getStringValue());
                                }
                            }
                        });

            }
        }

        protected void testAndFinish() {
            if (activityProcess && loadedscripts && loadedstyles) {
                wrapped.setResultAndFinish(result);
            }
        }

        @Override
        public void execute() {
            activityProcess = true;
            testAndFinish();
        }

    }

    class ProcessDescriptorCallback extends DataCallback<List<JsProcessDescriptor>> {

        private DataCallback<ActivityProcess> callback;

        public ProcessDescriptorCallback(DataCallback<ActivityProcess> callback) {
            this.callback = callback;
        }

        @Override
        public void execute() {
            if (result == null || result.isEmpty()) {
                // FIXME process 404
                throw new IllegalArgumentException("Activity Descriptor not found for current activity");
            } else {
                JsProcessDescriptor process = result.get(0);
                assembly.start(process, callback, eventBus);

            }
        }

    }
}
