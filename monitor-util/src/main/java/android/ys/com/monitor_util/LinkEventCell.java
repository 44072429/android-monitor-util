package android.ys.com.monitor_util;

public class LinkEventCell {
	protected LinkEventCell parent;
	
	protected LinkEventCell child;
	
	public OnLinkListener event;
	
	public void callBack(Object obj,int operate) {
		if (event != null) {
		    event.callBackEvent(obj,operate);
		}
		if (parent != null) {
			parent.callBack(obj,operate);
		}
	}
}
