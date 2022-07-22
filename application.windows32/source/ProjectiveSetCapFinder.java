import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ProjectiveSetCapFinder extends PApplet {

boolean mp = false;
boolean mr = false;
boolean displayDecimalOrderNumbers = false;
boolean displayBinaryOrderNumbers = true;
boolean omitFirstBit = false;
int dim = 4;
boolean inConfig = false;
boolean inDimUpButton = false;
boolean inDimDownButton = false;
boolean inClearButton = false;
boolean inCompleteButton = false;
boolean inRandomButton = false;

NodeConfiguration config = new NodeConfiguration(6, 25, 25, 800, 800);

public void setup() {
  
  background(0);
  frameRate(30);
}

public void draw() {
  background(0);
  displayDimensionControl(900, 100);
  displayClearButton(900, 200);
  displayCompleteButton(900, 300);
  displayRandomButton(900, 400);
  displayStatisticsButton(1200, 100);
  config.display();
  
  //sets cursor to arrow icon when it leaves the node configuration
  if (!inConfig && config.getMouseOnMe()) {
    inConfig = true;
  } else if (inConfig && !config.getMouseOnMe()) {
    cursor(ARROW);
    inConfig = false;
  }
}


public void displayDimensionControl(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w1 = PApplet.parseInt(textWidth("Dimension") + 30);
  int w2 = PApplet.parseInt(textWidth(config.getDimension() + "")) + 30;
  rect(x, y, w1, 60); //box that says "Dimension"
  rect(x + w1, y, w2, 60); //box with number of dimensions
  if (mouseIn(x + w1, y - 25, w2, 25) && config.getDimension() < 14) {
    fill(255, mp ? 100 : 50);
    if (!inDimUpButton) {
      inDimUpButton = true;
      cursor(HAND);
    }
    if (mr) {
      config = config.increment();
      mr = false;
    }
  } else if (inDimUpButton) {
    inDimUpButton = false;
    cursor(ARROW);
  }
  rect(x + w1, y - 25, w2, 25); //box with arrow up
  fill(0);
  if (mouseIn(x + w1, y + 60, w2, 25) && config.getDimension() > 0) {
    fill(255, mp ? 100 : 50);
    if (!inDimDownButton) {
      inDimDownButton = true;
      cursor(HAND);
    }
    if (mr) {
      config = config.decrement();
      mr = false;
    }
  } else if (inDimDownButton) {
    inDimDownButton = false;
    cursor(ARROW);
  }
  rect(x + w1, y + 60, w2, 25); //box with arrow down
  stroke(config.getDimension() < 14 ? 255 : 100);
  line(x + w1 + w2 / 2 - 5, y - 10, x + w1 + w2 / 2, y - 15);
  line(x + w1 + w2 / 2, y - 15, x + w1 + w2 / 2 + 5, y - 10);
  stroke(config.getDimension() > 0 ? 255 : 100);
  line(x + w1 + w2 / 2 - 5, y + 70, x + w1 + w2 / 2, y + 75);
  line(x + w1 + w2 / 2, y + 75, x + w1 + w2 / 2 + 5, y + 70);
  fill(255);
  text("Dimension", x + 15, y + 40);
  text(config.getDimension(), x + 15 + w1, y + 40);
}


public void displayClearButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = PApplet.parseInt(textWidth("Clear") + 30);
  if (mouseIn(x, y, w, 60)) {
    fill(255, mp ? 100 : 50);
    if (!inClearButton) {
      inClearButton = true;
      cursor(HAND);
    }
    if (mr) {
      config.clear();
      mr = false;
    }
  } else if (inClearButton) {
    inClearButton = false;
    cursor(ARROW);
  }
  rect(x, y, w, 60);
  fill(255);
  text("Clear", x + 15, y + 40);
}


public void displayCompleteButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = PApplet.parseInt(textWidth("Complete") + 30);
  if (mouseIn(x, y, w, 60)) {
    fill(255, mp ? 100 : 50);
    if (!inCompleteButton) {
      inCompleteButton = true;
      cursor(HAND);
    }
    if (mr) {
      config.complete();
      mr = false;
    }
  } else if (inCompleteButton) {
    inCompleteButton = false;
    cursor(ARROW);
  }
  rect(x, y, w, 60);
  fill(255);
  text("Complete", x + 15, y + 40);
}


public void displayRandomButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = PApplet.parseInt(textWidth("Random") + 30);
  if (mouseIn(x, y, w, 60)) {
    fill(255, mp ? 100 : 50);
    if (!inRandomButton) {
      inRandomButton = true;
      cursor(HAND);
    }
    if (mr) {
      config.clear();
      config.complete();
      mr = false;
    }
  } else if (inRandomButton) {
    inRandomButton = false;
    cursor(ARROW);
  }
  rect(x, y, w, 60);
  fill(255);
  text("Random", x + 15, y + 40);
}


public void displayStatisticsButton(int x, int y) {
  int w1 = 0;
  int w2 = 0;
  IntList available = config.availableNodes();
  int totalExcludes = 0;
  ArrayList<PVector> excludes = new ArrayList<PVector>(); //each PVector has as x an exclude number, and as y it has how many times that exclude nubmer occures
  textSize(15);
  for (int i = 1; i < config.nodes.length; i++) {
    int excludeValue = config.nodes[i].getExcludes();
    if (excludeValue != 0) {
      totalExcludes += excludeValue;
      boolean alreadyInExcludes = false;
      for (int exclude = 0; exclude < excludes.size(); exclude++) {
        if (excludeValue == excludes.get(exclude).x) {
          alreadyInExcludes = true;
          excludes.get(exclude).y++;
          w2 = max(w2, PApplet.parseInt(textWidth(PApplet.parseInt(excludes.get(exclude).y) + "")) + 30);
        }
      }
      if (!alreadyInExcludes) {
        PVector exclude = new PVector(excludeValue, 1);
        excludes.add(exclude);
        w1 = max(w1, PApplet.parseInt(textWidth(excludeValue + "")) + 30);
        w2 = max(w2, PApplet.parseInt(textWidth("1")) + 30);
      }
    }
  }
  excludes = sortPVectorArrayList(excludes);
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w3 = PApplet.parseInt(textWidth("Selected")) + 30;
  int w4 = PApplet.parseInt(textWidth(config.getSelected().size() + "")) + 30;
  int w5 = PApplet.parseInt(textWidth("Available")) + 30;
  int w6 = PApplet.parseInt(textWidth(available.size() + "")) + 30;
  int w7 = PApplet.parseInt(textWidth("Exclude sum")) + 30;
  int w8 = PApplet.parseInt(textWidth(totalExcludes + "")) + 30;
  int w9 = PApplet.parseInt(textWidth("Excluded")) + 30;
  int w10 = PApplet.parseInt(textWidth(PApplet.parseInt(pow(2, config.getDimension()) - available.size() - config.getSelected().size() - 1) + "")) + 30;
  int w = max(w3 + w4, w5 + w6, w7 + w8);
  w = max(w, w9 + w10);
  rect(x, y, w, 60); //box that says Statistics
  rect(x, y + 60, w3, 60); //box that says Selected
  rect(x + w3, y + 60, w - w3, 60); //box with the number of selected nodes
  rect(x, y + 120, w5, 60); //box that says Available
  rect(x + w5, y + 120, w - w5, 60); //box with the number of available nodes
  rect(x, y + 180, w7, 60); //box that says Exclude sum
  rect(x + w7, y + 180, w - w7, 60); //box with the number of total excludes
  rect(x, y + 240, w9, 60); //box that says Excluded
  rect(x + w9, y + 240, w - w9, 60);
  fill(255);
  text("Statistics", x + 15, y + 40);
  text("Selected", x + 15, y + 100);
  text(config.getSelected().size() + "", x + w3 + 15, y + 100);
  text("Available", x + 15, y + 160);
  text(available.size() + "", x + w5 + 15, y + 160);
  text("Exclude sum", x + 15, y + 220);
  text(totalExcludes + "", x + w7 + 15, y + 220);
  text("Excluded", x + 15, y + 280);
  text(PApplet.parseInt(pow(2, config.getDimension()) - available.size() - config.getSelected().size() - 1) + "", x + w9 + 15, y + 280);
  textSize(15);
  for (int i = 0; i < excludes.size(); i++) {
    if (i <= (excludes.size() - 1) / 2) {
      fill(0);
      rect(x, y + 300 + i * 30, w1, 30);
      rect(x + w1, y + 300 + i * 30, w2, 30);
      fill(255);
      text(PApplet.parseInt(excludes.get(i).x) + "", x + 15, y + 322 + i * 30);
      text(PApplet.parseInt(excludes.get(i).y) + "", x + w1 + 15, y + 322 + i * 30);
    } else {
      int i2 = i - (excludes.size() - 1) / 2 - 1; 
      fill(0);
      rect(x + w1 + w2 + 10, y + 300 + i2 * 30, w1, 30);
      rect(x + w1 + w2 + w1 + 10, y + 300 + i2 * 30, w2, 30);
      fill(255);
      text(PApplet.parseInt(excludes.get(i).x) + "", x + w1 + w2 + 25, y + 322 + i2 * 30);
      text(PApplet.parseInt(excludes.get(i).y) + "", x + w1 + w2 + w1 + 25, y + 322 + i2 * 30);
    }
  }
}


public ArrayList<PVector> sortPVectorArrayList(ArrayList<PVector> PVectors) {
  ArrayList<PVector> sortedPVectors = new ArrayList<PVector>();
  while (PVectors.size() > 0) {
    int smallest = 0;
    for (int i = 0; i < PVectors.size(); i++) {
      if (PVectors.get(i).x < PVectors.get(smallest).x) {
        smallest = i;
      }
    }
    sortedPVectors.add(PVectors.get(smallest));
    PVectors.remove(smallest);
  }
  return sortedPVectors;
}


public boolean mouseIn(int x, int y, int w, int h) {
  if (mouseX <= x || x + w <= mouseX || mouseY <= y || y + h <= mouseY) {
    return false;
  } else {
    return true;
  }
}


public String bitStrToStr(boolean[] bitStr) {
  String str = "";
  for (int i = omitFirstBit ? 1 : 0; i < bitStr.length; i++) {
    if (bitStr[i]) {
      str += "1";
    } else {
      str += "0";
    }
  }
  return str;
}


public void displayBitStr(boolean[] bitStr) {
  print("(" + (bitStr[0] ? 1 : 0));
  for (int i = 1; i < bitStr.length; i++) {
    print(", " + (bitStr[i] ? 1 : 0));
  }
  print(")");
}

public void mousePressed() {
  mp = true;
}

public void mouseReleased() {
  mp = false;
  mr = true;
}

public void keyPressed() {
  if (key == 's') {
    for (int i = 0; i < config.getSelected().size(); i++) {
      print(config.getSelected().get(i));
      print(" ");
    }
    println();
  } if (key == 'a') {
    IntList available = config.availableNodes();
    for (int i = 0; i < available.size(); i++) {
      print(available.get(i) + " ");
    }
    println();
  } if (key == 'r') {
    if (config.getSelected().size() > 13) {
      config.clear();
      config.complete();
    }
  } if (key == '7') {
    config.getNodes()[7].setSelected(true);
  } else if (key == CODED) {
    if (keyCode == UP) {
      if (config.getDimension() < 14) {
        config = config.increment();
      }
    } else if (keyCode == DOWN) {
      if (config.getDimension() > 0) {
        config = config.decrement();
      }
    }
  }
}
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
  
  public void makeChildren() {
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
  
  public void killChildren() {
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
  
  
  public void display(int x, int y, int w, int h, int s, int sw) {
    stroke(s);
    strokeWeight(sw);
    noFill();
    if (hasChildren) {
      line(x, y + w / 2, x + w, y + h / 2);
      line(x + h / 2, y, x + w / 2, y + h);
      children[0].display(x, y, ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5f)), ceil(0.6f * sw));
      children[1].display(ceil(x + w / 2), y, ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5f)), ceil(0.6f * sw));
      children[2].display(x, ceil(y + h / 2), ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5f)), ceil(0.6f * sw));
      children[3].display(ceil(x + w / 2), ceil(y + h / 2), ceil(w / 2), ceil(h / 2), ceil(lerp(s, 128, 0.5f)), ceil(0.6f * sw));
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
        ellipse(x + w / 2, y + h / 2, 0.75f * min(w, h), 0.75f * min(w, h));
      }
      if (excludes > 0 && index != 0) {
        textSize(ceil(min(w, h) / 1.5f));
        fill(255);
        text(excludes, x + w / 2 - textWidth(excludes + "") / 2, y + 0.75f * h);
      }
      if (displayDecimalOrderNumbers && min(w, h) / 4 > 5) { //checks if it should display the numbers and if the boxes are big enough to be visible
        textSize(ceil(min(w, h) / 4));
        fill(255);
        text(index, x + 0.1f * w, y + 0.3f * h);
      }
      if (displayBinaryOrderNumbers && min(w, h) / 4 > 6) {
        boolean[] bitStr = config.intToBitStr(index);
        String str = bitStrToStr(bitStr);
        textSize(ceil(min(w, h) / 6));
        fill(255);
        text(str, x + 0.1f * w, y + 0.2f * h);
      }
      if (index == 0) {
        stroke(200);
        strokeWeight(sw);
        line(x, y, x + w, y + h);
        line(x, y + h, x + w, y);
      }
    }
  }
  
  public void display(int x, int y, int w, int h) {
    display(x, y, w, h, 200, (w + h) / 200);
  }
  
  
  public boolean hasGrandChildren() {
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
  
  public boolean getHasChildren() {
    return hasChildren;
  }
  
  public boolean getX() {
    return x;
  }
  
  public boolean getY() {
    return y;
  }
  
  public boolean[] getBitStrXY() {
    boolean[] xy = {x, y};
    return xy;
  }
  
  public int getIntXY() {
    int xInt = x ? 2 : 0;
    int yInt = y ? 1 : 0;
    int xy = xInt + yInt;
    return xy;
  }
  
  public int getIndex() {
    return index;
  }
  
  public boolean getMouseOnMe() {
    return mouseOnMe;
  }
  
  public boolean getSelected() {
    return selected;
  }
  
  public void invertSelection() {
    selected = !selected;
  }
  
  public void incrementExcludes() {
    excludes++;
  }
  
  public void decrementExcludes() {
    excludes--;
  }
  
  public void setExcludes(int excludes_) {
    excludes = excludes_;
  }
  
  public int getExcludes() {
    return excludes;
  }
  
  public BinaryNode[] getChildren() {
    if (hasChildren) {
      return children;
    } else {
      return null;
    }
  }
  
  public BinaryNode getChild1() {
    if (hasChildren) {
      return children[0];
    } else {
      return null;
    }
  }
  
  public BinaryNode getChild2() {
    if (hasChildren) {
      return children[1];
    } else {
      return null;
    }
  }
  
  public BinaryNode getChild3() {
    if (hasChildren) {
      return children[2];
    } else {
      return null;
    }
  }
  
  public BinaryNode getChild4() {
    if (hasChildren) {
      return children[3];
    } else {
      return null;
    }
  }
  
  public void setSelected(boolean selected_) {
    selected = selected_;
  }
  
}
class NodeConfiguration {
  
  int dimension;
  BinaryNode seed;
  int x;
  int y;
  int w;
  int h;
  BinaryNode[] nodes;
  IntList selected;
  boolean mouseOnMe;
  boolean inDimUpButton;
  
  NodeConfiguration(int dimension_, int x_, int y_, int w_, int h_) {
    dimension = dimension_;
    seed = new BinaryNode(0, 0);
    for (int i = 0; i < dimension / 2; i++) {
      seed.makeChildren();
    }
    x = x_;
    y = y_;
    w = w_;
    h = h_;
    nodes = new BinaryNode[PApplet.parseInt(pow(2, dimension))];
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = getNode(i);
    }
    selected = new IntList();
    mouseOnMe = false;
    inDimUpButton = false;
  }
  
  public void display() {
    stroke(200);
    strokeWeight((w + h) / 200);
    noFill();
    rect(x, y, w, h);
    seed.display(x, y, w, h);
    if (mr) {
      for (int i = 1; i < nodes.length; i++) {
        if (nodes[i].getMouseOnMe() && nodes[i].getExcludes() == 0) { //if mouse is on node, and it can be selected/deselected
          if (nodes[i].getSelected()) { //if node is selected
            nodes[i].setSelected(false);
            for (int j = 0; j < selected.size(); j++) {
              if (selected.get(j) == nodes[i].getIndex()) {
                selected.remove(j);
                j--;
              }
            }
            updateExcludesAfterRemoving(nodes[i]);
          } else { //if node is not selected
            nodes[i].setSelected(true);
            updateExcludesAfterAdding(nodes[i]);
            selected.append(nodes[i].getIndex());
          }
        }
      }
      mr = false;
      updateExcludes();
    }
    if (mouseIn(x, y, w, h)) {
      mouseOnMe = true;
    } else {
      mouseOnMe = false;
    }
  }
  
  public NodeConfiguration increment() {
    NodeConfiguration incremented = new NodeConfiguration(dimension + 2, x, y, w, h);
    for (int i = 0; i < selected.size(); i++) {
      incremented.getNodes()[selected.get(i)].setSelected(true);
    }
    incremented.setSelected(selected);
    incremented.updateExcludes();
    return incremented;
  }
  
  public NodeConfiguration decrement() {
    NodeConfiguration decremented = new NodeConfiguration(dimension - 2, x, y, w, h);
    for (int i = 0; i < selected.size(); i++) {
      if (selected.get(i) < decremented.getNodes().length) {
        decremented.getNodes()[selected.get(i)].setSelected(true);
        decremented.getSelected().append(selected.get(i));
      }
    }
    decremented.updateExcludes();
    return decremented;
  }
  
  
  public BinaryNode getNode(boolean[] bitStr, BinaryNode ancestor) {
    assert bitStr.length % 2 == 0;
    if (bitStr.length < 2) {
      return ancestor;
    } else {
      boolean[] bitStrNext = new boolean[bitStr.length - 2];
      arrayCopy(bitStr, 2, bitStrNext, 0, bitStr.length - 2);
      boolean x = bitStr[0];
      boolean y = bitStr[1];
      if (!x && !y) {
        return getNode(bitStrNext, ancestor.getChild1());
      } else if (!x && y) {
        return getNode(bitStrNext, ancestor.getChild2());
      } else if (x && !y) {
        return getNode(bitStrNext, ancestor.getChild3());
      } else {
        return getNode(bitStrNext, ancestor.getChild4());
      }
    }
  }
  
  public BinaryNode getNode(boolean[] bitStr) {
    return getNode(bitStr, seed);
  }
  
  public BinaryNode getNode(int n) {
    boolean[] bitStr = intToBitStr(n);
    return getNode(bitStr);
  }
  
  
  public boolean[] intToBitStr(int n) {
    assert n < pow(2, dimension);
    boolean[] bitStr = new boolean[dimension];
    for (int i = 1; i <= dimension; i++) {
      if (n % 2 == 1) {
        bitStr[dimension - i] = true;
      }
      n /= 2;
    }
    return bitStr;
  }
  
  public int bitStrToInt(boolean[] bitStr) {
    return getNode(bitStr).getIndex();
  }


  public boolean[] bitStrSum(boolean[] bitStr1, boolean[] bitStr2) {
    assert bitStr1.length == bitStr2.length;
    boolean[] bitStr3 = new boolean[bitStr1.length];
    for (int i = 0; i < bitStr3.length; i++) {
      if (bitStr1[i] == bitStr2[i]) {
        bitStr3[i] = false;
      } else {
        bitStr3[i] = true;
      }
    }
    return bitStr3;
  }
  
  //takes the int index of two nodes and returnes the index of the node that completes their SET
  public int intSum(int n1, int n2) {
    boolean[] bitStr1 = intToBitStr(n1);
    boolean[] bitStr2 = intToBitStr(n2);
    boolean[] bitStr3 = bitStrSum(bitStr1, bitStr2);
    int n3 = bitStrToInt(bitStr3);
    return n3;
  }
  
  public void updateExcludes() {
    for (int n = 0; n < nodes.length; n++) {
      nodes[n].setExcludes(0);
    }
    for (int i = 0; i < selected.size(); i++) {
      for (int j = i + 1; j < selected.size(); j++) {
        //BinaryNode node1 = getNode(selected.get(i));
        //BinaryNode node2 = getNode(selected.get(j));
        BinaryNode node1 = nodes[selected.get(i)];
        BinaryNode node2 = nodes[selected.get(j)];
        int excludeIndex = intSum(node1.getIndex(), node2.getIndex());
        nodes[excludeIndex].incrementExcludes();
      }
    }
  }
  
  public void updateExcludesAfterAdding(BinaryNode node1) {
    for (int i = 0; i < selected.size(); i++) {
      BinaryNode node2 = nodes[selected.get(i)];
      int excludeIndex = intSum(node1.getIndex(), node2.getIndex());
      nodes[excludeIndex].incrementExcludes();
    }
  }
  
  public void updateExcludesAfterRemoving(BinaryNode node1) {
    for (int i = 0; i < selected.size(); i++) {
      BinaryNode node2 = nodes[selected.get(i)];
      int excludeIndex = intSum(node1.getIndex(), node2.getIndex());
      nodes[excludeIndex].decrementExcludes();
    }
  }
  
  
  public void clear() {
    for (int i = 0; i < nodes.length; i++) {
      nodes[i].setSelected(false);
      nodes[i].setExcludes(0);
    }
    selected = new IntList();
  }
  
  
  public IntList availableNodes() {
    IntList available = new IntList();
    for (int i = 1; i < nodes.length; i++) {
      if (!nodes[i].getSelected() && nodes[i].getExcludes() == 0) {
        available.append(i);
      }
    }
    return available;
  }
  
  public boolean available(BinaryNode node) {
    if (!node.getSelected() && node.getExcludes() == 0) {
      return true;
    } else {
      return false;
    }
  }
  
  
  public void complete() {
    IntList available = availableNodes();
    while (available.size() > 0) {
      int select = PApplet.parseInt(random(0, available.size()));
      nodes[available.get(select)].setSelected(true);
      selected.append(available.get(select));
      //updateExcludes();
      updateExcludesAfterAdding(nodes[available.get(select)]); //WAAAAAYYY faster than using updateExcludes();
      //available = availableNodes();
      for (int i = 0; i < available.size(); i++) { //faster than using availableNodes()
        if (!available(nodes[available.get(i)])) {
          available.remove(i);
          i--;
        }
      }
    }
  }
  
  
  public int getTotalExcludes() {
    int totalExcludes = 0;
    for (int i = 0; i < nodes.length; i++) {
      totalExcludes += nodes[i].getExcludes();
    }
    return totalExcludes;
  }
  
  
  public int getDimension() {
    return dimension;
  }
  
  public BinaryNode[] getNodes() {
    return nodes; 
  }
  
  public IntList getSelected() {
    return selected;
  }
  
  public void setSelected(IntList selected_) {
    selected = selected_;
  }
  
  public boolean getMouseOnMe() {
    return mouseOnMe;
  }
  
  
  
}
  public void settings() {  size(1600, 850); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ProjectiveSetCapFinder" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
