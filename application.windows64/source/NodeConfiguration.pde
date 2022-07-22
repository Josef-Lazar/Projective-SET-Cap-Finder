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
    nodes = new BinaryNode[int(pow(2, dimension))];
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = getNode(i);
    }
    selected = new IntList();
    mouseOnMe = false;
    inDimUpButton = false;
  }
  
  void display() {
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
  
  NodeConfiguration increment() {
    NodeConfiguration incremented = new NodeConfiguration(dimension + 2, x, y, w, h);
    for (int i = 0; i < selected.size(); i++) {
      incremented.getNodes()[selected.get(i)].setSelected(true);
    }
    incremented.setSelected(selected);
    incremented.updateExcludes();
    return incremented;
  }
  
  NodeConfiguration decrement() {
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
  
  
  BinaryNode getNode(boolean[] bitStr, BinaryNode ancestor) {
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
  
  BinaryNode getNode(boolean[] bitStr) {
    return getNode(bitStr, seed);
  }
  
  BinaryNode getNode(int n) {
    boolean[] bitStr = intToBitStr(n);
    return getNode(bitStr);
  }
  
  
  boolean[] intToBitStr(int n) {
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
  
  int bitStrToInt(boolean[] bitStr) {
    return getNode(bitStr).getIndex();
  }


  boolean[] bitStrSum(boolean[] bitStr1, boolean[] bitStr2) {
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
  int intSum(int n1, int n2) {
    boolean[] bitStr1 = intToBitStr(n1);
    boolean[] bitStr2 = intToBitStr(n2);
    boolean[] bitStr3 = bitStrSum(bitStr1, bitStr2);
    int n3 = bitStrToInt(bitStr3);
    return n3;
  }
  
  void updateExcludes() {
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
  
  void updateExcludesAfterAdding(BinaryNode node1) {
    for (int i = 0; i < selected.size(); i++) {
      BinaryNode node2 = nodes[selected.get(i)];
      int excludeIndex = intSum(node1.getIndex(), node2.getIndex());
      nodes[excludeIndex].incrementExcludes();
    }
  }
  
  void updateExcludesAfterRemoving(BinaryNode node1) {
    for (int i = 0; i < selected.size(); i++) {
      BinaryNode node2 = nodes[selected.get(i)];
      int excludeIndex = intSum(node1.getIndex(), node2.getIndex());
      nodes[excludeIndex].decrementExcludes();
    }
  }
  
  
  void clear() {
    for (int i = 0; i < nodes.length; i++) {
      nodes[i].setSelected(false);
      nodes[i].setExcludes(0);
    }
    selected = new IntList();
  }
  
  
  IntList availableNodes() {
    IntList available = new IntList();
    for (int i = 1; i < nodes.length; i++) {
      if (!nodes[i].getSelected() && nodes[i].getExcludes() == 0) {
        available.append(i);
      }
    }
    return available;
  }
  
  boolean available(BinaryNode node) {
    if (!node.getSelected() && node.getExcludes() == 0) {
      return true;
    } else {
      return false;
    }
  }
  
  
  void complete() {
    IntList available = availableNodes();
    while (available.size() > 0) {
      int select = int(random(0, available.size()));
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
  
  
  int getTotalExcludes() {
    int totalExcludes = 0;
    for (int i = 0; i < nodes.length; i++) {
      totalExcludes += nodes[i].getExcludes();
    }
    return totalExcludes;
  }
  
  
  int getDimension() {
    return dimension;
  }
  
  BinaryNode[] getNodes() {
    return nodes; 
  }
  
  IntList getSelected() {
    return selected;
  }
  
  void setSelected(IntList selected_) {
    selected = selected_;
  }
  
  boolean getMouseOnMe() {
    return mouseOnMe;
  }
  
  
  
}
