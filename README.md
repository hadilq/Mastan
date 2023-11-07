# Mastan
This is a passive aggressive refactor of [Firefly/Ebony](https://github.com/digitalbuddha/Firefly).
Notice, the refactoring never has a straight path,
rather it's a zig-zag path that you need to keep the functionality of the app,
therefore, each commit in this path must be compilable and the app must works as expected.
In fact, refactoring an already existing project is chosen for this project to show how it should be done,
instead of starting a new project.
In this way we can add more value, by building upon previous projects.

# Naming
Mastan in Farsi's poems literally means cool crowd, which we take it here, however,
it also means drunk people, so who knows! By the way, it's reflected in the icon!
Also the rainbow in the icon is referring to diversity of all kind.
 - Gender, including men, women, and the spectrum between.
 - Races, including all spectrum.
 - Believe systems, including all spectrum.
 - You get it, including all spectrum!

# Road map
 - Create a light root module.
 - Modularize.
 - Enforcing flat dependency graph by using DIP. All modules must have Input, Output contracts.
 - Apply Logic Tree Architect(LTA) that already explained in [The Molecule Sample](https://github.com/hadilq/molecule-sample-app) to fix the scoping problem of the legacy architect.
 - Handle the errors properly.
 - Complete the features.
 - Make it multiplatform.
