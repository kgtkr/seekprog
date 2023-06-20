package net.kgtkr.seekprog;

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.Includes._
import scalafx.scene.control.Slider
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import scala.jdk.CollectionConverters._
import java.nio.file.Path
import java.nio.file.WatchEvent
import com.sun.nio.file.SensitivityWatchEventModifier
import scalafx.application.Platform
import scalafx.beans.binding.Bindings

object Main extends JFXApp3 {
  override def start(): Unit = {
    val sketchPath = this.parameters.getUnnamed.get(0)
    val runner = new Runner(sketchPath)
    new Thread(new Runnable {
      override def run(): Unit = {
        runner.run()
      }
    }).start()

    new Thread(new Runnable {
      override def run(): Unit = {
        val watcher = FileSystems.getDefault().newWatchService();
        val path = Paths.get(sketchPath);
        path.register(
          watcher,
          Array[WatchEvent.Kind[?]](
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY
          ),
          SensitivityWatchEventModifier.HIGH
        );

        while (true) {
          val watchKey = watcher.take();

          for (event <- watchKey.pollEvents().asScala) {
            event.context() match {
              case filename: Path => {
                if (filename.toString().endsWith(".pde")) {
                  runner.cmdQueue.add(RunnerCmd.ReloadSketch())
                }
              }
              case evt => {
                println(s"unknown event: ${evt}")
              }
            }
          }

          if (!watchKey.reset()) {
            throw new RuntimeException("watchKey reset failed")
          }
        }
      }
    }).start()

    stage = new JFXApp3.PrimaryStage {
      title = "Seekprog"
      scene = new Scene {
        fill = Color.rgb(38, 38, 38)
        content = new HBox {
          padding = Insets(50, 80, 50, 80)
          val slider = new Slider(0, 0, 0) {
            valueChanging.addListener({ (_, _, changing) =>
              if (!changing) {
                runner.cmdQueue.add(
                  RunnerCmd.ReloadSketch(Some(value.value))
                );
              }
              ()
            })
          };
          new Thread {
            override def run(): Unit = {
              while (true) {
                val event = runner.eventQueue.take()
                event match {
                  case RunnerEvent.UpdateLocation(value2, max2) => {
                    Platform.runLater {
                      slider.max = max2
                      slider.value = value2
                    }
                  }
                  case _ => {}
                }
              }
            }
          }.start()
          children = Seq(
            slider,
            new Text {
              style = "-fx-font: normal bold 10pt sans-serif"
              fill = White
              text <== Bindings.createStringBinding(
                () =>
                  f"${slider.value.intValue()}%d秒/ ${slider.max.intValue()}%d秒",
                slider.value,
                slider.max
              )
            }
          )
        }
      }
    }
  }
}
