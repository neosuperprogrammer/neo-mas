package com.tionsoft.mas.tas.pushlet;

import com.tionsoft.mas.tas.taslet.TasPushResponse;
import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.mas.tas.taslet.TasletException;

public interface Pushlet {

	public TasPushResponse execute(TasRequest request, TasResponse response) throws TasletException;
}
