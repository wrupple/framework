package com.wrupple.muba.event.domain.reserved;

import java.util.List;

public interface HasResult<R> {
	public <T> T getConvertedResult();

	public R getResult();
	public void setResult(R r);

}
