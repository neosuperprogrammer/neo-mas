package com.tionsoft.mas.tas.protocol.codec.decoder;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.*;

public interface BodyDecoder {
	public TasBean decode(TasBean bean,PlatformHeader platformHeader);
}
