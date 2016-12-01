package com.wrupple.muba.bpm.domain;

import java.util.Date;

public interface SocketChannelSession {
	void setExpiration(Date expirationTime);
	Date getExpitarion();
}
