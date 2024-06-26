val Versions = new { // sbt doesn't like object definitions in build.sbt
  val circe           = "0.14.9"
  val disciplineMUnit = "2.0.0"
  val fs2             = "3.10.2"
  val kittens         = "3.3.0"
  val lucumaCore      = "0.100.0"
  val lucumaODBSchema = "0.11.10"
  val munit           = "1.0.0"
  val munitCatsEffect = "2.0.0"
}

ThisBuild / tlBaseVersion       := "0.90"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / crossScalaVersions  := Seq("3.4.2")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / scalafixScalaBinaryVersion                               := "2.13"
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

lazy val root = tlCrossRootProject.aggregate(model, testkit, lucumaSchemas, modelTests)

val model =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("model"))
    .settings(
      moduleName := "lucuma-schemas-model",
      libraryDependencies ++= Seq(
        "io.circe"      %%% "circe-core"        % Versions.circe,
        "io.circe"      %%% "circe-generic"     % Versions.circe,
        "org.typelevel" %%% "kittens"           % Versions.kittens,
        "edu.gemini"    %%% "lucuma-core"       % Versions.lucumaCore,
        "edu.gemini"    %%% "lucuma-odb-schema" % Versions.lucumaODBSchema
      )
    )

val testkit =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("testkit"))
    .dependsOn(model)
    .settings(
      moduleName := "lucuma-schemas-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % Versions.lucumaCore
      )
    )

val modelTests =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("tests"))
    .dependsOn(testkit)
    .enablePlugins(NoPublishPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "org.typelevel" %%% "discipline-munit" % Versions.disciplineMUnit % Test,
        "org.scalameta" %%% "munit"            % Versions.munit           % Test
      )
    )

val lucumaSchemas =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("lucuma-schemas"))
    .dependsOn(model)
    .settings(
      moduleName                    := "lucuma-schemas",
      libraryDependencies ++= Seq(
        "co.fs2"        %%% "fs2-io"            % Versions.fs2             % Test,
        "org.scalameta" %%% "munit"             % Versions.munit           % Test,
        "org.typelevel" %%% "munit-cats-effect" % Versions.munitCatsEffect % Test
      ),
      Compile / clueSourceDirectory := (ThisBuild / baseDirectory).value / "lucuma-schemas" / "src" / "clue",
      // Include schema files in jar.
      Compile / unmanagedResourceDirectories += (Compile / clueSourceDirectory).value / "resources"
    )
    .jsSettings(
      Test / scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
    )
    .enablePlugins(CluePlugin)
