package com.wrupple.muba.bpm.domain;

import java.util.Date;

public interface SocketChannelSession extends SuscriptorSession{
	void setExpiration(Date expirationTime);
	Date getExpitarion();
}
