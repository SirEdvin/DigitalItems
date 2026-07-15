# Repository Guidelines

## Project Structure

DigitalItems is a Gradle multi-project repository. Subprojects live under `projects/`:

- `core`: loader-independent Minecraft mod code and shared test-mod sources.
- `forge`: Forge implementation, generated resources, and Forge GameTests.
- `fabric`: Fabric implementation, generated resources, and Fabric GameTests.
- `typed-peripheral-digitalitems`: publishable TypeScriptToLua peripheral API package.
- `typescript-tests`: TypeScript GameTest programs compiled to Lua and included in the shared test mod.

The TypeScript tests consume `typed-peripheral-digitalitems` through a local npm file dependency. Gradle compiles the package before installing the test project's npm dependencies.

## Contribution Workflow

Every new task must be implemented on a dedicated branch and submitted as a GitHub pull request. Do not commit task changes directly to the base branch.
