package com.wrupple.muba.worker.domain;

import java.util.Date;

public interface SocketChannelSession {
	void setExpiration(Date expirationTime);
	Date getExpitarion();
}
