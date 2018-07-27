package multithreaded;

import java.awt.geom.*;


/**
 * A ball that moves and bounces off the edges of a rectangle.
 * @author lenovo
 *
 */

public class Ball {
	private static final int XSIZE = 15;
	private static final int YSIZE = 15;
	private double x = 0;
	private double y = 0;
	private double dx = 1;
	private double dy = 1;
	
	/**
	 * Moves the ball to the next position, reversing direction if it hits one of the edges.
	 */
	public void move(Rectangle2D bounds) {
		this.x += this.dx;
		this.y += this.dy;
		if (this.x < bounds.getMinX()) {
			this.x = bounds.getMinX();
			this.dx = -this.dx;
		}
		if (this.x + XSIZE >= bounds.getMaxX()) {
			this.x = bounds.getMaxX() - XSIZE;
			this.dx = -this.dx;
		}
		if (this.y < bounds.getMinY()) {
			this.y = bounds.getMinY();
			this.dy = -this.dy;
		}
		if (this.y + YSIZE >= bounds.getMaxY()) {
			this.y = bounds.getMaxY() - YSIZE;
			this.dy = -this.dy;
		}
	}
	
	/**
	 * Gets the shape of the ball at its current position.
	 * @return
	 */
	public Ellipse2D getShape() {
		return new Ellipse2D.Double(this.x, this.y, XSIZE, YSIZE);
	}
	
	
}
