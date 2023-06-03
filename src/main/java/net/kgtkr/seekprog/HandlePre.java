package net.kgtkr.seekprog;

import processing.core.PApplet;

public class HandlePre {
    private PApplet applet;
    private int targetFrameCount;

    public HandlePre(PApplet applet, int targetFrameCount) {
        this.applet = applet;
        this.targetFrameCount = targetFrameCount;
    }

    public void pre() {
        if (this.applet.frameCount == this.targetFrameCount) {
            new OnHandlePre().onTargetFrameCount(this.applet);
        }
    }
}
