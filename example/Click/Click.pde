int MAX_SIZE = 5 + 1;
int MAX_COUNT = 60;
int[] xs = new int[MAX_SIZE];
int[] ys = new int[MAX_SIZE];
int[] count = new int[MAX_SIZE];
int begin = 0;
int end = 0;

void setup() {
  size(600, 400);
}

void draw() {
  background(255);

  
  for (int i = begin; i != end; i = (i + 1) % MAX_SIZE) {
    fill(255, 255, 0); 
    circle(xs[i], ys[i], min(frameCount - count[i], 100));
  }

  int newBegin = begin;
  for (int i = begin; i != end; i = (i + 1) % MAX_SIZE) {
    if (count[i] + MAX_COUNT < frameCount) {
      newBegin = (newBegin + 1) % MAX_SIZE;
    }
  }
  begin = newBegin;
  
}  

void mousePressed() {
  if (begin == (end + 1) % MAX_SIZE) {
    begin = (begin + 1) % MAX_SIZE;
  }
 
  xs[end] = mouseX;
  ys[end] = mouseY;
  count[end] = frameCount;
  end = (end + 1) % MAX_SIZE;
}
