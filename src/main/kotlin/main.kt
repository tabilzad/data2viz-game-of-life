import io.data2viz.color.Color
import io.data2viz.color.Colors
import io.data2viz.geom.size
import io.data2viz.random.*
import io.data2viz.viz.*


const val vizSize = 1280.0 + 130
val step = 10.0;
fun Double.toBoolean(): Boolean{
    return when {
        this < 0.5 -> false
        else -> true
    }
}

data class Walker(
    var x: Int, var y: Int,
    var alive: Boolean = RandomDistribution.exponential(1.0 / 20)().toBoolean(),
    var gen: Int = 0
)

fun main() {

    val points = (0..150).map { x ->
        (0..100).map { y ->
            Walker((x), (y))
        }
    }

    fun updateBoard() {
        // println("update")
        val workBoard = points.map {
            it.toList();
        }.toTypedArray()

        workBoard.forEachIndexed { x: Int, xv: List<Walker> ->
            xv.forEachIndexed { y, yv ->
                val neighborsAlive = workBoard.countNeighborsAliveFrom(x, y)
                if (workBoard[x][y].alive) {
                    when {
                        neighborsAlive > 5-> {
                            points[x][y].apply {
                                gen++;
                                alive = RandomDistribution.uniform(0.0,2.0)().toBoolean()
                            }
                        }
                        neighborsAlive < 2 || neighborsAlive > 3 -> {
                            points[x][y].apply {
                                gen++;
                                alive = false
                            }
                        }
                    }
                } else {
                    if (neighborsAlive == 3) {
                        points[x][y].apply {
                            gen++
                            alive = true
                        }
                    }
                }
            }
        }
    }

    viz {
        println("Initialized")
        size = size(vizSize * 2, vizSize)
        val walkers = points.map {
            it.map { walker ->
                rect {
                    fill = Colors.rgb(40, 40, 40)
                    width = step
                    height = step
                    x = walker.x * step
                    y = walker.y * step
                } to walker
            }
        }.flatten()
        animation {// updates frames 60fps loop
            walkers.forEach {(node, w) ->
                val currentColor = node.fill as Color
                node.fill = when {
                    w.alive -> Colors.rgb(currentColor.r + w.gen*20 % 255, 100, 0)
                    else -> Colors.rgb(40,40, currentColor.g + w.gen *10 % 255)
                }
            }
            updateBoard()
        }
    }.bindRendererOn("viz")
}


fun Array<List<Walker>>.countNeighborsAliveFrom(x: Int, y: Int): Int {

    val deltas = listOf(
        Walker(0, 1),
        Walker(1, 0),
        Walker(1, 1),
        Walker(-1, -1),
        Walker(-1, 0),
        Walker(0, -1),
        Walker(-1, 1),
        Walker(1, -1)
    )

    val possibleNeighbors = deltas.filter {
        !ifOutside(x + it.y, y + it.x)
    }

    val neighbors = possibleNeighbors.map {
        this[x + it.y][y + it.x]
    }
    return neighbors.count { it.alive }
}

fun Array<List<Walker>>.ifOutside(x: Int, y: Int) = x < 0 || x > size - 1 || y < 0 || y > this[0].size - 1