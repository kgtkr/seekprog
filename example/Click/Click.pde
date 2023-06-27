ArrayList<Integer> xs = new ArrayList<>();
ArrayList<Integer> ys = new ArrayList<>();

void setup() {
  size(600, 400);
}

void draw() {
  background(255);

  fill(255, 255, 0); // 変更
  
  for (int i = 0; i < xs.size(); i++) {
    circle(xs.get(i), ys.get(i), 10);
  }
}

void mousePressed() {
  xs.add(mouseX);
  ys.add(mouseY);
}
