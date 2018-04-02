package com.zero.lionro.maps.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;


import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.PoikeywordsearchActivityBinding;
import com.zero.lionro.maps.presenter.impl.PoilDSearchPresenter;
import com.zero.lionro.maps.ui.view.IPoilDSearchView;
import com.zero.lionro.maps.ui.view.MyAutoCompleteTextView;


public class PoiKeywordSearchActivity extends FragmentActivity implements IPoilDSearchView{

	private PoikeywordsearchActivityBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.poikeywordsearch_activity);
		PoilDSearchPresenter presenter = new PoilDSearchPresenter(this, this);


	}


	@Override
	public Button getButtonSearch() {
		return binding.searchButton;
	}


	@Override
	public MyAutoCompleteTextView getMoreInfo() {
		return binding.keyWord;
	}

}
