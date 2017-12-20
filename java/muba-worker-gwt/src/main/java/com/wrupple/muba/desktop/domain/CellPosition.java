package com.wrupple.muba.desktop.domain;

import com.google.gwt.touch.client.Point;

public class CellPosition {
	Point origin;
	double width;
	double height;

	public CellPosition(double x, double y, double width, double height) {
		super();
		origin = new Point(x, y);
		this.width = width;
		this.height = height;
	}

	public CellPosition(CellPosition copy) {
		this(copy.origin.getX(), copy.origin.getY(), copy.width, copy.height);
	}

	public double getX() {
		return origin.getX();
	}

	public double getY() {
		return origin.getY();
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public CellPosition scalar(double factor) {
		return new CellPosition(getX() * factor, getY() * factor, width * factor, height * factor);
	}

	@Override
	public String toString() {
		return "CellPosition [origin=" + origin + ", width=" + width + ", height=" + height + "]";
	}

}