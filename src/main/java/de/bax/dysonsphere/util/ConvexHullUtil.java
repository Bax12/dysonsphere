package de.bax.dysonsphere.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

//credits go to chatgpt, shame on me...
public class ConvexHullUtil {

    // Function to compute the convex hull using the Graham scan algorithm
    public static List<Point2D> computeConvexHull(List<Point2D> points) {
        if (points.size() < 3) throw new IllegalArgumentException("At least 3 points required");

        // Find the point with the lowest y-coordinate (or the leftmost in case of tie)
        Point2D p0 = Collections.min(points, (p1, p2) -> {
            if (p1.getY() != p2.getY()) return Double.compare(p1.getY(), p2.getY());
            else return Double.compare(p1.getX(), p2.getX());
        });

        // Sort the points by polar angle with p0
        points.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
            double angle2 = Math.atan2(p2.getY() - p0.getY(), p2.getX() - p0.getX());
            return Double.compare(angle1, angle2);
        });

        Stack<Point2D> stack = new Stack<>();
        stack.push(points.get(0));
        stack.push(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            Point2D top = stack.pop();
            while (!stack.isEmpty() && orientation(stack.peek(), top, points.get(i)) != 2) {
                top = stack.pop();
            }
            stack.push(top);
            stack.push(points.get(i));
        }

        return new ArrayList<>(stack);
    }

    // Function to find the orientation of an ordered triplet (p, q, r)
    // The function returns:
    // 0 -> p, q and r are collinear
    // 1 -> Clockwise
    // 2 -> Counterclockwise
    private static int orientation(Point2D p, Point2D q, Point2D r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                     (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (val == 0) return 0;  // collinear
        return (val > 0) ? 1 : 2; // clockwise or counterclockwise
    }

    // Function to check if a point is inside a convex polygon
    public static boolean isInsideConvexPolygon(List<Point2D> polygon, Point2D p) {
        int n = polygon.size();
        if (n < 3) return false;  // A polygon must have at least 3 vertices

        // Iterate through each edge of the polygon
        for (int i = 0; i < n; i++) {
            Point2D a = polygon.get(i);
            Point2D b = polygon.get((i + 1) % n);
            if (orientation(a, b, p) != 2) {
                return false;  // If the point is not on the left side of this edge, it's outside
            }
        }
        return true;  // Point is inside the convex polygon
    }

        // Function to find the shortest vector from a point outside the convex hull to the hull
        public static Point2D shortestVectorToHull(List<Point2D> hull, Point2D p) {
            Point2D closestPoint = null;
            double minDistSquared = Double.MAX_VALUE;
    
            for (int i = 0; i < hull.size(); i++) {
                Point2D a = hull.get(i);
                Point2D b = hull.get((i + 1) % hull.size());
    
                // Compute the closest point on segment (a, b) to point p
                Point2D closest = closestPointOnSegment(a, b, p);
                double distSquared = p.distanceSq(closest);
    
                if (distSquared < minDistSquared) {
                    minDistSquared = distSquared;
                    closestPoint = closest;
                }
            }
    
            return new Point2D.Double(closestPoint.getX() - p.getX(), closestPoint.getY() - p.getY());
        }
    
        // Helper function to compute the closest point on a line segment to a point
        private static Point2D closestPointOnSegment(Point2D a, Point2D b, Point2D p) {
            double ax = a.getX(), ay = a.getY();
            double bx = b.getX(), by = b.getY();
            double px = p.getX(), py = p.getY();
    
            double abx = bx - ax;
            double aby = by - ay;
            double apx = px - ax;
            double apy = py - ay;
    
            double ab2 = abx * abx + aby * aby;
            double ap_ab = apx * abx + apy * aby;
            double t = ap_ab / ab2;
    
            if (t < 0.0) t = 0.0;
            else if (t > 1.0) t = 1.0;
    
            return new Point2D.Double(ax + t * abx, ay + t * aby);
        }

}
