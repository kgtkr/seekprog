package net.kgtkr.seekprog;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PathSearchingVirtualMachine;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import processing.mode.java.Commander;
import java.io.File;
import scala.jdk.CollectionConverters._
import processing.app.Util
import processing.app.Base
import processing.app.Platform
import processing.app.Preferences
import processing.mode.java.JavaMode
import processing.app.contrib.ModeContribution
import processing.app.Sketch
import processing.mode.java.JavaBuild
import java.util.concurrent.LinkedTransferQueue
import java.net.UnixDomainSocketAddress
import java.nio.channels.ServerSocketChannel
import java.net.StandardProtocolFamily
import java.nio.file.Files
import java.nio.file.Path
import net.kgtkr.seekprog.runtime.EventWrapper
import scala.collection.mutable.Buffer

enum RunnerCmd {
  case ReloadSketch(frameCount: Option[Int] = None);
  case UpdateLocation(
      frameCount: Int,
      trimMax: Boolean,
      events: List[List[EventWrapper]]
  );
}

enum RunnerEvent {
  case UpdateLocation(frameCount: Int, max: Int);
  case StartSketch();
}

class Runner(val sketchPath: String) {
  val cmdQueue = new LinkedTransferQueue[RunnerCmd]();
  // 1つのスレッドからしかアクセスしないこと
  var eventListeners = List[RunnerEvent => Unit]();

  var frameCount = 0;
  var maxFrameCount = 0;
  val events = Buffer[List[EventWrapper]]();

  val sockPath = Path.of(sketchPath, "seekprog.sock")

  def run() = {
    Files.deleteIfExists(sockPath);
    val sockAddr = UnixDomainSocketAddress.of(sockPath);
    val ssc = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
    ssc.bind(sockAddr);

    Base.setCommandLine();
    Platform.init();
    Preferences.init();
    Base.locateSketchbookFolder();

    while (true) {
      new VmManager(this, ssc).run();
    }
  }
}
