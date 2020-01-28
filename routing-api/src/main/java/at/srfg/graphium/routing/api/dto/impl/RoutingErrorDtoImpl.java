package at.srfg.graphium.routing.api.dto.impl;

public class RoutingErrorDtoImpl {

	private String msg;
	
	public RoutingErrorDtoImpl(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
