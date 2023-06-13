package net.kgtkr.seekprog;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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

@main def main(sketchPath: String): Unit = {
  Base.setCommandLine();
  Platform.init();

  val sketchFolder = new File(sketchPath).getAbsoluteFile();
  val pdeFile = new File(sketchFolder, sketchFolder.getName() + ".pde");
  val outputFolder = new File("pdedist").getAbsoluteFile();
  if (outputFolder.exists()) {
    Util.removeDir(outputFolder);
  }
  outputFolder.mkdirs();

  Preferences.init();
  Base.locateSketchbookFolder();

  val javaMode =
    ModeContribution
      .load(
        null,
        Platform.getContentFile("modes/java"),
        "processing.mode.java.JavaMode"
      )
      .getMode()
      .asInstanceOf[JavaMode];

  val sketch = new Sketch(pdeFile.getAbsolutePath(), javaMode);
  val build = new JavaBuild(sketch);
  val srcFolder = new File(outputFolder, "source");
  val mainClassName = build.build(srcFolder, outputFolder, true);

  val launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
  val env = launchingConnector.defaultArguments();
  env.get("main").setValue(mainClassName);
  env
    .get("options")
    .setValue(
      "-classpath " + System.getProperty("java.class.path") + ":" + outputFolder
    );
  val vm = launchingConnector.launch(env);

  val mainMethodEntryRequest =
    vm.eventRequestManager().createMethodEntryRequest();
  mainMethodEntryRequest.addClassFilter(mainClassName);
  mainMethodEntryRequest.enable();

  val onHandlePde = "net.kgtkr.seekprog.OnHandlePre";
  val onHandlePreMethodEntryRequest =
    vm.eventRequestManager().createMethodEntryRequest();
  onHandlePreMethodEntryRequest.addClassFilter(onHandlePde);
  onHandlePreMethodEntryRequest.enable();

  var gBak: ObjectReference | Null = null;

  var eventSet: EventSet | Null = null;

  try {
    while ({
      eventSet = vm.eventQueue().remove();
      eventSet != null
    }) {
      for (evt <- eventSet.nn.asScala) {
        println(evt);
        evt match {
          case evt: MethodEntryEvent => {
            if (
              evt.method().declaringType().name().equals(mainClassName)
              && evt.method().name().equals("draw")
            ) {
              val frame = evt.thread().frame(0);
              val instance = frame.thisObject();

              val surface = instance
                .getValue(instance.referenceType().fieldByName("surface"))
                .asInstanceOf[ObjectReference];
              surface.setValue(
                surface.referenceType().fieldByName("frameRatePeriod"),
                vm.mirrorOf(1)
              );
              gBak = instance
                .getValue(instance.referenceType().fieldByName("g"))
                .asInstanceOf[ObjectReference];
              gBak.disableCollection();
              val PGraphicsClassType = vm
                .classesByName("processing.core.PGraphics")
                .get(0)
                .asInstanceOf[ClassType];

              instance.setValue(
                instance.referenceType().fieldByName("g"),
                PGraphicsClassType.newInstance(
                  evt.thread(),
                  PGraphicsClassType.methodsByName("<init>", "()V").get(0),
                  ArrayList(),
                  0
                )
              );

              val ClassClassType = vm
                .classesByName("java.lang.Class")
                .get(0)
                .asInstanceOf[ClassType];
              ClassClassType.invokeMethod(
                evt.thread(),
                ClassClassType
                  .methodsByName(
                    "forName",
                    "(Ljava/lang/String;)Ljava/lang/Class;"
                  )
                  .get(0),
                Arrays.asList(vm.mirrorOf("net.kgtkr.seekprog.HandlePre")),
                0
              );
              ClassClassType.invokeMethod(
                evt.thread(),
                ClassClassType
                  .methodsByName(
                    "forName",
                    "(Ljava/lang/String;)Ljava/lang/Class;"
                  )
                  .get(0),
                Arrays.asList(vm.mirrorOf("net.kgtkr.seekprog.OnHandlePre")),
                0
              );

              val HandlePreClassType = vm
                .classesByName("net.kgtkr.seekprog.HandlePre")
                .get(0)
                .asInstanceOf[ClassType];

              instance.invokeMethod(
                evt.thread(),
                instance.referenceType().methodsByName("registerMethod").get(0),
                Arrays.asList(
                  vm.mirrorOf("pre"),
                  HandlePreClassType.newInstance(
                    evt.thread(),
                    HandlePreClassType
                      .methodsByName("<init>")
                      .get(0),
                    Arrays.asList(instance, vm.mirrorOf(6000)),
                    0
                  )
                ),
                0
              );

              mainMethodEntryRequest.disable();
            }

            if (
              evt.method().declaringType().name().equals(onHandlePde)
              && evt.method().name().equals("onTargetFrameCount")
            ) {
              val frame = evt.thread().frame(0);
              val instance =
                frame.getArgumentValues().get(0).asInstanceOf[ObjectReference];

              val surface = instance
                .getValue(instance.referenceType().fieldByName("surface"))
                .asInstanceOf[ObjectReference]

              surface.setValue(
                surface.referenceType().fieldByName("frameRatePeriod"),
                vm.mirrorOf(16666666L)
              );
              instance.setValue(
                instance.referenceType().fieldByName("g"),
                gBak
              );
              gBak.enableCollection();
              onHandlePreMethodEntryRequest.disable();
            }
          }
          case evt: VMDisconnectEvent => {
            println("VM is now disconnected.");
          }
          case _ => {}
        }

      }

      {
        val reader = new InputStreamReader(vm.process().getInputStream());
        val writer = new OutputStreamWriter(System.out);
        var size = 0;
        val buf = new Array[Char](1024);
        while (
          reader.ready() && {
            size = reader.read(buf);
            size != -1
          }
        ) {
          writer.write(buf, 0, size);
        }
        writer.flush();
      }

      {
        val reader = new InputStreamReader(vm.process().getErrorStream());
        val writer = new OutputStreamWriter(System.err);
        var size = 0;
        val buf = new Array[Char](1024);
        while (
          reader.ready() && {
            size = reader.read(buf);
            size != -1
          }
        ) {
          writer.write(buf, 0, size);
        }
        writer.flush();
      }

      vm.resume();
    }
  } catch {
    case e: Exception => {
      e.printStackTrace();
    }
  } finally {
    val reader = new InputStreamReader(vm.process().getInputStream());
    val writer = new OutputStreamWriter(System.out);
    var size = 0;
    val buf = new Array[Char](1024);
    while (
      reader.ready() && {
        size = reader.read(buf);
        size != -1
      }
    ) {
      writer.write(buf, 0, size);
    }
    writer.flush();
  }
}
