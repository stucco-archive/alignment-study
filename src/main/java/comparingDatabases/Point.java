package alignmentStudy;

import java.util.*;

class Point implements Comparator<Point>        {
  
                 double x;
                 double y;
                 Point() {};
 
                 Point(double x, double y)       {
                         this.x = x;
                         this.y = y;
                 }
                 public int compare(Point one, Point two)        {
                         return Double.compare(one.x, two.x);
                 }
}
