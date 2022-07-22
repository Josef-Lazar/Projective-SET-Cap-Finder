class BinaryNode {
  
  boolean x;
  boolean y;
  int index;
  boolean hasChildren;
  boolean mouseOnMe;
  boolean selected;
  int excludes;
  BinaryNode[] children;
  
  BinaryNode(int n, int index_) {
    assert 0 <= n && n < 4;
    x = n < 2 ? false : true;
    y = n % 2 == 0 ? false : true;
    index = index_;
    hasChildren = false;
    mouseOnMe = false;
    selected = false;
    excludes = 0;
  }
  
  BinaryNode(boolean x_, boolean y_, int index_) {
    x = x_;
    y = y_;
    index = index_;
    hasChildren = false;
    mouseOnMe = false;
    selected = false;
    excludes = 0;
  }
  
  void makeChildren() {
    if (!hasChildren) {
      hasChildren = true;
      selected = false;
      excludes = 0;
      children = new BinaryNode[4];
      children[0] = new BinaryNode(false, false, 4 * index);
      children[1] = new BinaryNode(false, true, 4 * index + 1);
      children[2] = new BinaryNode(true, false, 4 * index + 2);
      children[3] = new BinaryNode(true, true, 4 * index + 3);
    } else {
      for (int i = 0; i < children.length; i++) {
        children[i].makeChildren();
      }
    }
  }
  
  void killChildren() {
    if (!hasGrandChildren()) {
      children = null;
      hasChildren = false;
    } else {
      children[0].killChildren();
      children[1].killChildren();
      children[2].killChildren();
      children[3].killChildren();
    }
  }
  
  
  void display(int x, int y, int w, int h, int s, int sw) {
    stroke(s);
    strokeWeight(sw);
    noFill();
    if (hasChildren) {
      line(x, y + w / 2, x + w, y + h / 2);
      line(x + h / 2, y, x + w / 2, y + h);
      children[0].display(x, y, ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5)), ceil(0.6 * sw));
      children[1].display(ceil(x + w / 2), y, ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5)), ceil(0.6 * sw));
      children[2].display(x, ceil(y + h / 2), ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5)), ceil(0.6 * sw));
      children[3].display(ceil(x + w / 2), ceil(y + h / 2), ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5)), ceil(0.6 * sw));
    } else {
      if (mouseIn(x, y, w, h)) {
        mouseOnMe = true;
        if (excludes == 0 && index != 0) {
          fill(255, mp ? 100 : 50);
          noStroke();
          rect(x, y, w, h);
          cursor(HAND);
        } else {
          cursor(ARROW);
        }
      } else {
        mouseOnMe = false;
      }
      if (selected) {
        fill(255, 0, 0);
        noStroke();
        ellipse(x + w / 2, y + h / 2, 0.75 * min(w, h), 0.75 * min(w, h));
      }
      if (excludes > 0 && index != 0) {
        textSize(ceil(min(w, h) / 1.5));
        fill(255);
        text(excludes, x + w / 2 - textWidth(excludes + "") / 2, y + 0.75 * h);
      }
      if (displayDecimalOrderNumbers && min(w, h) / 4 > 5) { //checks if it should display the numbers and if the boxes are big enough to be visible
        textSize(ceil(min(w, h) / 4));
        fill(255);
        text(index, x + 0.1 * w, y + 0.3 * h);
      }
      if (displayBinaryOrderNumbers && min(w, h) / 4 > 6) {
        boolean[] bitStr = config.intToBitStr(index);
        String str = bitStrToStr(bitStr);
        textSize(ceil(min(w, h) / 6));
        fill(255);
        text(str, x + 0.1 * w, y + 0.2 * h);
      }
      if (index == 0) {
        stroke(200);
        strokeWeight(sw);
        line(x, y, x + w, y + h);
        line(x, y + h, x + w, y);
      }
    }
  }
  
  void display(int x, int y, int w, int h) {
    display(x, y, w, h, 200, (w + h) / 200);
  }
  
  
  boolean hasGrandChildren() {
    if (!hasChildren) {
      return false;
    } else if (children[0].getHasChildren() && children[1].getHasChildren() && children[2].getHasChildren() && children[3].getHasChildren()) {
      return true;
    } else if (!children[0].getHasChildren() && !children[1].getHasChildren() && !children[2].getHasChildren() && !children[3].getHasChildren()) {
      return false;
    } else { //crashes if some children have children and some don't (technically the node would have grandchildren - which is why it returns true - but it means there's a problem in the code)
      assert false;
      return true;
    }
  }
  
  boolean getHasChildren() {
    return hasChildren;
  }
  
  boolean getX() {
    return x;
  }
  
  boolean getY() {
    return y;
  }
  
  boolean[] getBitStrXY() {
    boolean[] xy = {x, y};
    return xy;
  }
  
  int getIntXY() {
    int xInt = x ? 2 : 0;
    int yInt = y ? 1 : 0;
    int xy = xInt + yInt;
    return xy;
  }
  
  int getIndex() {
    return index;
  }
  
  boolean getMouseOnMe() {
    return mouseOnMe;
  }
  
  boolean getSelected() {
    return selected;
  }
  
  void invertSelection() {
    selected = !selected;
  }
  
  void incrementExcludes() {
    excludes++;
  }
  
  void decrementExcludes() {
    excludes--;
  }
  
  void setExcludes(int excludes_) {
    excludes = excludes_;
  }
  
  int getExcludes() {
    return excludes;
  }
  
  BinaryNode[] getChildren() {
    if (hasChildren) {
      return children;
    } else {
      return null;
    }
  }
  
  BinaryNode getChild1() {
    if (hasChildren) {
      return children[0];
    } else {
      return null;
    }
  }
  
  BinaryNode getChild2() {
    if (hasChildren) {
      return children[1];
    } else {
      return null;
    }
  }
  
  BinaryNode getChild3() {
    if (hasChildren) {
      return children[2];
    } else {
      return null;
    }
  }
  
  BinaryNode getChild4() {
    if (hasChildren) {
      return children[3];
    } else {
      return null;
    }
  }
  
  void setSelected(boolean selected_) {
    selected = selected_;
  }
  
}
