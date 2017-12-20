package com.wrupple.muba.desktop.shared.services.impl;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.shared.services.UrlParser;

import java.io.PrintWriter;
import java.util.List;
import java.util.Stack;

public class UrlParserImpl implements UrlParser {

	@Override
	public ApplicationItem findNextTreeNode(ApplicationItem currentItem, ApplicationItem root) {
		ApplicationItem currentItemParent = getParentValue(currentItem, root);
		List<? extends ApplicationItem> children;
		if (currentItemParent == null) {
			// next child?
			children = currentItem.getChildItemsValues();
			if (children == null) {

			} else {
				currentItem = children.get(0);
				return currentItem;
			}

		} else {
			children = currentItemParent.getChildItemsValues();
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					if (currentItem == children.get(i)) {
						if ((i + 1) < children.size()) {
							// next brother
							return children.get(i + 1);
						} else {
							// item is the last brother of it's parent
							if (getParentValue(currentItemParent, root) == null) {
								// no where to go now
							} else {
								return findNextTreeNode(currentItemParent, root);
							}
						}

					}
				}
			}
		}
		throw new NullPointerException("Unable to determine next activity");
	}

	@Override
	public void getItemActivity(String[] tokens, int firstActivityIndex, ApplicationItem item, ApplicationItem root, StringBuilder builder) {
		builder.append('/');
		// find the token index corresponding to root Item, and print all
		// previous tokens
		String token;
		for (int i = 0; i < firstActivityIndex; i++) {
			token = tokens[i];
			builder.append(token);
			builder.append('/');
		}
		Stack<ApplicationItem> chain = new Stack<ApplicationItem>();
		chain.push(item);
		// find chain of items leading to desired item
		ApplicationItem parent = getParentValue(item, root);
		while (parent != null) {
			chain.push(parent);
			parent = getParentValue(parent, root);
		}

		// print all chain nodes
		for (ApplicationItem chainNode : chain) {
			if (chainNode != root) {
				builder.append(chainNode.getActivity());
				builder.append('/');
			}
		}
	}

	@Override
	public void printItemActivity(String[] tokens, int firstActivityIndex, ApplicationItem item, ApplicationItem root, PrintWriter printer) {
		printer.print('/');
		// find the token index corresponding to root Item, and print all
		// previous tokens
		String token;
		for (int i = 0; i < firstActivityIndex; i++) {
			token = tokens[i];
			printer.print(token);
			printer.print('/');
		}
		Stack<ApplicationItem> chain = new Stack<ApplicationItem>();
		chain.push(item);
		// find chain of items leading to desired item
		ApplicationItem parent = getParentValue(item, root);
		while (parent != null) {
			chain.push(parent);
			parent = getParentValue(parent, root);
		}

		// print all chain nodes
		for (ApplicationItem chainNode : chain) {
			if (chainNode != root) {
				printer.print(chainNode.getActivity());
				printer.print('/');
			}
		}
	}

	private ApplicationItem getParentValue(ApplicationItem currentItem, ApplicationItem root) {
		if (currentItem == root) {
			return null;
		}
		List<? extends ApplicationItem> children = root.getChildItemsValues();
		if (children == null) {
			return null;
		} else {
			ApplicationItem found;
			for (ApplicationItem child : children) {
				if (child == currentItem) {
					return root;
				} else {
					found = getParentValue(currentItem, child);
					if (found != null) {
						return found;
					}
				}
			}
			return null;
		}

	}

	@Override
	public ApplicationItem getActivityItem(String activity, ApplicationItem root) {
		int previous, next, lastIndex = activity.length() - 1;

		if (activity.charAt(0) == '/') {
			previous = 1;
		} else {
			previous = 0;
		}

		ApplicationItem regreso = root;
		List<? extends ApplicationItem> children;
		String current;
		do {

			next = activity.indexOf('/', previous);
			if (next == -1) {
				next = lastIndex;
			}
			current = activity.substring(previous, next);
			previous = next + 1;

			children = regreso.getChildItemsValues();
			for (ApplicationItem child : children) {
				if (current.equals(child.getActivity())) {
					regreso = child;
				}
			}

		} while (next < lastIndex);

		return regreso;
	}

}
