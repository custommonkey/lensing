name := "lensing"

version := "1.0"

scalaVersion := "2.12.3"

val monocleVersion = "1.4.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
libraryDependencies ++= Seq(
  "com.github.julien-truffaut" %% "monocle-core"    % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-macro"   % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-law"     % monocleVersion % "test",
  "com.github.julien-truffaut" %% "monocle-refined" % monocleVersion
)

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-MF"

libraryDependencies += "eu.timepit" %% "refined" % "0.8.2"
