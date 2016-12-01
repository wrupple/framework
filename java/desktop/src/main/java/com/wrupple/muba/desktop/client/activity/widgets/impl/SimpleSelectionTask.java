package com.wrupple.muba.desktop.client.activity.widgets.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.activity.widgets.SelectionTask;

public class SimpleSelectionTask<O> extends Composite implements
		SelectionTask<O> {
	
	class SelectionHandler implements com.google.gwt.view.client.SelectionChangeEvent.Handler{


		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			List<O> result = new ArrayList<O>();
			List<O> originalList = provider.getList();
			SelectionModel<? super O> selectionModel = list.getSelectionModel();
			for(O entry : originalList){
				if(selectionModel.isSelected(entry)){
					result.add(entry);
				}
			}
			onDone.setResultAndFinish(result);
		}
		
	}

	protected CellTable<O> list;
	private StateTransition<List<O>> onDone;
	private ListDataProvider<O> provider;
	
	public SimpleSelectionTask(Cell<O> cell, SelectionModel<? super O> selectionModel) {
		super();
		provider = new ListDataProvider<O>();
		list = new CellTable<O>();
		list.setWidth("100%");
		list.addColumn(new IdentityColumn<O>(cell));
		provider.addDataDisplay(list);
		SelectionHandler selectionEventManager = new SelectionHandler();
		selectionModel.addSelectionChangeHandler(selectionEventManager);
		list.setSelectionModel(selectionModel);
		ScrollPanel container = new ScrollPanel(list);
		
		initWidget(container);
		
	}


	@Override
	public void start(List<O> parameter, StateTransition<List<O>> onDone,
			EventBus bus) {
		provider.setList(parameter);
		Range range= new Range(0,parameter.size());
		list.setVisibleRangeAndClearData(range, true);
		this.onDone=onDone;
	}

}
