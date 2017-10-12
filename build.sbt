import scala.util.matching.Regex

name := "HolomorphicMaps"

val copyMainProcess = taskKey[File]("Return main file.")
val copyPlotWindows = taskKey[File]("Return main file.")
val copyMainWindow = taskKey[File]("Return main file.")
lazy val fastOptCompileCopy = taskKey[Unit]("Compile and copy paste projects and generate html.")

val copyMainProcessFullOpt = taskKey[File]("Return main file")
val copyPlotWindowsFullOpt = taskKey[File]("Return main file")
val copyMainWindowFullOpt = taskKey[File]("Return main file")
lazy val fullOptCompileCopy = taskKey[Unit]("Compile and copy paste projects, and generate html.")


version := "0.1"

scalaVersion := "2.12.3"

fullOptCompileCopy := {

  val mainProcessDirectory = (copyMainProcessFullOpt in `mainProcess`).value
  IO.delete(baseDirectory.value / "electron/mainprocess")
  IO.copyDirectory(
    mainProcessDirectory.getParentFile,
    baseDirectory.value / "electron/mainprocess",
    overwrite = true
  )


  val plotWindowsDirectory = (copyPlotWindowsFullOpt in `plotWindows`).value
  IO.delete(baseDirectory.value / "electron/plotwindows/js")
  IO.copyDirectory(
    plotWindowsDirectory.getParentFile,
    baseDirectory.value / "electron/plotwindows/js",
    overwrite = true
  )

  val mainWindowDirectory = (copyMainWindowFullOpt in `mainWindow`).value
  IO.delete(baseDirectory.value / "electron/mainwindow/js")
  IO.copyDirectory(
    mainWindowDirectory.getParentFile,
    baseDirectory.value / "electron/mainwindow/js",
    overwrite = true
  )


  def fastOptToFullOpt(line: String): String =
    new Regex("fastopt").replaceAllIn(line, "opt")

  val sourcePackageJSON = IO.readLines(baseDirectory.value / "electron/package.json")

  IO.writeLines(
    baseDirectory.value / "electron/package.json",
    sourcePackageJSON.map(fastOptToFullOpt)
  )


  val sourceMainWindowHtml = IO.readLines(baseDirectory.value / "electron/mainwindow/html/index.html")

  IO.writeLines(
    baseDirectory.value / "electron/mainwindow/html/index.html",
    sourceMainWindowHtml.map(fastOptToFullOpt)
  )

  val sourcePlotWindowsHtml = IO.readLines(baseDirectory.value / "electron/plotwindows/html/index.html")

  IO.writeLines(
    baseDirectory.value / "electron/plotwindows/html/index.html",
    sourcePlotWindowsHtml.map(fastOptToFullOpt)
  )



  println("[info] Files copied to relevant directories")


}


fastOptCompileCopy := {
  val mainProcessDirectory = (copyMainProcess in `mainProcess`).value
  IO.delete(baseDirectory.value / "electron/mainprocess")
  IO.copyDirectory(
    mainProcessDirectory.getParentFile,
    baseDirectory.value / "electron/mainprocess",
    overwrite = true
  )


  val plotWindowsDirectory = (copyPlotWindows in `plotWindows`).value
  IO.delete(baseDirectory.value / "electron/plotwindows/js")
  IO.copyDirectory(
    plotWindowsDirectory.getParentFile,
    baseDirectory.value / "electron/plotwindows/js",
    overwrite = true
  )

  val mainWindowDirectory = (copyMainWindow in `mainWindow`).value
  IO.delete(baseDirectory.value / "electron/mainwindow/js")
  IO.copyDirectory(
    mainWindowDirectory.getParentFile,
    baseDirectory.value / "electron/mainwindow/js",
    overwrite = true
  )

  def fullOptToFastOpt(line: String): String = if (line.contains("fastopt")) line else {
    new Regex("opt").replaceAllIn(line, "fastopt")
  }

  val sourcePackageJSON = IO.readLines(baseDirectory.value / "electron/package.json")

  IO.writeLines(
    baseDirectory.value / "electron/package.json",
    sourcePackageJSON.map(fullOptToFastOpt)
  )


  val sourceMainWindowHtml = IO.readLines(baseDirectory.value / "electron/mainwindow/html/index.html")

  IO.writeLines(
    baseDirectory.value / "electron/mainwindow/html/index.html",
    sourceMainWindowHtml.map(fullOptToFastOpt)
  )

  val sourcePlotWindowsHtml = IO.readLines(baseDirectory.value / "electron/plotwindows/html/index.html")

  IO.writeLines(
    baseDirectory.value / "electron/plotwindows/html/index.html",
    sourcePlotWindowsHtml.map(fullOptToFastOpt)
  )

  println("[info] Files copied to relevant directories")
}


val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "utf-8")
)

lazy val `mainProcess` = project.in(file("mainprocess"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.suzaku" %%% "boopickle" % "1.2.7-SNAPSHOT"
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    copyMainProcess := {
      (fastOptJS in Compile).value.data
    },
    copyMainProcessFullOpt := {
      (fullOptJS in Compile).value.data
    }

  )
  .dependsOn(shared)

lazy val `plotWindows` = project.in(file("plotwindows"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.suzaku" %%% "boopickle" % "1.2.7-SNAPSHOT"
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    copyPlotWindows := {
      (fastOptJS in Compile).value.data
    },
    copyPlotWindowsFullOpt := {
      (fullOptJS in Compile).value.data
    }

  )
  .dependsOn(sharedPlotsAndMainWindow)

lazy val `mainWindow` = project.in(file("mainwindow"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.suzaku" %%% "boopickle" % "1.2.7-SNAPSHOT"
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    copyMainWindow := {
      (fastOptJS in Compile).value.data
    },
    copyMainWindowFullOpt := {
      (fullOptJS in Compile).value.data
    }

  )
  .dependsOn(sharedPlotsAndMainWindow)


lazy val `facade` = project.in(file("facades"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings).settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3"
    )
  )

lazy val `sharedPlotsAndMainWindow` = project.in(file("sharedPlotsAndMainWindow"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.suzaku" %%% "boopickle" % "1.2.7-SNAPSHOT"
    )
  )
  .dependsOn(shared)

lazy val `shared` = project.in(file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.suzaku" %%% "boopickle" % "1.2.7-SNAPSHOT"
    )
  )
  .dependsOn(facade)
