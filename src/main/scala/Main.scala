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
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY
        );

        while (true) {
          val watchKey = watcher.take();

          for (event <- watchKey.pollEvents().asScala) {
            event.context() match {
              case filename: Path => {
                if (filename.toString().endsWith(".pde")) {
                  runner.cmdQueue.add(RunnerCmd.ReloadSketch)
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
          children = Seq(
            new Text {
              text = "Scala"
              style = "-fx-font: normal bold 100pt sans-serif"
              fill = new LinearGradient(endX = 0, stops = Stops(Red, DarkRed))
            },
            new Text {
              text <== when(hover) choose "Green" otherwise "Red"
              style = "-fx-font: italic bold 100pt sans-serif"
              fill = new LinearGradient(
                endX = 0,
                stops = Stops(White, DarkGray)
              )
              effect = new DropShadow {
                color = DarkGray
                radius = 15
                spread = 0.25
              }
            },
            new Slider(0, 100, 100) {}
          )
        }
      }
    }
  }
}
