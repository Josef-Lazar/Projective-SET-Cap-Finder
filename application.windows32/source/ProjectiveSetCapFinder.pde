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

void setup() {
  size(1600, 850);
  background(0);
  frameRate(30);
}

void draw() {
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


void displayDimensionControl(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w1 = int(textWidth("Dimension") + 30);
  int w2 = int(textWidth(config.getDimension() + "")) + 30;
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


void displayClearButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = int(textWidth("Clear") + 30);
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


void displayCompleteButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = int(textWidth("Complete") + 30);
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


void displayRandomButton(int x, int y) {
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w = int(textWidth("Random") + 30);
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


void displayStatisticsButton(int x, int y) {
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
          w2 = max(w2, int(textWidth(int(excludes.get(exclude).y) + "")) + 30);
        }
      }
      if (!alreadyInExcludes) {
        PVector exclude = new PVector(excludeValue, 1);
        excludes.add(exclude);
        w1 = max(w1, int(textWidth(excludeValue + "")) + 30);
        w2 = max(w2, int(textWidth("1")) + 30);
      }
    }
  }
  excludes = sortPVectorArrayList(excludes);
  fill(0);
  stroke(150);
  strokeWeight(2);
  textSize(30);
  int w3 = int(textWidth("Selected")) + 30;
  int w4 = int(textWidth(config.getSelected().size() + "")) + 30;
  int w5 = int(textWidth("Available")) + 30;
  int w6 = int(textWidth(available.size() + "")) + 30;
  int w7 = int(textWidth("Exclude sum")) + 30;
  int w8 = int(textWidth(totalExcludes + "")) + 30;
  int w9 = int(textWidth("Excluded")) + 30;
  int w10 = int(textWidth(int(pow(2, config.getDimension()) - available.size() - config.getSelected().size() - 1) + "")) + 30;
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
  text(int(pow(2, config.getDimension()) - available.size() - config.getSelected().size() - 1) + "", x + w9 + 15, y + 280);
  textSize(15);
  for (int i = 0; i < excludes.size(); i++) {
    if (i <= (excludes.size() - 1) / 2) {
      fill(0);
      rect(x, y + 300 + i * 30, w1, 30);
      rect(x + w1, y + 300 + i * 30, w2, 30);
      fill(255);
      text(int(excludes.get(i).x) + "", x + 15, y + 322 + i * 30);
      text(int(excludes.get(i).y) + "", x + w1 + 15, y + 322 + i * 30);
    } else {
      int i2 = i - (excludes.size() - 1) / 2 - 1; 
      fill(0);
      rect(x + w1 + w2 + 10, y + 300 + i2 * 30, w1, 30);
      rect(x + w1 + w2 + w1 + 10, y + 300 + i2 * 30, w2, 30);
      fill(255);
      text(int(excludes.get(i).x) + "", x + w1 + w2 + 25, y + 322 + i2 * 30);
      text(int(excludes.get(i).y) + "", x + w1 + w2 + w1 + 25, y + 322 + i2 * 30);
    }
  }
}


ArrayList<PVector> sortPVectorArrayList(ArrayList<PVector> PVectors) {
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


boolean mouseIn(int x, int y, int w, int h) {
  if (mouseX <= x || x + w <= mouseX || mouseY <= y || y + h <= mouseY) {
    return false;
  } else {
    return true;
  }
}


String bitStrToStr(boolean[] bitStr) {
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


void displayBitStr(boolean[] bitStr) {
  print("(" + (bitStr[0] ? 1 : 0));
  for (int i = 1; i < bitStr.length; i++) {
    print(", " + (bitStr[i] ? 1 : 0));
  }
  print(")");
}

void mousePressed() {
  mp = true;
}

void mouseReleased() {
  mp = false;
  mr = true;
}

void keyPressed() {
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
