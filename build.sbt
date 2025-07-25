val Versions = new { // sbt doesn't like object definitions in build.sbt
  val circe           = "0.14.14"
  val circeRefined    = "0.15.1"
  val disciplineMUnit = "2.0.0"
  val fs2             = "3.12.0"
  val kittens         = "3.5.0"
  val lucumaCore      = "0.137.4"
  val lucumaODBSchema = "0.25.6"
  val munit           = "1.1.1"
  val munitCatsEffect = "2.1.0"
}

ThisBuild / tlBaseVersion       := "0.149"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / crossScalaVersions  := Seq("3.7.1")
ThisBuild / tlVersionIntroduced := Map("3" -> "0.29.0")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

// Publish the package to npm, using the version from the git tag (won't be committed to the repo)
ThisBuild / githubWorkflowPublishPostamble += WorkflowStep.Run(
  name = Some("Publish npm package"),
  cond = Some("github.event_name != 'pull_request' && startsWith(github.ref, 'refs/tags/v')"),
  commands = List(
    "echo \"//registry.npmjs.org/:_authToken=${{ secrets.NPM_REPO_TOKEN }}\" > ~/.npmrc",
    "npm version from-git --git-tag-version=false",
    "npm publish --access public"
  )
)

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
        "io.circe"      %%% "circe-refined"     % Versions.circeRefined,
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
    .crossType(CrossType.Full)
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
