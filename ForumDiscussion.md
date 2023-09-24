My Vacuum Agent was given the Bump percept.

It took me a while to settle on this design, but it seems to work well in all
environments that I have run it through.

My vacuum keeps a few sets on hand to track various categories of cells. It has
a `knownCells` set that contains all cells that the vacuum knows about. There
are two subsets of `knownCells`, `visitedCells` and `blockedCells` that contain
cells the vacuum has been in and cells it knows are blocked, respectively. There
is a priority queue called `frontier` that holds any cells that are members of
`knownCells` and are NOT members of `visitedCells` or `blockedCells`.

The vacuum agent chooses a goal cell by removing the next element from the
`froniter` and then uses a depth-first search through `knownCells` that are not
members of `blockedCells` to build a path from the current cell to the goal. Once
the `fontier` is empty, the agent returns the STOP action to end the simulation.

My current design performs far better than the RandomAgent on all tested
environments so far.
