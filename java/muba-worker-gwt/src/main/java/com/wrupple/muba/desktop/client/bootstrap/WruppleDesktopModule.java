package com.wrupple.muba.desktop.client.bootstrap;

import com.google.gwt.inject.client.GinModule;
import com.wrupple.muba.desktop.client.activity.process.state.LoadMapsApi;
import com.wrupple.muba.desktop.client.activity.process.state.PaymentSourceTokenApi;
import com.wrupple.muba.desktop.client.activity.process.state.impl.LoadGraphaelSVGAPI;
import com.wrupple.muba.desktop.client.activity.process.state.impl.ReCAPTCHALoader;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleAggregateCanvasDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.WruppleIndexedPointMapWidget;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.GooglePointMapEditorImpl;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.LongitudeCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CAPTCHACellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.LatitudeCellProvider;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.AggregateCanvasRendererMap;
import com.wrupple.muba.desktop.client.services.presentation.impl.PieChartGroupRenderService;
import com.wrupple.muba.worker.client.services.BPMModule;

public interface WruppleDesktopModule extends BPMModule,GinModule {
	/*
	 * Api Loaders & Startup Loaders
	 */
	StartUserHeartBeat getUserHeartBeatStarter();
	
	LoadMapsApi mapsApi();
	
	LoadGraphaelSVGAPI svgApi();
	
	ReCAPTCHALoader captchaApi();
	
	PaymentSourceTokenApi paymentApi();
	
	
	/*
	 * Activities
	 */

	
	/*
	 * Aid Providers
	 */
	
	/*
	 * Service Maps
	 */
	AggregateCanvasRendererMap aggregateCanvasRenderers();
	
	/*
	 * Browsers
	 */
	WruppleAggregateCanvasDataWidget aggregateCanvasBrowser();
	
	WruppleIndexedPointMapWidget pointMapBrowser();
	
	/*
	 * Editors
	 */
	GooglePointMapEditorImpl mapEditor();
	
	/*
	 * Aggregate Canvas Renderers
	 */
	PieChartGroupRenderService pieChartRenderer();

	/*
	 * Cell Providers
	 */
	
	LatitudeCellProvider latitudeProvider();

	LongitudeCellProvider longitudeProvider();
	
	CAPTCHACellProvider captchaCell();
	
}
