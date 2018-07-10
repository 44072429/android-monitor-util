package android.ys.com.monitor_util;

public class LinkEventProxy {
	private LinkEventCell node;
	
	protected LinkEventProxy() {
		node = new LinkEventCell();
		node.parent = null;
		node.child = null;
		node.event = null;
	}
	
	protected LinkEventCell getLastChildCell() {
		if (node.child == null) {
			return node;
		}
		LinkEventCell tmpCell = node.child;
		while (true) {
			if (tmpCell.child == null) {
				break;
			}
			tmpCell = tmpCell.child;
		}
		return tmpCell;
	}
	
	protected void addLinkCell(LinkEventCell cell) {
		LinkEventCell parent = node;
		LinkEventCell tmpNode = node.child;
		while (tmpNode != null) {
			parent = tmpNode;
			tmpNode = tmpNode.child;
		}
		
		parent.child = cell;
		cell.parent = parent;
		cell.child = null;
	}
	
	public void addLinkEvent(OnLinkListener listener) {
		LinkEventCell cell = new LinkEventCell();
		cell.event = listener;
		addLinkCell(cell);
	}
	
	public void callBack(Object obj,int operate) {
		LinkEventCell cell = getLastChildCell();
		cell.callBack(obj, operate);
	}
}
